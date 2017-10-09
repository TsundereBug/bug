package com.tsunderebug.bug.bot.command

import sx.blah.discord.handle.obj.{IGuild, IMessage, IUser}

final case class CommandMessage(author: IUser, message: IMessage, guild: IGuild, time: Long)