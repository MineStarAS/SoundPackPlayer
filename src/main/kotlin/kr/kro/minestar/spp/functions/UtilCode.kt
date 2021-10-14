package kr.kro.minestar.spp.functions

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun String.toServer() {
    Bukkit.broadcastMessage(this)
}

fun String.toPlayer(player: Player) {
    player.sendMessage(this)
}

fun String.toPlayers(players: Collection<Player>) {
    for (player in players) player.sendMessage(this)
}

fun String.toComponent(): Component {
    return Component.text(this)
}

fun String.setUnderBar(): String {
    return this.replace(' ', '_')
}

fun String.removeUnderBar(): String {
    return this.replace('_', ' ')
}

fun Int.toTick(): Int {
    return this * 20
}

fun Int.toTime(): String {
    val min = this / 60
    val sec = this - (min * 60)

    return if (sec < 10) "$min : 0$sec"
    else "$min : $sec"
}