package com.tsunderebug.bug.bot.command

import java.io.{BufferedReader, File, InputStreamReader}
import java.util.concurrent.TimeUnit

import com.tsunderebug.bug._
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.RequestBuffer.RequestFuture

import scala.util.matching.Regex

object Eval {

  val evalReg: Regex = """```crystal\n((?:.|\n)+)\n```|((?:.|\n)+)""".r

  def crystal(m: CommandMessage, a: Array[String]): Unit = {
    val midt = (() => m.message.getChannel.sendMessage("```\nCompiling...\n```")).r
    val l = m.message.getContent.substring(m.message.getContent.indexOf(a(0)))
    l match {
      case evalReg(c, null) => doCrystalEval(c, midt)
      case evalReg(null, c) => doCrystalEval(c, midt)
      case _ =>
        midt.get.edit(s"Could not find crystal code in:```\n${l.replaceAll("```", "\ufeff`\ufeff`\ufeff`")}\n```")
    }
  }

  def doCrystalEval(code: String, m: RequestFuture[IMessage]): Unit = {
    val p: Process = new ProcessBuilder("crystal", "eval").directory(new File("/")).start()
    p.getOutputStream.write(code.getBytes)
    p.getOutputStream.close()
    val mess = m.get()
    var out: String = ""
    var err: String = ""
    val br: BufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream))
    val er: BufferedReader = new BufferedReader(new InputStreamReader(p.getErrorStream))
    while(p.isAlive || br.ready() || er.ready()) {
      if(br.ready()) out += "\n" + br.readLine
      if(er.ready()) err += "\n" + er.readLine
      p.waitFor(1, TimeUnit.SECONDS)
      (() => mess.edit(s"```$out\n```\nError log:```$err\n```")).r.get
    }
    br.close()
    er.close()
    (() => mess.addReaction(ReactionEmoji.of("✅"))).r
  }

}
