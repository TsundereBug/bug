package com.tsunderebug.bug.config

import java.lang.reflect.Type
import java.sql.{Connection, Timestamp}

import com.google.gson.{Gson, GsonBuilder, InstanceCreator}
import com.tsunderebug.bug.Main
import org.postgresql.ds.PGConnectionPoolDataSource
import org.postgresql.util.PSQLException
import sx.blah.discord.handle.obj.IGuild

import scala.collection.mutable

object Database {

  lazy val conn: Connection = {
    val s = new PGConnectionPoolDataSource
    s.setServerName("localhost:5432")
    s.setDatabaseName(Main.properties.getProperty("dbname"))
    s.setUser(Main.properties.getProperty("dbuser"))
    s.setPassword(Main.properties.getProperty("dbpass"))
    s.getConnection
  }

  def init(): Unit = {
    conn.prepareStatement(
      """
        |CREATE TABLE IF NOT EXISTS infractions(
        |  infid UUID,
        |  targetid BIGINT,
        |  submitterid BIGINT,
        |  guildid BIGINT,
        |  type CHAR(1),
        |  message TEXT,
        |  submitted TIMESTAMP
        |);
      """.stripMargin).execute()
    conn.prepareStatement(
      """
        |CREATE TABLE IF NOT EXISTS gconfig(
        |  gid BIGINT,
        |  configj TEXT
        |);
      """.stripMargin).execute()
    conn.prepareStatement(
      """
        |CREATE TABLE IF NOT EXISTS tempbans(
        |  gid BIGINT,
        |  uid BIGINT,
        |  unb TIMESTAMP
        |);
      """.stripMargin).execute()
  }

  def guildConfig(g: Long): GuildConfig = {
    val s = conn.prepareStatement("SELECT configj FROM gconfig WHERE gid = ?;")
    s.setLong(1, g)
    s.execute()
    val r = s.getResultSet
    r.next()
    Option(try {
      r.getString("configj")
    } catch {
      case _: PSQLException => null
    }) match {
      case Some(j) =>
        new GsonBuilder().registerTypeAdapter(classOf[Option[_]], new InstanceCreator[Option[_]] {
          override def createInstance(t: Type): Option[_] = None
        }).create().fromJson(j, classOf[GuildConfig])
      case None =>
        val c = GuildConfig()
        val s = conn.prepareStatement(
          """
             |INSERT INTO gconfig
             |VALUES (?, ?);
          """.stripMargin)
        s.setLong(1, g)
        s.setString(2, new Gson().toJson(c))
        s.execute()
        c
    }
  }

  def setGuildConfig(g: IGuild, c: GuildConfig): Unit = {
    val s = conn.prepareStatement(
      """
        |INSERT INTO gconfig
        |VALUES (?, ?);
      """.stripMargin)
    s.setLong(1, g.getLongID)
    s.setString(2, new Gson().toJson(c))
    s.execute()
  }

  def tempbans: Array[(Long, Long, Timestamp)] = {
    val s = conn.prepareStatement("SELECT * FROM tempbans;")
    s.execute()
    val r = s.getResultSet
    val b: mutable.Seq[(Long, Long, Timestamp)] = mutable.Seq()
    while (r.next()) {
      b :+ (r.getLong("gid"), r.getLong("uid"), r.getTimestamp("unb"))
    }
    b.toArray
  }

}
