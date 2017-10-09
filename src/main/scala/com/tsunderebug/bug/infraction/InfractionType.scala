package com.tsunderebug.bug.infraction

sealed class InfractionType(val ident: String, val emoji: String, val name: String)
class Ban(perm: Boolean) extends InfractionType(if(perm) "B" else "b", "\uD83D\uDD28", if(perm) "Banned" else "Temp-Banned")
class Mute(perm: Boolean) extends InfractionType(if(perm) "M" else "m", "\uD83D\uDC62", if(perm) "Muted" else "Temp-Muted")
case object PermBan extends Ban(true)
case object TempBan extends Ban(false)
case object Kick extends InfractionType("K", "⚠", "Kicked")
case object PermMute extends Mute(true)
case object TempMute extends Mute(false)
case object Warn extends InfractionType("W", "\uD83D\uDE36", "Warned")