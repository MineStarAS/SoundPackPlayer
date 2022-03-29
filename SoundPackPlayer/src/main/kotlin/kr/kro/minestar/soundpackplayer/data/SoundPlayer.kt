package kr.kro.minestar.soundpackplayer.data

import kr.kro.minestar.soundpackplayer.Main
import kr.kro.minestar.soundpackplayer.Main.Companion.pl
import kr.kro.minestar.utility.string.toPlayer
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.io.File

class SoundPlayer(val player: Player) {
    private val prefix = Main.prefix

    private var file = File(pl.dataFolder, "player/${player.uniqueId}.yml")
    private var data = YamlConfiguration.loadConfiguration(file)

    private var playing: SoundData?
    fun playingSound() = playing

    private var listPlay: Boolean
    fun isListPlay() = listPlay
    private var loop: Boolean
    fun isLoop() = loop

    private var playList: MutableList<SoundData>
    fun playList() = playList

    private var nextPlay: BukkitTask? = null
    fun isPlaying() = (nextPlay != null)

    private var timer: BukkitTask? = null
    private var currentTime = 0
    fun currentTime() = currentTime

    fun timeScript(): String {
        val maxTime = playing?.time ?: 0

        val maxTimeSec = if (maxTime % 60 < 10) "0${maxTime % 60}"
        else "${maxTime % 60}"

        val currentTimeSec = if (currentTime % 60 < 10) "0${currentTime % 60}"
        else "${currentTime % 60}"

        return "${currentTime / 60}:$currentTimeSec / ${maxTime / 60}:$maxTimeSec"
    }

    init {
        if (!file.exists()) {
            data = YamlConfiguration()
            val config = pl.config

            data["playing"] = config.getString("DEFAULT_BGM_SUBTITLE")
            data["listPlay"] = true
            data["loop"] = true
            val list = mutableListOf<String>()
            if (data.getString("playing") != "" && data.getString("playing") != null) list.add(data.getString("playing")!!)
            data["playList"] = list
            data.save(file)
            Bukkit.getScheduler().runTaskLater(pl, Runnable {
                "$prefix 음악 볼륨 조절은 §e[음악 및 소리...] §f설정에서 §e[주크박스/소리 블록] §f으로 조절 할 수 있습니다.".toPlayer(player)
            }, 5 * 20)
        }

        playing = DataClass.soundData(data.getString("playing") ?: "")

        listPlay = data.getBoolean("listPlay")
        loop = data.getBoolean("loop")

        val list = mutableListOf<SoundData>()
        for (subtitle in data.getStringList("playList")) {
            val soundData = DataClass.soundData(subtitle) ?: continue
            list.add(soundData)
        }

        playList = list
        Bukkit.getScheduler().runTaskLater(pl, Runnable { play() }, 5 * 20)
        DataClass.registerSoundPlayer(this)
    }

    /**
     * Play Control
     */

    fun play() {
        if (playing == null) {
            "$prefix §c선택된 곡이 없습니다.".toPlayer(player)
            stop(false)
            return
        }

        currentTime = 0
        timer?.cancel()
        timer = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            ++currentTime
        }, 0, 20)

        playing!!.play(player)
        nextPlay()
    }

    fun play(soundData: SoundData) {
        playing = soundData
        currentTime = 0
        timer?.cancel()
        timer = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            ++currentTime
        }, 0, 20)

        playing!!.play(player)
        nextPlay()
    }

    fun skip() {
        if (playList.isEmpty()) {
            playing = null
            return play()
        }
        var index = playList.indexOf(playing)

        if (playList.lastIndex == index || index == -1) {
            index = 0
        } else ++index

        playing = playList[index]

        play()
    }

    fun previous() {
        if (playList.isEmpty()) {
            playing = null
            return play()
        }
        var index = playList.indexOf(playing)

        if (index <= 0) index = playList.lastIndex
        else --index

        playing = playList[index]

        play()
    }

    fun stop(script: Boolean) {
        player.stopSound(SoundStop.source(Sound.Source.RECORD))
        timer?.cancel()
        nextPlay?.cancel()
        nextPlay = null
        if (script) "${Main.prefix} §cStop".toPlayer(player)
    }

    private fun nextPlay() {
        nextPlay?.cancel()
        nextPlay = null
        nextPlay = Bukkit.getScheduler().runTaskLater(pl, Runnable {
            if (listPlay) {
                if (playList.isEmpty()) {
                    playing = null
                    play()
                } else {
                    var index = playList.indexOf(playing)
                    if (playList.lastIndex == index || index == -1) {
                        if (!loop) {
                            stop(true)
                            return@Runnable
                        }
                        index = 0
                    } else ++index
                    playing = playList[index]
                    play()
                }
            } else {
                if (loop) play()
                else stop(true)
            }
        }, playing!!.time * 20L)
    }

    fun listPlayToggle() {
        listPlay = !listPlay
    }

    fun loopToggle() {
        loop = !loop
    }

    /**
     * PlayList Control
     */
    fun playListAdd(soundData: SoundData) {
        if (playList.contains(soundData)) return "$prefix §c이미 추가된 곡입니다.".toPlayer(player)
        playList.add(soundData)
        "$prefix §aAdded §e${soundData.subtitle} §ain PlayList.".toPlayer(player)
    }

    fun playListSub(soundData: SoundData) {
        if (!playList.contains(soundData)) return "$prefix §cPlayList 에 추가되어 있지 않습니다.".toPlayer(player)
        playList.remove(soundData)
        "$prefix §aRemoved §e${soundData.subtitle} §ain PlayList.".toPlayer(player)
    }

    fun playListClear() {
        if (playList.isEmpty()) return "$prefix §c이미 비어 있습니다.".toPlayer(player)
        playList.clear()
        "$prefix §acleared PlayList.".toPlayer(player)
    }

    fun playListShuffle() {
        playList.shuffle()
        "$prefix §ashuffled PlayList.".toPlayer(player)
    }

    private fun playListToStringList(): List<String> {
        val list = mutableListOf<String>()
        for (soundData in playList) list.add(soundData.subtitle)
        return list.toList()
    }

    /**
    Save
     */
    fun save() {
        data["playing"] = playing?.subtitle
        data["listPlay"] = listPlay
        data["loop"] = loop
        data["playList"] = playListToStringList()
        data.save(file)
    }
}