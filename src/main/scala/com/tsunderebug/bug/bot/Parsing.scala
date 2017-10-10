package com.tsunderebug.bug.bot

import com.tsunderebug.bug.Main
import sx.blah.discord.handle.obj.IUser

import scala.util.matching.Regex

object Parsing {

  val userIDReg: Regex = """(\d+)""".r
  val userMentionReg: Regex = """<@!?(\d+)>""".r

  def getUser(s: String): Option[IUser] = {
    s match {
      case userIDReg(id) =>
        Option(Main.client.getUserByID(id.toLong))
      case userMentionReg(id) =>
        Option(Main.client.getUserByID(id.toLong))
      case _ =>
        None
    }
  }

}
