package com.tsunderebug.bug.bot.command

import akka.actor.{Actor, Props}
import com.tsunderebug.bug.Main
import com.tsunderebug.bug.config.Database
import com.tsunderebug.bug.infraction.Infraction
import sx.blah.discord.handle.obj.Permissions

import scala.collection.JavaConverters._

class CommandActor extends Actor {

  override def receive: Receive = {
    case CommandMessage(author, message, guild, time) if message.getEmbeds.asScala.forall((e) => !(e.getType == "rich" && e.getFooter.getIconUrl != "https://abs.twimg.com/icons/apple-touch-icon-192x192.png")) && !author.isBot =>
      val mo = CommandMessage(author, message, guild, time)
      val gc = Database.guildConfig(guild.getLongID)
      val vprefs = gc.array("prefixes").getOrElse(Array("<@316763443816300545> ", "<@!316763443816300545> ")).filter(message.getContent.startsWith).map(_.length)
      if(!vprefs.isEmpty) {
        val pref = message.getContent.drop(vprefs.max)
        Commands.findCommand(mo, pref) match {
          case (Some(c), a) =>
            c.run(mo, a)
          case (None, _) =>
        }
      }
    case CommandMessage(author, _, guild, _) if !author.isBot =>
      if(Main.client.getOurUser.getPermissionsForGuild(guild).contains(Permissions.BAN) && Main.client.getOurUser.getRolesForGuild(guild).asScala.map(_.getPosition).max > author.getRolesForGuild(guild).asScala.map(_.getPosition).max) {
        Infraction.banUser(guild, author, Main.client.getOurUser, "Using a selfbot.")
      } else {
        Infraction.warnUser(guild, author, Main.client.getOurUser, "Using a selfbot in a server that disallows it.")
      }
    case CommandMessage(_, _, _, _) =>
  }

}

object CommandActor {

  def props: Props = Props[CommandActor]

}
