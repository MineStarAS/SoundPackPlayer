package kr.kro.minestar.spp.data

import kr.kro.minestar.spp.Main
import kr.kro.minestar.spp.functions.SppClass
import kr.kro.minestar.spp.toPlayer
import kr.kro.minestar.spp.toTick
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.io.File

class PlayerOption(val player: Player) {
    private var file = File(Main.pl.dataFolder, "player/" + player.uniqueId.toString())
    private var yaml = YamlConfiguration.loadConfiguration(file)
    private var task: BukkitTask? = null

    var play = true
    var shuffleNumber: List<Int> = mutableListOf()

    var playSound: String
    var loop: LoopType
    var shuffle: Boolean
    var soundList: List<String>

    init {
        if (!file.exists()) {
            file.createNewFile()
            yaml = YamlConfiguration.loadConfiguration(file)
            yaml["PLAY_SOUND"] = Main.pl.config.getString("DEFAULT_BGM") ?: ""
            yaml["LOOP"] = LoopType.ONE_TIME.toString()
            yaml["SHUFFLE"] = false
            yaml["SOUND_LIST"] = listOf<String>(Main.pl.config.getString("DEFAULT_BGM") ?: "")
            yaml.save(file)
            file = File(Main.pl.dataFolder, player.uniqueId.toString())
            yaml = YamlConfiguration.loadConfiguration(file)
        }
        playSound = yaml.getString("PLAY_SOUND") ?: ""
        loop = LoopType.valueOf(yaml.getString("LOOP") ?: LoopType.ONE_TIME.toString())
        shuffle = yaml.getBoolean("SHUFFLE")
        soundList = yaml.getStringList("SOUND_LIST")
        setShuffleNumber()
    }

    /**
     * 재생상태를 조정합니다.
     */
    fun togglePlay() {
        if (play) {
            play = false
            cancelTask()
            javaClass.enclosingMethod.name.toPlayer(player)
        } else {
            play = true
            when (loop) {
                LoopType.ONE_TIME -> playOneTime()
                LoopType.ONE_LOOP -> playOneLoop()
                LoopType.LIST_TIME -> playList()
                LoopType.LIST_LOOP -> playList()
            }
            javaClass.enclosingMethod.name.toPlayer(player)
        }
    }

    fun playSound() {
        stopSound()
        player.playSound(player.location.add(0.0, 500.0, 0.0), playSound, SoundCategory.RECORDS, 1F, 1F)
    }

    fun stopSound() {
        player.stopSound(SoundStop.source(Sound.Source.RECORD))
    }

    fun cancelTask() {
        stopSound()
        if (task == null) return
        task!!.cancel()
        task = null
    }

    /**
     * 한곡을 한번만 재생합니다.
     */
    fun playOneTime() {
        cancelTask()
        playSound()
    }

    fun playOneTime(key: String) {
        cancelTask()
        setSound(key)
        playSound()
    }

    /**
     * 한곡을 반복 재생합니다.
     */
    fun playOneLoop() {
        cancelTask()
        val time = SppClass().getOggTimeLength(SppClass().getFile(playSound))
        if (time == 0) {
            javaClass.enclosingMethod.name.toPlayer(player)
            return
        }
        task = Bukkit.getScheduler().runTaskTimer(Main.pl, Runnable { playSound() }, 0, time.toTick() + 20L)
    }

    fun playOneLoop(key: String) {
        cancelTask()
        setSound(key)
        val time = SppClass().getOggTimeLength(SppClass().getFile(playSound))
        if (time == 0) {
            javaClass.enclosingMethod.name.toPlayer(player)
            return
        }
        task = Bukkit.getScheduler().runTaskTimer(Main.pl, Runnable { playSound() }, 0, time.toTick() + 20L)
    }

    /**
     * 재생목록을 재생합니다.
     */
    fun playList() {
        cancelTask()
        if (soundList.isEmpty()) {
            javaClass.enclosingMethod.name.toPlayer(player)
            return
        }
        setShuffleNumber()
        val l1 = soundList
        val l2 = shuffleNumber
        setSound(l1[l2[0]])
        playSound()
        playList(1, l1, l2)
    }

    fun playList(count: Int, l1: List<String>, l2: List<Int>) {
        if (count == l1.size) {
            if (loop == LoopType.LIST_LOOP) playList().let { return }
            else cancelTask().let { return }
        }
        setSound(l1[l2[count]])
        var time = SppClass().getOggTimeLength(SppClass().getFile(playSound))
        task = Bukkit.getScheduler().runTaskLater(Main.pl, Runnable {
            playSound()
            playList(count + 1, l1, l2)
        }, time.toTick() + 20L)
    }


    /**
     * 현재 재생 중인 사운드를 지정 합니다.
     */
    fun setSound(key: String) {
        playSound = key
        save()
    }

    /**
     * 다음 루프 타입으로 설정됩니다.
     */
    fun rotateLoopType() {
        loop = when (loop) {
            LoopType.ONE_TIME -> LoopType.LIST_TIME
            LoopType.LIST_TIME -> LoopType.ONE_LOOP
            LoopType.ONE_LOOP -> LoopType.LIST_LOOP
            LoopType.LIST_LOOP -> LoopType.ONE_TIME
        }
        save()
    }

    fun toggleShuffle() {
        shuffle = !shuffle
    }

    fun setShuffleNumber() {
        val list = mutableListOf<Int>()
        for (int in soundList.indices) list.add(int)
        if (shuffle) list.shuffle()
        shuffleNumber = list
    }

    fun save() {
        yaml["MUTE"] = loop
        yaml["PLAY_SOUND"] = playSound
        file = File(Main.pl.dataFolder, player.uniqueId.toString())
        yaml = YamlConfiguration.loadConfiguration(file)
    }
}