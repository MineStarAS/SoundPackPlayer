package kr.kro.minestar.soundpackplayer.functions

import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Material

object ItemClass {
    fun head(id: Int) = HeadDatabaseAPI().getItemHead("$id") ?: Material.BARRIER.item().display("§c해당 ID의 머리가 없습니다")
}
