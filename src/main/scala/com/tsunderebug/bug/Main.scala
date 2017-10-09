package com.tsunderebug.bug

import java.util.Properties

import com.tsunderebug.bug.bot.CommandListener
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}

object Main {

  val properties = new Properties()
  private val conf = getClass.getResourceAsStream("/conf.properties")
  properties.load(conf)
  conf.close()
  val client: IDiscordClient = new ClientBuilder().withToken(properties.getProperty("token")).registerListener(CommandListener).build()

  def main(args: Array[String]): Unit = {
    Database.init()
  }

}
