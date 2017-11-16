package com.tsunderebug.bug.bot.command

import com.tsunderebug.bug.bot.command.moderation.{BanCommand, InfractionCommands, WarnCommand}
import com.tsunderebug.bug.config.Database
import sx.blah.discord.handle.obj.Permissions

object Commands {

  lazy val list: Seq[Command] = Seq(
    BanCommand,
    WarnCommand,
    Command(
      """c(?:o?n)?(?:fg?)?""".r, (m, _) => m.author.getPermissionsForGuild(m.guild).contains(Permissions.MANAGE_SERVER),
      (m, a) => Database.guildConfig(m.guild.getLongID).configCommand(m, a)
    ),
    Command(
      """eval""".r, (m, _) => m.author.getLongID == 192322936219238400l, Eval.crystal
    ),
    InfractionCommands.Info
  )

  def findCommand(m: CommandMessage, afterPref: String): (Option[Command], Array[String]) = {
    val search = afterPref.split("""\s+""")
    list.map(searchCommand(m, _, search)).find(_._1.isDefined) match {
      case Some((c, a)) => (c, a)
      case None => (None, Array())
    }
  }

  def searchCommand(m: CommandMessage, command: Command, toFind: Array[String]): (Option[Command], Array[String]) = {
    command.subs.find((c) => toFind(1) match {
      case c.reg() if c.valid(m, toFind.drop(1)) || (c.subs.isEmpty || c.subs.forall(!_.valid(m, toFind.drop(2)))) => true
      case _ => false
    }) match {
      case None =>
        toFind(0) match {
          case command.reg() if command.valid(m, toFind.drop(1)) =>
            (Some(command), toFind.drop(1))
          case _ => (None, Array())
        }
      case Some(c) =>
        searchCommand(m, c, toFind.drop(1))
    }
  }

}
