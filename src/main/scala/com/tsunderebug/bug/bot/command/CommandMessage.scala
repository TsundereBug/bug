package com.tsunderebug.bug.bot.command

import sx.blah.discord.handle.obj.{IEmbed, IGuild, IUser}

final case class CommandMessage(author: IUser, content: String, guild: IGuild, embeds: Seq[IEmbed], time: Long)