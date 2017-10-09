package com.tsunderebug.bug.config

case class GuildConfig(prefixes: Array[String] = Array("!"), banSelfbots: Boolean = true, modLogChannelID: Option[Long] = None)
