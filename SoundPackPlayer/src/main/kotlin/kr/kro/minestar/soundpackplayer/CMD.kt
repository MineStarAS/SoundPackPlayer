package kr.kro.minestar.soundpackplayer

import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.functions.gui.SoundPlayerGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CMD : CommandExecutor {

    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        SoundPlayerGUI(DataClass.soundPlayer(player))
        return false
    }
}