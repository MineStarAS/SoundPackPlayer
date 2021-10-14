package kr.kro.minestar.spp

import kr.kro.minestar.spp.functions.SppClass
import kr.kro.minestar.spp.functions.removeUnderBar
import kr.kro.minestar.spp.functions.setUnderBar
import kr.kro.minestar.spp.functions.toPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CMD : CommandExecutor, TabCompleter {
    private val prefix = Main.prefix
    private val args0 = listOf("play", "playlist", "shuffle", "loop", "select", "add", "remove")
    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) {
        } else {
            val option = Main.poMap[player]!!
            when (args[0]) {
                args0[0] -> option.togglePlay()
                args0[1] -> option.togglePlayType()
                args0[2] -> option.toggleShuffle()
                args0[3] -> option.toggleLoop()

                args0[4] -> {
                    if (args.size == 2) option.selectSound(args[1].removeUnderBar())
                    else "$prefix §c/${cmd.name} ${args[0]} <SoundName>".toPlayer(player)
                }
                args0[5] -> {
                    when (args.size) {
                        1 -> option.addPlayList()
                        2 -> option.addPlayList(args[1].removeUnderBar())
                        else -> "$prefix §c/${cmd.name} ${args[0]} [SoundName]".toPlayer(player)
                    }
                }
                args0[6] -> {
                    when (args.size) {
                        1 -> option.removePlayList()
                        2 -> option.removePlayList(args[1].removeUnderBar())
                        else -> "$prefix §c/${cmd.name} ${args[0]} [SoundName]".toPlayer(player)
                    }
                }
            }
        }
        return false
    }

    override fun onTabComplete(player: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        if (args.size == 1) for (s in args0) if (s.contains(args[0])) list.add(s)
        if (args.size >= 2) when (args[0]) {
            args0[4], args0[5] -> for (s in SppClass().getSubtitleList()) if (s.setUnderBar().contains(args[1])) list.add(s.setUnderBar())
            args0[6] -> for (s in Main.poMap[player]!!.getPlayList()) if (s.setUnderBar().contains(args[1])) list.add(s.setUnderBar())
        }

        return list
    }

}