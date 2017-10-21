package com.tsunderebug.bug.bot.command.moderation

import com.tsunderebug.bug.Main
import com.tsunderebug.bug.bot.Parsing
import com.tsunderebug.bug.bot.command.Command
import com.tsunderebug.bug.infraction.Infraction
import sx.blah.discord.handle.obj.Permissions

import scala.collection.JavaConverters._

object BanCommand extends Command(
  """b(?:a?n)?""".r, (m, a) => {
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