package kr.kro.minestar.spp

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CMD : CommandExecutor, TabCompleter {
    private val prefix = Main.prefix
    private val args0 = listOf("check", "length", "cmd3")
    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) {
        } else {
            when (args[0]) {
                args0[0] -> {
                    if (args.size == 2) SppClass().getSoundAddress(args[1])?.toPlayer(player)
                    else "$prefix §c/${cmd.name} ${args[0]} <SoundKey>".toPlayer(player)
                }
                args0[1] -> {
                    if (args.size == 2) SppClass().calculateDuration(SppClass().getFile(args[1])).toString().toPlayer(player)
                    else "$prefix §c/${cmd.name} ${args[1]} <SoundKey>".toPlayer(player)
                }
                args0[2] -> TODO()
            }
        }
        return false
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        if (args.size == 1) for (s in args0) if (s.contains(args[0])) list.add(s)
        if (args.size >= 2) when (args[0]) {
            args0[0], args0[1] -> for (s in SppClass().getSoundList()) if (s.toString().contains(args[1])) list.add(s.toString())
        }

        return list
    }

}