package com.tsunderebug.bug.config

import com.tsunderebug.bug.Main

case class GuildConfig(prefixes: Array[String] = Array("!", s"${Main.client.getOurUser.mention()} "), banSelfbots: Boolean = true, modLogChannelID: Option[Long] = None)
