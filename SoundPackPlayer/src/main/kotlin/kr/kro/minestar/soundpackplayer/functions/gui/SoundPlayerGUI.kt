package kr.kro.minestar.soundpackplayer.functions.gui

import kr.kro.minestar.soundpackplayer.Main
import kr.kro.minestar.soundpackplayer.data.SoundPlayer
import kr.kro.minestar.soundpackplayer.functions.ItemClass.head
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.item.flagAll
import kr.kro.minestar.utility.material.item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.scheduler.BukkitTask

class SoundPlayerGUI(val soundPlayer: SoundPlayer) : GUI {
    override val player = soundPlayer.player
    override val pl = Main.pl
    override val gui = Bukkit.createInventory(null, 9 * 3, "[SSP PLAYER]")

    val soundList = Material.MOJANG_BANNER_PATTERN.item().display("§e[§f곡 목록§e]").flagAll()
    val shuffle = head(9171).display("§e[§f재생목록 셔플§e]")
    val previous = head(8894).display("§9[§f이전 곡 재생§9]")
    val play = listOf(
        head(9325).display("§c[§f정지§c]"),
        head(9865).display("§a[§f재생§a]"),
    )
    val skip = head(8892).display("§9[§f다음 곡 재생§9]")
    val listPlay = listOf(
        head(9394).display("§c[§f한 곡 재생§c]"),
        head(9934).display("§a[§f재생목록 재생§a]"),
    )
    val autoStart = listOf(
        head(9405).display("§c[§f접속 시 자동 재생§c]"),
        head(9945).display("§a[§f접속 시 자동 재생§a]"),
    )
    val loop = listOf(
        head(9329).display("§9[§f반복재생§9] §f: §cOFF"),
        head(9330).display("§9[§f반복재생§9] §f: §cOFF"),
        head(9333).display("§9[§f반복재생§9] §f: §cOFF"),
        head(9332).display("§9[§f반복재생§9] §f: §cOFF"),

        head(9869).display("§9[§f반복재생§9] §f: §aON"),
        head(9870).display("§9[§f반복재생§9] §f: §aON"),
        head(9873).display("§9[§f반복재생§9] §f: §aON"),
        head(9872).display("§9[§f반복재생§9] §f: §aON"),
    )

    val playList = Material.MOJANG_BANNER_PATTERN.item().display("§a[§f재생목록§a]").flagAll()

    fun timeBar() = Material.YELLOW_STAINED_GLASS_PANE.item().display("§6[§f${soundPlayer.timeScript()}§6]")
    fun timePoint() = Material.ORANGE_STAINED_GLASS_PANE.item().display("§6[§f${soundPlayer.timeScript()}§6]")

    var loopFrame = 0

    var timer: BukkitTask? = null

    init {
        openGUI()
    }

    override fun openGUI () {
        Bukkit.getPluginManager().registerEvents(this, pl)
        displaying()

        timer = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            displaying()
            ++loopFrame
            if (loopFrame >= 4) loopFrame = 0
        }, 0, 20)

        player.openInventory(gui)
    }

    override fun displaying() {
        gui.clear()
        gui.setItem(9 * 2, soundList)
        gui.setItem(9 * 2 + 1, shuffle)

        if (soundPlayer.isAutoStart()) gui.setItem(9 * 2 + 2, autoStart.last())
        else gui.setItem(9 * 2 + 2, autoStart.first())

        gui.setItem(9 * 2 + 3, previous)

        if (soundPlayer.isPlaying()) gui.setItem(9 * 2 + 4, play.last())
        else gui.setItem(9 * 2 + 4, play.first())

        gui.setItem(9 * 2 + 5, skip)

        if (soundPlayer.isListPlay()) gui.setItem(9 * 2 + 6, listPlay.last())
        else gui.setItem(9 * 2 + 6, listPlay.first())

        if (!soundPlayer.isLoop()) gui.setItem(9 * 2 + 7, loop[loopFrame])
        else gui.setItem(9 * 2 + 7, loop[loopFrame + 4])

         gui.setItem(9 * 2 + 8, playList)

        for (int in 0..8) gui.setItem(int, timeBar())

        if (soundPlayer.playingSound() == null) gui.setItem(9 * 1 + 4, Material.STRUCTURE_VOID.item().display("§7선택된 곡이 없습니다"))
        else {
            gui.setItem(9 * 1 + 4, soundPlayer.playingSound()!!.iconItem)
            val divisionTime = soundPlayer.playingSound()!!.time / 9
            val currentTime = soundPlayer.currentTime()
            var point = 8
            for (int in 0..8) if (divisionTime * int <= currentTime && currentTime < divisionTime * (int + 1)) {
                point = int
                break
            }
            gui.setItem(point, timePoint())
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return
        val item = e.currentItem ?: return

        when (item) {
            soundList -> SoundListGUI(soundPlayer, this)
            shuffle -> soundPlayer.playListShuffle()
            previous -> soundPlayer.previous()
            skip -> soundPlayer.skip()
            playList -> PlayListGUI(soundPlayer, this)
            else -> {
                if (play.contains(item)) {
                    if (soundPlayer.isPlaying()) soundPlayer.stop(true)
                    else soundPlayer.play()
                }
                if (autoStart.contains(item)) soundPlayer.autoStartToggle()
                if (listPlay.contains(item)) soundPlayer.listPlayToggle()
                if (loop.contains(item)) soundPlayer.loopToggle()
            }
        }
        displaying()
    }

    @EventHandler
    override fun closeGUI(e: InventoryCloseEvent) {
        if (e.player != player) return
        if (e.inventory != gui) return
        HandlerList.unregisterAll(this)
        timer?.cancel()
    }
}