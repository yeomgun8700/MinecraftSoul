package org.yeomgun.minecraftSoul

import org.bukkit.GameRule
import org.bukkit.plugin.java.JavaPlugin
import org.yeomgun.minecraftSoul.flesh.FleshManager


class MinecraftSoul : JavaPlugin() {

    companion object {
        lateinit var instance : MinecraftSoul
            private set
    }

    override fun onEnable() {
        instance = this
        server.pluginManager.registerEvents(FleshManager, this)
        for (world in server.worlds) {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
