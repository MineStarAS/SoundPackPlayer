package kr.kro.minestar.soundpackplayer.functions.events

import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.data.SoundPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object AlwaysEvent: Listener {

    @EventHandler
    fun worldMove(e: PlayerChangedWorldEvent) {
        DataClass.soundPlayer(e.player).play()
    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent){
        SoundPlayer(e.player)
    }

    @EventHandler
    fun playerQuit(e: PlayerQuitEvent){
        DataClass.unregisterSoundPlayer(DataClass.soundPlayer(e.player))
    }
}