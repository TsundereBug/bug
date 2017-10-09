package com.tsunderebug.bug.bot

import java.time.ZoneOffset

import akka.actor.{ActorRef, ActorSystem}
import com.tsunderebug.bug.bot.command.{CommandActor, CommandMessage}
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent

import scala.collection.JavaConverters._

object CommandListener extends IListener[MessageEvent] {

  val system: ActorSystem = ActorSystem("bugCommandListener")
  val commandActor: ActorRef = system.actorOf(CommandActor.props)

  override def handle(e: MessageEvent): Unit = {
    commandActor ! CommandMessage(e.getAuthor, e.getMessage.getContent, e.getMessage.getGuild, e.getMessage.getEmbeds.asScala, e.getMessage.getTimestamp.toInstant(ZoneOffset.UTC).toEpochMilli)
  }

}
