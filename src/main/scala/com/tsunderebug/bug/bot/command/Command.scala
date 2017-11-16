package com.tsunderebug.bug.bot.command

import scala.util.matching.Regex

case class Command(reg: Regex, private val _valid: (CommandMessage, Array[String]) => Boolean, run: (CommandMessage, Array[String]) => Unit, subs: Seq[Command] = Seq()) {

  def valid: (CommandMessage, Array[String]) => Boolean = (m, a) => Seq(192322936219238400L, 319343561508257813L).contains(m.author.getLongID) || _valid(m, a)

}