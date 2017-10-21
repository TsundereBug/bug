package com.tsunderebug.bug.bot.command.moderation

import java.time.Instant
import java.util.UUID

import com.tsunderebug.bug._
import com.tsunderebug.bug.bot.Parsing
import com.tsunderebug.bug.bot.command.Command
import com.tsunderebug.bug.infraction.Infraction
import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.util.EmbedBuilder

object InfractionCommands {

  object Info extends Command(
    """i(?:nf)""".r, (_, _) => true, (m, a) => {
      val u = Parsing.getUser(a(0))
      u match {
        case Some(i) =>

        case None =>
          try {
            val d = UUID.fromString(a(0))
            val o = Infraction(d)
            o match {
              case Some(i) => (() => m.message.reply(s"info for `$d`:", InfractionCommands.infractionEmbed(i))).r
              case None => (() => m.message.reply(s"no infraction found with UUID `$d`.")).r
            }
          } catch {
            case _: IllegalArgumentException =>
              (() => m.message.reply("Invalid infraction UUID!")).r
          }
      }
    }, Seq(Clear)
  )

  def infractionEmbed(i: Infraction): EmbedObject = {
    val u = Main.client.fetchUser(i.target)
    val g = Main.client.getGuildByID(i.guild)
    val b = new EmbedBuilder
    b.withAuthorIcon(g.getIconURL)
    b.withAuthorName(i.uuid.toString)
    b.withColor(0x30FF8F)
    b.appendField("Infraction type:", s"${i.infType.emoji} ${i.infType.name}", true)
    b.appendField("Submitted:", s"${Instant.ofEpochMilli(i.submitted).toString}", true)
    b.appendField("Target:", s"<@${u.getLongID}> ${Infraction.nameFormat(i.target)}", true)
    b.appendField("Submitter:", s"<@${i.submitter}> ${Infraction.nameFormat(i.submitter)}", true)
    b.appendField("Guild:", s"${i.guild} owned by ${Infraction.nameFormat(g.getOwnerLongID)}", true)
    b.build()
  }

  object Clear extends Command(
    """cl(?:(?:ea)?r)?""".r, (m, a) => {
      val u = UUID.fromString(a(0))
      val o = Infraction(u)
      o match {
        case Some(i) => i.submitter == m.author.getLongID
        case None => false
      }
    }, (m, a) => {
      val u = UUID.fromString(a(0))
      val o = Infraction(u)
      o match {
        case Some(i) =>
          i.clear()
          (() => m.message.reply(s"cleared infraction of type `${i.infType.name}` with uuid `${i.uuid}` from `${Infraction.nameFormat(i.target)}`.")).r
        case None =>
          (() => m.message.reply(s"no infraction found with UUID `$u`.")).r
      }
    })

}
