package kr.kro.minestar.soundpackplayer.functions.gui

import kr.kro.minestar.soundpackplayer.Main
import kr.kro.minestar.soundpackplayer.Main.Companion.prefix
import kr.kro.minestar.soundpackplayer.data.DataClass
import kr.kro.minestar.soundpackplayer.data.SoundPlayer
import kr.kro.minestar.soundpackplayer.functions.ItemClass.head
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.unColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class PlayListGUI(val soundPlayer: SoundPlayer, val backGUI: GUI) : GUI {
    override val player = soundPlayer.player
    override val pl = Main.pl
    override val gui = Bukkit.createInventory(null, 9 * 6, "[재생목록]")

    var page = 0

    val previousPage = head(8895).display("§9[§f이전 페이지§9]")
    val nextPage = head(8893).display("§9[§f다음 페이지§9]")
    val back = head(9865).display("§a[§fSPP PLAYER§a]")
    val clear = head(9403).display("§c[§f재생목록 초기화§c]")
    val help = head(9236).display("§7[§f도움말§7]")
        .addLore("§7[LEFT] : 재생")
        .addLore("§7[SHIFT LEFT] : 재생목록에서 제거")

    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()
        gui.setItem(9 * 5 + 0, previousPage)
        gui.setItem(9 * 5 + 4, back)
        gui.setItem(9 * 5 + 6, help)
        gui.setItem(9 * 5 + 8, nextPage)
        val list = soundPlayer.playList()
        for ((slot, int) in (page * 45 until (page + 1) * 45).withIndex()) {
            if (list.lastIndex < int || int < 0) break
            gui.setItem(slot, list[int].iconItem)
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT && e.click != ClickType.SHIFT_LEFT) return
        val item = e.currentItem ?: return

        when (item) {
            previousPage -> {
                if (page <= 0) return
                --page
            }
            nextPage -> {
                if (soundPlayer.playList().lastIndex < (page + 1) * 45) return
                ++page
            }
            back -> return backGUI.openGUI()
            clear -> soundPlayer.playListClear()

            else -> {
                val soundData = DataClass.soundData(item.display().unColor()) ?: return "$prefix §c곡을 찾을 수 없습니다.".toPlayer(player)
                if (e.click == ClickType.LEFT) soundPlayer.play(soundData)
                if (e.click == ClickType.SHIFT_LEFT) soundPlayer.playListSub(soundData)
            }
        }
        displaying()
    }
}