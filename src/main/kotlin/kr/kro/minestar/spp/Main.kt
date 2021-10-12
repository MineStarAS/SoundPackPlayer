package kr.kro.minestar.spp

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f§7[§9SoundPackPlayer§7]§f"
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("spp")?.setExecutor(CMD())

        if (!File(dataFolder, "config.yml").exists()) saveDefaultConfig()
    }

    override fun onDisable() {
    }
}