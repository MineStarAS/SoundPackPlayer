package kr.kro.minestar.soundpackplayer.data

import kr.kro.minestar.soundpackplayer.Main.Companion.prefix
import kr.kro.minestar.soundpackplayer.functions.SppClass
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.toPlayerActionBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

class SoundData(val subtitle: String) {
    val key = SppClass.getKeyFromSubtitle(subtitle)
    val file = SppClass.getFile(key)
    val time = SppClass.getOggTimeLength(file) + 1

    val iconItem = DataClass.icon(subtitle).item()
        .display("§e$subtitle")
        .addLore("§9[§f${time / 60} §a분 §f${time % 60} §a초§9]")

    init {
        DataClass.registerSoundData(this)
    }

    fun play(player: Player) {
        player.stopSound(SoundStop.source(Sound.Source.RECORD))
        player.playSound(player.location.add(0.0, 500.0, 0.0), key, SoundCategory.RECORDS, 1F, 1F)
        "§aPlay §e$subtitle".toPlayerActionBar(player)
    }

    fun play(players: Collection<Player>) {
        for (player in players) {
            player.stopSound(SoundStop.source(Sound.Source.RECORD))
            player.playSound(player.location.add(0.0, 500.0, 0.0), key, SoundCategory.RECORDS, 1F, 1F)
            "§aPlay §e$subtitle".toPlayerActionBar(player)
        }
    }
}