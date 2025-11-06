package org.yeomgun.minecraftSoul.flesh

import io.papermc.paper.datacomponent.item.ResolvableProfile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Mannequin
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import org.yeomgun.minecraftSoul.MinecraftSoul
import java.util.UUID

class Flesh(player: Player) {

    val playerUUID : UUID?
    val mannequin : Mannequin = player.world.spawn(player.location, Mannequin :: class.java) {
        it.pose = Pose.SLEEPING
        it.profile = ResolvableProfile.resolvableProfile(player.playerProfile)

        val inv = player.inventory
        it.equipment.helmet = inv.helmet
        it.equipment.chestplate = inv.chestplate
        it.equipment.leggings = inv.leggings
        it.equipment.boots = inv.boots
        it.equipment.setItemInMainHand(inv.itemInMainHand)
        it.equipment.setItemInOffHand(inv.itemInOffHand)
    }
    val inventory : Inventory = Bukkit.createInventory(null, 45,
        Component.text("\uEBBB\uE001")
        .color(NamedTextColor.WHITE)
        .decoration(TextDecoration.ITALIC, false))

    init {
        if (player.isOnline) {
            playerUUID = null
            object : BukkitRunnable() {
                override fun run() {
                    removeFlesh()
                }
            }.runTaskLater(MinecraftSoul.instance, 12000/*10분*/)
        } else {
            playerUUID = player.uniqueId
        }
        val inv = mannequin.equipment
        val array : Array<ItemStack?> = arrayOfNulls(45)

        val playerHead = ItemStack(Material.PLAYER_HEAD, 1)
        val meta = playerHead.itemMeta as? SkullMeta
        meta?.owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
        playerHead.itemMeta = meta

        array[0] = playerHead
        array[3] = inv.helmet
        array[4] = inv.chestplate
        array[5] = inv.leggings
        array[6] = inv.boots
        array[8] = inv.itemInOffHand
        for (i in 0..35) {
            if (i < 9) {
                if (i == player.inventory.heldItemSlot) {
                    array[i + 36] = inv.itemInMainHand
                } else {
                    array[i + 36] = player.inventory.contents[i]
                }
            } else {
                array[i] = player.inventory.contents[i]
            }
        }
        inventory.contents = array
    }


    fun removeFlesh() {
        mannequin.remove()
        var inv : PlayerInventory? = null
        for (p in MinecraftSoul.instance.server.onlinePlayers) {
            if (p.uniqueId == playerUUID) {
                p.inventory.clear()
                inv = p.inventory
                break
            }
        }
        if (inv != null) {
            inv.helmet = inventory.contents[3]
            inv.chestplate = inventory.contents[4]
            inv.leggings = inventory.contents[5]
            inv.boots = inventory.contents[6]
            inv.setItemInOffHand(inventory.contents[8])
            for (i in 0..35) {
                if (i < 9) {
                    inv.contents[i] = inventory.contents[i + 36]
                } else {
                    inv.contents[i] = inventory.contents[i]
                }
            }
        } else {
            MinecraftSoul.instance.logger.warning("player's flesh doesn't exist")
        }
    }

}