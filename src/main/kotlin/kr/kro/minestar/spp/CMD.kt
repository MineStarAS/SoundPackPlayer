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
    private val args0 = listOf("play", "shuffle", "loop", "select", "playlist")
    private val playlistArgs = listOf("add", "remove", "addAll", "clear")
    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) {
        } else {
            val option = Main.poMap[player]!!
            when (args[0]) {
                args0[0] -> option.togglePlay()
                args0[1] -> option.toggleShuffle()
                args0[2] -> option.toggleLoop()

                args0[3] -> {
                    if (args.size == 2) option.selectSound(args[1].removeUnderBar())
                    else "$prefix §c/${cmd.name} ${args[0]} <SoundName>".toPlayer(player)
                }

                args0[4] -> {
                    if (args.size == 1) option.togglePlayType()
                    else when (args[1]) {
                        playlistArgs[0] -> when (args.size) {
                            2 -> option.addPlayList()
                            3 -> option.addPlayList(args[2].removeUnderBar())
                            else -> "$prefix §c/${cmd.name} ${args[0]} ${args[1]} [SoundName]".toPlayer(player)
                        }
                        playlistArgs[1] -> when (args.size) {
                            2 -> option.removePlayList()
                            3 -> option.removePlayList(args[2].removeUnderBar())
                            else -> "$prefix §c/${cmd.name} ${args[0]} ${args[1]} [SoundName]".toPlayer(player)
                        }
                        playlistArgs[2] -> option.addPlayListAll()
                        playlistArgs[3] -> option.clearPlayList()
                    }
                }
            }
        }
        return false
    }

    override fun onTabComplete(player: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        val option = Main.poMap[player]!!
        if (args.size == 1) for (s in args0) if (s.contains(args[0])) list.add(s)
        if (args.size >= 2) when (args[0]) {
            args0[3] -> for (s in SppClass().getSubtitleList()) if (s.setUnderBar().contains(args[1])) list.add(s.setUnderBar())

            args0[4] -> {
                if (args.size == 2) for (s in playlistArgs) if (s.contains(args[1])) list.add(s)
                if (args.size == 3) when (args[1]) {
                    playlistArgs[0] -> for (s in SppClass().getSubtitleList()) if (s.setUnderBar().contains(args[2])) list.add(s.setUnderBar())
                    playlistArgs[1] -> for (s in option.getPlayList()) if (s.setUnderBar().contains(args[2])) list.add(s.setUnderBar())
                }
            }
        }

        return list
    }

}