package kr.kro.minestar.spp

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioSystem


class SppClass {
    val path = Main.pl.dataFolder.canonicalPath + "\\"
    val soundsFile = File(Main.pl.dataFolder, "sounds.json")

    fun getLength(address: String): Double? {
        val file = File(address)
        if (!file.exists()) return null

        val audioInputStream = AudioSystem.getAudioInputStream(file)
        val format = audioInputStream.format
        val frames = audioInputStream.frameLength
        return frames.toDouble() / format.frameRate
    }

    fun getLengthFromKey(key: String): Double {
        val address = "sounds/" + getSoundAddress(key) + ".ogg"
        val file = File(Main.pl.dataFolder, address)
        if (!file.exists()) return 0.123
        file.length().toString().toServer()

        val audioInputStream = AudioSystem.getAudioInputStream(file)
        val format = audioInputStream.format
        val frames = audioInputStream.frameLength
        return frames.toDouble() / format.frameRate
    }

    fun getFile(key: String): File {
        val address = "sounds/" + getSoundAddress(key) + ".ogg"
        return File(Main.pl.dataFolder, address)
    }

    //test

    @Throws(IOException::class)
    fun calculateDuration(oggFile: File): Double {
        var rate = -1
        var length = -1
        val size = oggFile.length().toInt()
        val t = ByteArray(size)
        val stream = FileInputStream(oggFile)
        stream.read(t)
        run {
            var i = size - 1 - 8 - 2 - 4
            while (i >= 0 && length < 0) {
                //4 bytes for "OggS", 2 unused bytes, 8 bytes for length
                // Looking for length (value after last "OggS")
                if (t[i] == 'O'.toByte() && t[i + 1] == 'g'.toByte() && t[i + 2] == 'g'.toByte() && t[i + 3] == 'S'.toByte()
                ) {
                    val byteArray = byteArrayOf(t[i + 6], t[i + 7], t[i + 8], t[i + 9], t[i + 10], t[i + 11], t[i + 12], t[i + 13])
                    val bb: ByteBuffer = ByteBuffer.wrap(byteArray)
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                    length = bb.getInt(0)
                }
                i--
            }
        }
        var i = 0
        while (i < size - 8 - 2 - 4 && rate < 0) {

            // Looking for rate (first value after "vorbis")
            if (t[i] == 'v'.toByte() && t[i + 1] == 'o'.toByte() && t[i + 2] == 'r'.toByte() && t[i + 3] == 'b'.toByte() && t[i + 4] == 'i'.toByte() && t[i + 5] == 's'.toByte()
            ) {
                val byteArray = byteArrayOf(t[i + 11], t[i + 12], t[i + 13], t[i + 14])
                val bb: ByteBuffer = ByteBuffer.wrap(byteArray)
                bb.order(ByteOrder.LITTLE_ENDIAN)
                rate = bb.getInt(0)
            }
            i++
        }
        stream.close()
        return (length * 1000).toDouble() / rate.toDouble()
    }

    //test end

    fun getSoundList(): List<Any?> {
        if (!existsSounds()) return listOf()
        val parser = JSONParser()
        val reader = FileReader(path + "sounds.json")
        val obj = parser.parse(reader)
        val jsonObject = obj as JSONObject
        reader.close()
        return jsonObject.keys.toList()
    }

    fun getSoundAddress(key: String): String? {
        val parser = JSONParser()
        val reader = FileReader(path + "sounds.json")
        val obj = parser.parse(reader)
        val jsonObject = obj as JSONObject
        reader.close()

        val m = jsonObject[key] ?: return null
        val m1 = m as Map<String, *>
        val m2 = m1["sounds"] as List<*>
        val m3 = m2[0] as Map<String, *>

        return m3["name"].toString()
    }

    fun existsSounds(): Boolean {
        if (soundsFile.exists()) return true
        return false
    }
}