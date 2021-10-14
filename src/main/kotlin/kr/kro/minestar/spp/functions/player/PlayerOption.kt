package kr.kro.minestar.spp.functions.player

import kr.kro.minestar.spp.Main
import kr.kro.minestar.spp.functions.SppClass
import kr.kro.minestar.spp.functions.toPlayer
import kr.kro.minestar.spp.functions.toTick
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.io.File

class PlayerOption(private val player: Player) {
    private val prefix = Main.prefix

    private var file = File(Main.pl.dataFolder, "player/" + player.uniqueId.toString() + ".yml")
    private var yaml = YamlConfiguration.loadConfiguration(file)
    private var task: BukkitTask? = null

    private var play: Boolean
    private var playSound: String
    private var playType: PlayType
    private var loop: Boolean
    private var shuffle: Boolean
    private var playList: MutableList<String>
    private var shuffleNumber: List<Int> = mutableListOf()

    init {
        if (!file.exists()) {
            yaml = YamlConfiguration.loadConfiguration(file)
            yaml["PLAY"] = true
            yaml["PLAY_SOUND"] = Main.pl.config.getString("DEFAULT_BGM_SUBTITLE") ?: ""
            yaml["PLAY_TYPE"] = PlayType.ONE_TIME.toString()
            yaml["LOOP"] = true
            yaml["SHUFFLE"] = false
            yaml["PLAY_LIST"] = listOf<String>()
            yaml.save(file)
            file = File(Main.pl.dataFolder, player.uniqueId.toString())
        }
        play = yaml.getBoolean("PLAY")
        playSound = yaml.getString("PLAY_SOUND") ?: ""
        playType = PlayType.valueOf(yaml.getString("PLAY_TYPE") ?: PlayType.ONE_TIME.toString())
        loop = yaml.getBoolean("LOOP")
        shuffle = yaml.getBoolean("SHUFFLE")
        playList = yaml.getStringList("PLAY_LIST")
        setShuffleNumber()
        Bukkit.getScheduler().runTaskLater(Main.pl, Runnable {
            if (play) when (playType) {
                PlayType.ONE_TIME -> playOne()
                PlayType.LIST -> playList(false)
            }
        }, 20 * 5)
    }

    /**
     * 재생상태를 조정합니다.
     */
    fun togglePlay() {
        if (play) {
            "$prefix §9Play §cOFF".toPlayer(player)
            play = false
            cancelTask()
        } else {
            "$prefix §9Play §aON".toPlayer(player)
            play = true
            when (playType) {
                PlayType.ONE_TIME -> playOne()
                PlayType.LIST -> playList(false)
            }
        }
    }

    fun selectSound(subtitle: String) {
        setSound(subtitle)
        if (play && playType == PlayType.ONE_TIME) playOne()
    }

    private fun playSound() {
        stopSound()
        val key = SppClass().getKeyFromSubtitle(playSound)
        if (key == "") {
            "$prefix null file".toPlayer(player)
            cancelTask()
        }
        player.playSound(player.location.add(0.0, 500.0, 0.0), key, SoundCategory.RECORDS, 1F, 1F)
        return
    }

    private fun stopSound() {
        player.stopSound(SoundStop.source(Sound.Source.RECORD))
    }

    private fun cancelTask() {
        stopSound()
        if (task == null) return
        task!!.cancel()
        task = null
    }

    /**
     * 한곡을 한번만 재생합니다.
     */
    private fun playOne() {
        playOneLoop()
        playSound()
        "$prefix §aPlay §e§l$playSound".toPlayer(player)
    }

    /**
     * 한곡을 반복 재생합니다.
     */
    private fun playOneLoop() {
        cancelTask()
        val time = SppClass().getOggTimeLength(SppClass().getFile(SppClass().getKeyFromSubtitle(playSound)))
        task = Bukkit.getScheduler().runTaskLater(Main.pl, Runnable {
            if (!loop) return@Runnable
            playOneLoop()
            playSound()
        }, time.toTick() + 20L)
    }

    /**
     * 재생목록을 재생합니다.
     */
    private fun playList(isLoop: Boolean) {
        cancelTask()
        if (playList.isEmpty()) {
            "$prefix §c재생목록이 비어있습니다.".toPlayer(player)
            togglePlay()
            return
        }
        if (!isLoop) setShuffleNumber()
        val l1 = playList
        val l2 = shuffleNumber
        setSound(l1[l2[0]])
        playSound()
        playList(0, l1, l2)
        "$prefix §aPlay §e§l$playSound".toPlayer(player)
    }

