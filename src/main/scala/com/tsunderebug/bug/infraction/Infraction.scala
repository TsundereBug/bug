package com.tsunderebug.bug.infraction

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.tsunderebug.bug._
import com.tsunderebug.bug.config.{Database, GuildConfig}
import sx.blah.discord.handle.obj.{IGuild, IUser}

case class Infraction(uuid: UUID, target: Long, submitter: Long, guild: Long, infType: InfractionType, reason: String, submitted: Long) {

  def this(uuid: UUID, target: IUser, submitter: IUser, guild: IGuild, infType: InfractionType, reason: String, submitted: Long) {
    this(uuid, target.getLongID, submitter.getLongID, guild.getLongID, infType, reason, submitted)
  }

}

object Infraction {

  def nameFormat(ul: Long): String = {
    Option(Main.client.getUserByID(ul)) match {
      case Some(u) =>
        s"`${u.getName}#${u.getDiscriminator} (${u.getLongID})`"
      case None =>
        s"`$ul`"
    }
  }

  def banID(g: IGuild, id: Long, submitter: IUser, r: String): Unit = {
    (() => g.banUser(id, r)).r
    addInfraction(Infraction(UUID.randomUUID(), id, submitter.getLongID, g.getLongID, PermBan, r, System.currentTimeMillis()))
  }

  def banUser(g: IGuild, target: IUser, submitter: IUser, r: String): Unit = {
    banID(g, target.getLongID, submitter, r)
  }

  def warnUser(g: IGuild, target: IUser, submitter: IUser, r: String): Unit = {
    target.getOrCreatePMChannel().sendMessage(s"You have been warned by ${nameFormat(submitter.getLongID)} for:\n```\n$r\n```")
    addInfraction(new Infraction(UUID.randomUUID(), target, submitter, g, Warn, r, System.currentTimeMillis()))
  }

  def addInfraction(i: Infraction): Unit = {
    val conn = Database.conn
    val s = conn.prepareStatement(
      """
        |INSERT INTO infractions
        |VALUES (?, ?, ?, ?, ?, ?, ?)
      """.stripMargin)
    s.setObject(1, i.uuid)
    s.setLong(2, i.target)
    s.setLong(3, i.submitter)
    s.setLong(4, i.guild)
    s.setObject(5, i.infType.ident.charAt(0))
    s.setObject(6, i.reason)
    s.setTimestamp(7, Timestamp.from(Instant.ofEpochMilli(i.submitted)))
    s.execute()
    Database.guildConfig(i.guild) match {
      case GuildConfig(_, _, Some(c)) =>
        Main.client.getChannelByID(c).sendMessage(s"${i.infType.emoji} ${nameFormat(i.target)} was **${i.infType.name}** by ${nameFormat(i.submitter)} for:\n```\n${i.reason}\n```")
      case _ =>
    }
  }

}
