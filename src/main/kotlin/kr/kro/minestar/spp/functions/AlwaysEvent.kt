package kr.kro.minestar.spp.functions

import kr.kro.minestar.spp.Main
import kr.kro.minestar.spp.functions.player.PlayerOption
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AlwaysEvent: Listener {

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent){
        Main.poMap[e.player] = PlayerOption(e.player)
    }
}