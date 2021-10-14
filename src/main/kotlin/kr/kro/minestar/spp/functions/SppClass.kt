package kr.kro.minestar.spp.functions

import kr.kro.minestar.spp.Main
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SppClass {
    val path = Main.pl.dataFolder.canonicalPath + "\\"
    val soundsFile = File(Main.pl.dataFolder, "sounds.json")

    /**
     * sounds.json을 JSONObject 클래스로 가져옵니다.
     */
    fun getJson(): JSONObject {
        val parser = JSONParser()
        val reader = BufferedReader(InputStreamReader(FileInputStream(path + "sounds.json"), "UTF-8"))
        val obj = parser.parse(reader)
        val jsonObject = obj as JSONObject
        reader.close()
        return jsonObject
    }

    /**
     * jsonObject 의 key 목록을 가져옵니다.
     */
    fun getKeyList(): List<Any?> {
        if (!soundsFile.exists()) return listOf()
        val jsonObject = getJson()
        return jsonObject.keys.toList()
    }

    /**
     * key를 이용해 subtitle을 가져옵니다.
     */
    fun getSubtitle(key: String): String {
        val jsonObject = getJson()
        val m = jsonObject[key] ?: return "null"
        val m1 = m as Map<String, *>
        return m1["subtitle"].toString()
    }

    /**
     * subtitle 목록을 가져옵니다.
     */
    fun getSubtitleList(): List<String> {
        val list = mutableListOf<String>()
        for (key in getKeyList()) list.add(getSubtitle(key.toString()))
        return list
    }

    fun getKeyFromSubtitle(subtitle: String): String {
        val list = getKeyList()
        for (key in list) if (getSubtitle(key.toString()) == subtitle) return key.toString()
        return ""
    }

    /**
     * key를 이용해 파일 주소를 가져옵니다.
     */
    fun getSoundAddress(key: String): String {
        val jsonObject = getJson()
        val m = jsonObject[key] ?: return "null"
        val m1 = m as Map<String, *>
        val m2 = m1["sounds"] as List<*>
        val m3 = m2[0] as Map<String, *>
        return m3["name"].toString()
    }

    /**
     * key를 이용해 파일 주소를 가져온 후, 파일 주소에 있는 파일을 가져옵니다.
     */
    fun getFile(key: String): File? {
        val address = "sounds/" + getSoundAddress(key) + ".ogg"
        val file = File(Main.pl.dataFolder, address)
        return if (file.exists()) file
        else null
    }

    /**
     * OGG파일의 재생길이를 가져옵니다.
     */
    fun getOggTimeLength(oggFile: File?): Int {
        if (oggFile == null) return 0
        var rate = -1
        var length = -1
        val size = oggFile.length().toInt()
        val t = ByteArray(size)
        val stream = FileInputStream(oggFile)
        stream.read(t)
        run {
            var i = size - 1 - 8 - 2 - 4
            while (i >= 0 && length < 0) {
                if (t[i] == 'O'.toByte() && t[i + 1] == 'g'.toByte() && t[i + 2] == 'g'.toByte() && t[i + 3] == 'S'.toByte()) {
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
            if (t[i] == 'v'.toByte() && t[i + 1] == 'o'.toByte() && t[i + 2] == 'r'.toByte() && t[i + 3] == 'b'.toByte() && t[i + 4] == 'i'.toByte() && t[i + 5] == 's'.toByte()) {
                val byteArray = byteArrayOf(t[i + 11], t[i + 12], t[i + 13], t[i + 14])
                val bb: ByteBuffer = ByteBuffer.wrap(byteArray)
                bb.order(ByteOrder.LITTLE_ENDIAN)
                rate = bb.getInt(0)
            }
            i++
        }
        stream.close()
        return (length.toDouble() / rate.toDouble()).toInt()
    }
}