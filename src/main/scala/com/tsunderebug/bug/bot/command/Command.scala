package com.tsunderebug.bug.bot.command

import scala.util.matching.Regex

case class Command(reg: Regex, valid: (CommandMessage, Array[String]) => Boolean, run: (CommandMessage, Array[String]) => Unit, subs: Seq[Command] = Seq())