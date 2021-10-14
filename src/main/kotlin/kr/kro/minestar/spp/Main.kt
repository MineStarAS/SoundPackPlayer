package kr.kro.minestar.spp

import kr.kro.minestar.spp.functions.player.PlayerOption
import kr.kro.minestar.spp.functions.AlwaysEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f§7[§9SPP§7]§f"

        val poMap: HashMap<Player, PlayerOption> = HashMap()
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("spp")?.setExecutor(CMD())

        Bukkit.getPluginManager().registerEvents(AlwaysEvent(), this)

        if (!File(dataFolder, "config.yml").exists()) saveDefaultConfig()
        if (!File(dataFolder, "sounds.json").exists()) saveResource("sounds.json", false)

        for (p in Bukkit.getOnlinePlayers()) poMap[p] = PlayerOption(p)
    }

    override fun onDisable() {
    }
}