package kr.kro.minestar.spp

import org.bukkit.entity.Player
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader
import javax.sound.sampled.AudioSystem


class SppClass {
    val path = Main.pl.dataFolder.canonicalPath + "\\"
    val file = File(Main.pl.dataFolder, "sound.")

    fun getLength(file: File): Double {
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        val format = audioInputStream.format
        val frames = audioInputStream.frameLength
        return frames.toDouble() / format.frameRate
    }

    fun test(player: Player) {
        val parser = JSONParser()
        val reader = FileReader(path + "sounds.json")
        val obj = parser.parse(reader)
        val jsonObject = obj as JSONObject
        reader.close()

        for (map in jsonObject) {
            map.value
            "key:: ${map.key}".toPlayer(player)
            " ".toPlayer(player)
            val m1 = map.value as Map<String, *>
            val m2 = m1["sounds"]
            m2.toString().replace("[", "").replace("]", "").toPlayer(player)
            " ".toPlayer(player)
            val m3 = m2 as Map<String, *>
            m3["name"].toString().toPlayer(player)
            " ".toPlayer(player)
        }
    }
    fun test1(player: Player){
        val list = listOf("test1","test2")
        list.toString().toPlayer(player)
    }
}