    private fun playList(count: Int, l1: List<String>, l2: List<Int>) {
        if (count == l1.size) {
            if (loop) playList(true).let { return }
            else cancelTask().let { return }
        } else {
            val time = SppClass().getOggTimeLength(SppClass().getFile(SppClass().getKeyFromSubtitle(playSound)))
            task = Bukkit.getScheduler().runTaskLater(Main.pl, Runnable {
                if (count + 1 != l1.size) setSound(l1[l2[count + 1]])
                playSound()
                playList(count + 1, l1, l2)
                "$prefix §aPlay §e§l$playSound".toPlayer(player)
            }, time.toTick() + 20L)
        }
    }

    /**
     * 현재 재생 중인 사운드를 지정 합니다.
     */
    private fun setSound(subtitle: String) {
        if (subtitle == "") return
        playSound = subtitle
        save()
    }

    /**
     * 플레이 타입, 셔플 온/오프, 루프 온/오프 를 설정합니다.
     */
    fun togglePlayType() {
        if (play) "$prefix §c재생 중에는 변경 할 수 없습니다.".toPlayer(player).let { return }
        if (playType == PlayType.ONE_TIME) {
            playType = PlayType.LIST
            "$prefix 재생목록을 재생합니다.".toPlayer(player)
            togglePlay()
        } else {
            playType = PlayType.ONE_TIME
            "$prefix 현재 선택된 곡만 재생합니다.".toPlayer(player)
        }
        save()
    }

    fun toggleShuffle() {
        if (play) "$prefix §c재생 중에는 변경 할 수 없습니다.".toPlayer(player).let { return }
        shuffle = !shuffle
        if (shuffle) "$prefix §9셔플 기능§f을 §a활성화 §f합니다.".toPlayer(player)
        else "$prefix §9셔플 기능§f을 §c비활성화 §f합니다.".toPlayer(player)
        save()
    }

    fun toggleLoop() {
        loop = !loop
        if (loop) "$prefix §9반복 재생§f을 §a활성화 §f합니다.".toPlayer(player)
        else "$prefix §9반복 재생§f을 §c비활성화 §f합니다.".toPlayer(player)
        save()
    }

    private fun setShuffleNumber() {
        val list = mutableListOf<Int>()
        for (int in playList.indices) list.add(int)
        if (shuffle) list.shuffle()
        shuffleNumber = list
    }

    /**
     * 재생목록에 추가 또는 제거를 합니다.
     */
    fun addPlayList() {
        if (SppClass().getKeyFromSubtitle(playSound) == "") "$prefix §c알 수 없는 제목입니다.".toPlayer(player).let { return }
        if (playList.contains(playSound)) "$prefix §c이미 목록에 추가되어 있습니다.".toPlayer(player).let { return }
        playList.add(playSound)
        save()
        "$prefix §9$playSound §f을/를 재생목록에 §a추가 §f하였습니다.".toPlayer(player)
    }

    fun addPlayList(subtitle: String) {
        if (SppClass().getKeyFromSubtitle(subtitle) == "") "$prefix §c알 수 없는 제목입니다.".toPlayer(player).let { return }
        if (playList.contains(subtitle)) "$prefix §c이미 목록에 추가되어 있습니다.".toPlayer(player).let { return }
        playList.add(subtitle)
        save()
        "$prefix §9$subtitle §f을/를 재생목록에 §a추가 §f하였습니다.".toPlayer(player)
    }

    fun addPlayListAll() {
        for (subtitle in SppClass().getSubtitleList()) if (!playList.contains(subtitle)) {
            playList.add(subtitle)
            "$prefix §9$subtitle §f을/를 재생목록에 §a추가 §f하였습니다.".toPlayer(player)
        }
        save()
    }

    fun removePlayList() {
        if (!playList.contains(playSound)) "$prefix §c재생목록에 추가되어 있지 않습니다.".toPlayer(player).let { return }
        playList.remove(playSound)
        save()
        "$prefix §9$playSound §f을/를 재생목록에서 §c제거 §f하였습니다.".toPlayer(player)
    }

    fun removePlayList(subtitle: String) {
        if (!playList.contains(subtitle)) "$prefix §c재생목록에 추가되어 있지 않습니다.".toPlayer(player).let { return }
        playList.remove(subtitle)
        save()
        "$prefix §9$playSound §f을/를 재생목록에서 §c제거 §f하였습니다.".toPlayer(player)
    }

    fun clearPlayList() {
        playList.clear()
        save()
        "$prefix §9모든 곡§f을 재생목록에서 §c제거 §f하였습니다.".toPlayer(player)
        if (play && playType == PlayType.LIST) togglePlay()
    }

    fun getPlayList(): MutableList<String> {
        return playList
    }

    private fun save() {
        yaml["PLAY"] = play
        yaml["PLAY_SOUND"] = playSound
        yaml["PLAY_TYPE"] = playType.toString()
        yaml["LOOP"] = loop
        yaml["SHUFFLE"] = shuffle
        yaml["PLAY_LIST"] = playList
        yaml.save(file)
        file = File(Main.pl.dataFolder, "player/" + player.uniqueId.toString() + ".yml")
    }
}