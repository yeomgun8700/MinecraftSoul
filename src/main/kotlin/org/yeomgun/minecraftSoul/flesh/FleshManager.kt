package org.yeomgun.minecraftSoul.flesh

import io.papermc.paper.datacomponent.item.ResolvableProfile
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.yeomgun.minecraftSoul.MinecraftSoul
import java.util.UUID


object FleshManager : Listener {

    val fleshes : MutableList<Flesh> = mutableListOf()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val p = event.player
        fleshes.add(Flesh(p))
    }

    @EventHandler
    fun onPlayerDie(event: PlayerDeathEvent) {
        val p = event.player
        event.drops.clear()
        fleshes.add(Flesh(p).apply { this.mannequin.isInvulnerable = true })
        TODO("플레이어 관전모드로 바꾸고 미리 만들어놓은 flesh(마네킹)에 빙의시키기")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        var flesh : Flesh? = null
        for (f in fleshes) {
            if (f.playerUUID == event.player.uniqueId) {
                flesh = f
                break
            }
        }
        flesh?.removeFlesh()
        fleshes.remove(flesh)
    }


    @EventHandler
    fun onRightClicked(event: PlayerInteractEntityEvent) {
        if (event.rightClicked is Mannequin) {
            for (flesh in fleshes) {
                if (flesh.mannequin == event.rightClicked) {
                    event.player.openInventory(flesh.inventory)
                    return
                }
            }
        }
    }

    @EventHandler
    fun onInventoryChanged(event: InventoryInteractEvent) {
        for (flesh in fleshes) {
            if (flesh.inventory == event.inventory) {
                TODO("만약 갑옷, 왼손 부분 인벤토리가 바뀌었고 그 칸에 들어갈 수 있는 템이면 통과 후 그거 적용하기. 이후 break")
            }
        }
    }

    @EventHandler
    fun onEntityDie(event: EntityDeathEvent) {
        if (event.entity is Mannequin) {
            for (flesh in fleshes) {
                if (flesh.mannequin == event.entity) {
                    event.drops.clear()
                    flesh.inventory.contents.forEach { event.drops.add(it) }
                    fleshes.remove(flesh)
                }
            }
        }
    }

}