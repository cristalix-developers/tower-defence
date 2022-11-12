package ru.crazylegend.tower

import clepto.bukkit.B
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.DefaultGameNode
import dev.implario.games5e.node.GameCreator
import dev.implario.games5e.node.linker.SessionBukkitLinker
import me.func.mod.conversation.ModLoader
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.GlobalSerializers

class Bootstrap : JavaPlugin() {

    override fun onEnable() {
        B.plugin = this

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

        ModLoader.loadAll("mods")
    }
}