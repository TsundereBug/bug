package com.tsunderebug.bug.bot.command

import akka.actor.{Actor, Props}
import com.tsunderebug.bug._

class CommandActor extends Actor {

  override def receive: Receive = {
    case CommandMessage(author, content, guild, null, time) =>
    case CommandMessage(author, _, guild, _, _) =>
      if(!author.isBot && Database.guildConfig(guild).banSelfbots) (() => guild.banUser(author, "Autodetection of selfbot")).r
  }

}

object CommandActor {

  def props: Props = Props[CommandActor]

}
