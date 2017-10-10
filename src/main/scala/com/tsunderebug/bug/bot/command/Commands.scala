package com.tsunderebug.bug.bot.command

import com.tsunderebug.bug.Main
import com.tsunderebug.bug.bot.Parsing
import com.tsunderebug.bug.infraction.Infraction
import sx.blah.discord.handle.obj.Permissions

import scala.collection.JavaConverters._

object Commands {

  lazy val list: Seq[Command] = Seq(
    Command(
      """!b(?:a?n)?""".r, (m, a) => {
        val u = Parsing.getUser(a(0))
        m.author.getPermissionsForGuild(m.guild).contains(Permissions.BAN) && Main.client.getOurUser.getPermissionsForGuild(m.guild).contains(Permissions.BAN) &&
          (
            u.isEmpty || !m.guild.getUsers.contains(u.get) ||
            m.author.getRolesForGuild(m.guild).asScala.map(_.getPosition).max > u.get.getRolesForGuild(m.guild).asScala.map(_.getPosition).max
          )
      }, (m, a) => {
        val u = Parsing.getUser(a(0))
        val r = a.drop(1).mkString(" ")
        u match {
          case Some(uo) =>
            Infraction.banUser(m.guild, uo, m.author, r)
            m.message.reply(s"banned ${Infraction.nameFormat(uo.getLongID)} for:```\n$r\n```")
          case None =>
            Infraction.banID(m.guild, a(0).toLong, m.author, r)
            m.message.reply(s"banned ${Infraction.nameFormat(a(0).toLong)} for:```\n$r\n```")
        }
      })
  )

  def findCommand(m: CommandMessage, afterPref: String): (Option[Command], Array[String]) = {
    val search = afterPref.split("""\s+""")
    list.map(searchCommand(m, _, search)).filter(_._1.isDefined).head
  }

  def searchCommand(m: CommandMessage, command: Command, toFind: Array[String]): (Option[Command], Array[String]) = {
    command.subs.find((c) => toFind(0) match {
      case c.reg() => true
      case _ if command.valid(m, toFind.drop(1)) => false
    }) match {
      case None =>
        (Some(command), toFind.drop(1))
      case Some(c) =>
        searchCommand(m, c, toFind.drop(1))
    }
  }

}
