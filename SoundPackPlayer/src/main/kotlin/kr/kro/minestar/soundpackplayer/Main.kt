package kr.kro.minestar.soundpackplayer

import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.data.SoundPlayer
import kr.kro.minestar.soundpackplayer.functions.events.AlwaysEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f[§9SPP§f]"
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("spp")?.setExecutor(CMD)

        Bukkit.getPluginManager().registerEvents(AlwaysEvent, this)

        saveResource("config.yml", false)
        if (!File(dataFolder, "sounds.json").exists()) saveResource("sounds.json", false)
        if (!File(dataFolder, "sounds").exists()) File(dataFolder, "sounds").mkdir()

        DataClass.loadSoundList()

        for (player in Bukkit.getOnlinePlayers()) SoundPlayer(player)
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) DataClass.unregisterSoundPlayer(DataClass.soundPlayer(player))
    }
}