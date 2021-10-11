package kr.kro.minestar.spp

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
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

fun Int.toTick(): Int {
    return this * 20
}