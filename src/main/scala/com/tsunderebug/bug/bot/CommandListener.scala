package com.tsunderebug.bug.bot

import java.time.ZoneOffset

import akka.actor.{ActorRef, ActorSystem}
import com.tsunderebug.bug.bot.command.{CommandActor, CommandMessage}
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

object CommandListener extends IListener[MessageReceivedEvent] {

  val system: ActorSystem = ActorSystem("bugCommandListener")
  val commandActor: ActorRef = system.actorOf(CommandActor.props)

  override def handle(e: MessageReceivedEvent): Unit = {
    commandActor ! CommandMessage(e.getAuthor, e.getMessage, e.getMessage.getGuild, e.getMessage.getTimestamp.toInstant(ZoneOffset.UTC).toEpochMilli)
  }

}
