package com.tsunderebug.bug

import java.sql.{Connection, DriverManager}
import java.util.Properties

import com.google.gson.Gson
import sx.blah.discord.handle.obj.IGuild

object Database {

  lazy val connectionProperties: Properties = {
    val c = new Properties()
    connectionProperties.setProperty("user", Main.properties.getProperty("dbuser"))
    connectionProperties.setProperty("password", Main.properties.getProperty("dbpass"))
    c
  }
  lazy val conn: Connection = DriverManager.getConnection(s"jdbc:postgres://localhost/${Main.properties.getProperty("dbname")}", connectionProperties)

  def init(): Unit = {
    conn.prepareStatement(
      """
        |CREATE TABLE IF NOT EXISTS infractions(
        |  infid UUID,
        |  targetid BIGINT,
        |  submitterid BIGINT,
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
  }

  def guildConfig(g: IGuild): GuildConfig = {
    val call = conn.prepareCall(s"SELECT * FROM gconfig WHERE gid = ${g.getLongID};")
    Option(call.getString("configj")) match {
      case Some(j) =>
        new Gson().fromJson(j, classOf[GuildConfig])
      case None =>
        val c = GuildConfig()
        conn.prepareStatement(
          s"""
            |INSERT INTO gconfig
            |VALUES (${g.getLongID}, ${new Gson().toJson(c)});
          """.stripMargin).execute()
        c
    }
  }

}
