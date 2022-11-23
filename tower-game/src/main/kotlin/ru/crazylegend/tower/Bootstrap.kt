package ru.crazylegend.tower

import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.DefaultGameNode
import dev.implario.games5e.node.GameCreator
import dev.implario.games5e.node.linker.SessionBukkitLinker
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.listener
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.GlobalSerializers

class Bootstrap : JavaPlugin(), Listener {

    override fun onEnable() {
        listener(this@Bootstrap)

        val node = DefaultGameNode()
        node.supportedImagePrefixes.add("twd")
        node.linker = SessionBukkitLinker.link(node)
        node.gameCreator = GameCreator { gameId, _, settings ->
            TowerDefenceGame(
                gameId = gameId,
                settings = GlobalSerializers.fromJson(settings, TowerDefenceSettings::class.java)
            )
        }

        val coordinatorClient = CoordinatorClient(node)
        coordinatorClient.enable()

    }

    @EventHandler
    private fun PlayerJoinEvent.join() {
        Bukkit.getScheduler().runTask(this@Bootstrap) {
            player.sendMessage("123")
            player.inventory.clear()
            player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

            val locations = arrayListOf(Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0))

            ModTransfer()
                .integer(locations.size)
                .apply {
                    locations.forEach { v3(it) }
                }.send("tower:locations", player)
        }
    }
}