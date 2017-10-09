package com.tsunderebug.bug.bot

import java.sql.Timestamp
import java.util.concurrent.{Executors, TimeUnit}

import com.tsunderebug.bug._
import com.tsunderebug.bug.config.Database
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.ReadyEvent

object ReadyListener extends IListener[ReadyEvent] {

  val scheduler = Executors.newScheduledThreadPool(10000)

  override def handle(event: ReadyEvent): Unit = {
    val tb = Database.tempbans
    tb.par.foreach((t) => {
      val gid = t._1
      val uid = t._2
      val unb = t._3
      addTempBanTimer(gid, uid, unb)
    })
  }

  def addTempBanTimer(gid: Long, uid: Long, unb: Timestamp): Unit = {
    scheduler.schedule(() => {
      (() => {
        Main.client.getGuildByID(gid).pardonUser(uid)

      }).r
    }, unb.getTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
  }

}
