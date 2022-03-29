package kr.kro.minestar.soundpackplayer.data

import kr.kro.minestar.soundpackplayer.Main.Companion.pl
import kr.kro.minestar.soundpackplayer.functions.SppClass
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object DataClass {
    /**
     * SoundPlayer
     */
    private val soundPlayerMap = hashMapOf<Player, SoundPlayer>()

    fun soundPlayer(player: Player) = soundPlayerMap[player]!!

    fun registerSoundPlayer(soundPlayer: SoundPlayer) {
        soundPlayerMap[soundPlayer.player] = soundPlayer
    }
    fun unregisterSoundPlayer(soundPlayer: SoundPlayer) {
        soundPlayer.save()
        soundPlayerMap.remove(soundPlayer.player)
    }

    /**
     * SoundData
     */
    private val soundDataMap = hashMapOf<String, SoundData>()

    fun soundData(subtitle: String) = soundDataMap[subtitle]

    fun soundDataList() = soundDataMap.values

    fun registerSoundData(soundData: SoundData) {
        soundDataMap[soundData.subtitle] = soundData
    }

    fun loadSoundList() {
        soundDataMap.clear()
        for (subtitle in SppClass.subtitleList()) SoundData(subtitle)
    }

    /**
     * SoundData Icon
     */
    private val iconFile = File(pl.dataFolder, "icon.yml")
    private val iconData = YamlConfiguration.loadConfiguration(iconFile)

    private val randomIcon = listOf(
        Material.RED_DYE,
        Material.ORANGE_DYE,
        Material.YELLOW_DYE,
        Material.LIME_DYE,
        Material.GREEN_DYE,
        Material.LIGHT_BLUE_DYE,
        Material.CYAN_DYE,
        Material.BLUE_DYE,
        Material.MAGENTA_DYE,
        Material.PURPLE_DYE,
    )

    fun icon(subtitle: String): Material {
        val key = iconData.getString(subtitle) ?: return randomIcon.random()
        return Material.getMaterial(key) ?: randomIcon.random()
    }

    fun registerIcon(subtitle: String, material: Material) {
        iconData[subtitle] = material.name
        iconData.save(iconFile)
    }
}