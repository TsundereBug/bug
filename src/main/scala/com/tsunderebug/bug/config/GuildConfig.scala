package com.tsunderebug.bug.config

import com.tsunderebug.bug.bot.command.CommandMessage
import com.tsunderebug.bug._

import scala.util.matching.Regex

case class GuildConfig(arrays: Map[String, Array[String]] = Map(
  "prefixes" -> Array("<@316763443816300545> ", "<@!316763443816300545> ")
), maps: Map[String, Map[String, String]] = Map(), values: Map[String, String] = Map(
  "ban_selfbots" -> "true"
)) {

  def configCommand(m: CommandMessage, a: Array[String]): Unit = {
    Database.setGuildConfig(m.guild, a.mkString(" ") match {
      case GuildConfig.setReg(k, _, v) =>
        (() => m.message.reply(s"set `$k` equal to `$v`.")).r
        value_=(k, Some(v))
      case GuildConfig.addReg(k, _, v) =>
        (array(k), map(k)) match {
          case (None, Some(h)) =>
            v match {
              case GuildConfig.kvReg(mk, mv) =>
                (() => m.message.reply(s"added the key/value pair `$mk => $mv` to `$h`.")).r
                map_=(k, Some(h + (mk -> mv)))
              case _ =>
                (() => m.message.reply("input does not match `key => value`!")).r
                this
            }
          case (Some(q), None) =>
            (() => m.message.reply(s"added `$v` to `$k`.")).r
            array_=(k, Some(q :+ v))
          case (Some(_), Some(_)) =>
            (() => m.message.reply(s"ambiguous cases, array `$k` and map `$k`.")).r
            this
        }
      case GuildConfig.remReg(k, _, v) =>
        (array(k), map(k)) match {
          case (None, Some(h)) =>
            v match {
              case GuildConfig.kvReg(mk, mv) =>
                (() => m.message.reply(s"removed the key/value pair `$mk => $mv` from `$h`.")).r
                map_=(k, Some(h - mk))
              case _ =>
                (() => m.message.reply("input does not match `key => value`!")).r
                this
            }
          case (Some(q), None) =>
            (() => m.message.reply(s"removed `$v` from `$k`.")).r
            array_=(k, Some(q.filter(_ != v)))
          case (Some(_), Some(_)) =>
            (() => m.message.reply(s"ambiguous cases, array `$k` and map `$k`.")).r
            this
        }
    })
  }

  def array(k: String): Option[Array[String]] = {
    arrays.get(k)
  }

  def map(k: String): Option[Map[String, String]] = {
    maps.get(k)
  }

  def value(k: String): Option[String] = {
    values.get(k)
  }

  def array_=(k: String, v: Option[Array[String]]): GuildConfig = {
    v match {
      case Some(a) =>
        copy(arrays = arrays + (k -> a))
      case None =>
        copy(arrays = arrays - k)
    }
  }

  def map_=(k: String, v: Option[Map[String, String]]): GuildConfig = {
    v match {
      case Some(m) =>
        copy(maps = maps + (k -> m))
      case None =>
        copy(maps = maps - k)
    }
  }

  def value_=(k: String, v: Option[String]): GuildConfig = {
    v match {
      case Some(s) =>
        copy(values = values + (k -> s))
      case None =>
        copy(values = values - k)
    }
  }

}

object GuildConfig {

  lazy val setReg: Regex = """(\w+)\s+(?:s(?:e?t)?|=)\s+(`?)(.+)\2""".r
  lazy val addReg: Regex = """(\w+)\s+(?:a(?:dd)?|\+)\s+(`?)(.+)\2""".r
  lazy val remReg: Regex = """(\w+)\s+(?:r(?:e?m)?|-)\s+(`?)(.+)\2""".r
  lazy val kvReg: Regex = """\(?(\w+)\s*[-=]*>\s*(.+)\)?""".r
  lazy val prefReg: Regex = """p(?:r(?:e(?:f(?:i?x(?:e?s)?)?)?)?)?""".r

}
