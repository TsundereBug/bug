package com.tsunderebug.bug.bot.command

import akka.actor.{Actor, Props}
import com.tsunderebug.bug.Main
import com.tsunderebug.bug.infraction.Infraction
import sx.blah.discord.handle.obj.Permissions

import scala.collection.JavaConverters._

class CommandActor extends Actor {

  override def receive: Receive = {
    case CommandMessage(author, message, guild, time) if message.getEmbeds.asScala.forall(_.getType == "link") && !author.isBot =>

    case CommandMessage(author, _, guild, time) if !author.isBot =>
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
