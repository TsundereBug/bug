package com.tsunderebug.bug.infraction

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.tsunderebug.bug._
import com.tsunderebug.bug.config.{Database, GuildConfig}
import sx.blah.discord.handle.obj.{IGuild, IUser}

case class Infraction(uuid: UUID, target: Long, submitter: Long, guild: Long, infType: InfractionType, reason: String, submitted: Long, onClear: () => Unit) {

  def this(uuid: UUID, target: IUser, submitter: IUser, guild: IGuild, infType: InfractionType, reason: String, submitted: Long, onClear: () => Unit) {
    this(uuid, target.getLongID, submitter.getLongID, guild.getLongID, infType, reason, submitted, onClear)
  }

  def clear(): Unit = {
    val s = Database.conn.prepareStatement("DELETE FROM infractions WHERE infid = ?")
    s.setObject(1, uuid)
    s.execute()
    onClear()
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
    addInfraction(Infraction(UUID.randomUUID(), id, submitter.getLongID, g.getLongID, PermBan, r, System.currentTimeMillis(), () => {(() => g.pardonUser(id)).r}))
  }

  def banUser(g: IGuild, target: IUser, submitter: IUser, r: String): Unit = {
    banID(g, target.getLongID, submitter, r)
  }

  def warnUser(g: IGuild, target: IUser, submitter: IUser, r: String): Unit = {
    target.getOrCreatePMChannel().sendMessage(s"You have been warned by ${nameFormat(submitter.getLongID)} for:\n```\n$r\n```")
    addInfraction(new Infraction(UUID.randomUUID(), target, submitter, g, Warn, r, System.currentTimeMillis(), () => {}))
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
    s.setString(6, i.reason)
    s.setTimestamp(7, Timestamp.from(Instant.ofEpochMilli(i.submitted)))
    s.execute()
    Database.guildConfig(i.guild) match {
      case GuildConfig(_, _, v) =>
        v.get("mod_channel") match {
          case Some(id) => (() => Main.client.getChannelByID(id.toLong).sendMessage(s"${i.infType.emoji} ${i.uuid}\n${nameFormat(i.target)} was **${i.infType.name}** by ${nameFormat(i.submitter)} for:\n```\n${i.reason}\n```")).r
        }
      case _ =>
    }
  }

  def apply(uuid: UUID): Option[Infraction] = {
    val s = Database.conn.prepareCall("SELECT * FROM infractions WHERE infid = ?;")
    s.setObject(1, uuid)
    s.execute()
    val r = s.getResultSet
    if(r.next()) {
      val t = r.getLong(2)
      val s = r.getLong(3)
      val g = r.getLong(4)
      val i = InfractionType(r.getString(5).charAt(0))
      val m = r.getString(6)
      val z = r.getTimestamp(7)
      Some(Infraction(uuid, t, s, g, i.getOrElse(Warn), m, z.getTime, i match {
        case Some(TempBan) | Some(PermBan) => () => {(() => Main.client.getGuildByID(g).pardonUser(t)).r}
        case Some(TempMute) | Some(PermMute) => () => {} // TODO
        case Some(Kick) => () => {}
        case Some(Warn) => () => {}
      }))
    } else {
      None
    }
  }

}
