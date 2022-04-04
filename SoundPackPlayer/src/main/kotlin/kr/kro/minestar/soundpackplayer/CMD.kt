package kr.kro.minestar.soundpackplayer

import kr.kro.minestar.soundpackplayer.Main.Companion.prefix
import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.functions.gui.SoundPlayerGUI
import kr.kro.minestar.utility.string.removeUnderBar
import kr.kro.minestar.utility.string.setUnderBar
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.toServer
import kr.kro.minestar.utility.unit.setFalse
import net.md_5.bungee.api.chat.TranslatableComponent
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object CMD : CommandExecutor, TabCompleter {

    private enum class Arg(private val howToUse: String, private val validSize: List<Int>) {
        icon("<Subtitle>", listOf(2)),
        ;

        fun howToUse(label: String) = "/$label $howToUse"
        fun validSize(args: Array<out String>) = validSize.contains(args.size)
    }

    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) {
            SoundPlayerGUI(DataClass.soundPlayer(player))
            return false
        }
        val arg = try {
            Arg.valueOf(args.first())
        } catch (_: Exception) {
            null
        }

        when (arg) {
            Arg.icon -> {
                if (arg.validSize(args)) return arg.howToUse(label).toPlayer(player).setFalse()
                val soundData = DataClass.soundData(args.last().removeUnderBar())
                    ?: return "$prefix §e${args.last().removeUnderBar()} §c라는 사운드를 찾을 수 없습니다.".toPlayer(player).setFalse()
                val type = player.inventory.itemInMainHand.type
                if (type == Material.AIR) return "$prefix §c아이콘으로 설정하고 싶은 아이템을 손에 들고 사용하여야 합니다.".toPlayer(player).setFalse()
                DataClass.registerIcon(soundData.subtitle, type)
                return  "$prefix §e ${args.last().removeUnderBar()} §a의 아이콘을 §e$type §a로 설정하였습니다.".toPlayer(player).setFalse()
            }
            null -> return false
        }
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        if (!p.isOp) return list

        val arg = try {
            Arg.valueOf(args.first())
        } catch (_: Exception) {
            null
        }

        if (args.size == 1) {
            for (s in Arg.values()) if (s.name.contains(args.last())) list.add(s.name)
        }
        if (args.size > 1) when (arg) {
            Arg.icon -> for (s in DataClass.soundDataList()) if (s.subtitle.setUnderBar().contains(args.last())) list.add(s.subtitle.setUnderBar())
            null -> return list
        }
        return list
    }
}