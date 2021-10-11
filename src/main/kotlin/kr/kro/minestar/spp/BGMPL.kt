package kr.kro.minestar.spp

import org.bukkit.entity.Player
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.scheduler.BukkitTask
import java.lang.Exception
import java.util.*

//class BGMPL {
//    fun playBGM(p: Player, bgm: BGM) {
//        stopLoop(p)
//        p.sendMessage(prefix + "§6" + bgm.bgmClass + " §e" + bgm.toString().replace("_", " "))
//        p.stopSound(SoundStop.source(Sound.Source.RECORD))
//        p.playSound(p.location.add(0.0, 500.0, 0.0), bgm.code, records, bgm.bgmClass.volume, 1)
//    }
//
//    fun playBGM(p: Player, bgmToString: String?) {
//        val bgm: BGM
//        bgm = try {
//            BGM.valueOf(bgmToString)
//        } catch (e1: Exception) {
//            p.sendMessage(prefix + "§c알 수 없는 BGM 입니다.")
//            return
//        }
//        stopLoop(p)
//        p.sendMessage(prefix + "§6" + bgm.bgmClass + " §e" + bgm.toString().replace("_", " "))
//        p.stopSound(SoundStop.source(Sound.Source.RECORD))
//        p.playSound(p.location.add(0.0, 500.0, 0.0), bgm.code, records, bgm.bgmClass.volume, 1)
//    }
//
//    fun loopPlayBGM(p: Player, bgm: BGM) {
//        stopLoop(p)
//        p.sendMessage(prefix + "§6" + bgm.bgmClass + " §e" + bgm.toString().replace("_", " "))
//        loop[p.uniqueId] = Bukkit.getScheduler().runTaskTimer(BGMController.pl, Runnable {
//            p.stopSound(SoundStop.source(Sound.Source.RECORD))
//            p.playSound(p.location.add(0.0, 500.0, 0.0), bgm.code, records, bgm.bgmClass.volume, 1)
//        }, 1, bgm.playTime + 20)
//    }
//
//    fun loopPlayBGM(p: Player, bgmToString: String?) {
//        val bgm: BGM
//        bgm = try {
//            BGM.valueOf(bgmToString)
//        } catch (e1: Exception) {
//            p.sendMessage(prefix + "§c알 수 없는 BGM 입니다.")
//            return
//        }
//        stopLoop(p)
//        p.sendMessage(prefix + "§6" + bgm.bgmClass + " §e" + bgm.toString().replace("_", " "))
//        loop[p.uniqueId] = Bukkit.getScheduler().runTaskTimer(BGMController.pl, Runnable {
//            p.stopSound(SoundStop.source(Sound.Source.RECORD))
//            p.playSound(p.location.add(0.0, 500.0, 0.0), bgm.code, records, bgm.bgmClass.volume, 1)
//        }, 1, bgm.playTime + 20)
//    }
//
//    fun stopLoop(p: Player) {
//        p.stopSound(SoundStop.source(Sound.Source.RECORD))
//        if (loop[p.uniqueId] != null) {
//            loop[p.uniqueId]!!.cancel()
//            loop.remove(p.uniqueId)
//        }
//    }
//
//    companion object {
//        private val prefix: String = BGMController.prefix
//        private val records = SoundCategory.RECORDS
//        private val loop: MutableMap<UUID, BukkitTask?> = HashMap()
//        private val loopBGM: Map<UUID, BGM> = HashMap<UUID, BGM>()
//    }
//}