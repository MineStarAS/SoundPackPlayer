package kr.kro.minestar.soundpackplayer.api

import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.functions.gui.SoundPlayerGUI
import org.bukkit.entity.Player

object SoundPackPlayerOpenGUI {
    fun soundPlayer(player: Player) {
        SoundPlayerGUI(DataClass.soundPlayer(player))
    }
}