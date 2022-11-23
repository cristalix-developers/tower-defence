package ru.crazylegend.tower

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.item.ItemBuilder
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.command
import me.func.mod.util.listener
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.lobby.ILobbyService
import ru.cristalix.core.lobby.LobbyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import java.util.UUID

class Bootstrap : JavaPlugin(), Listener {

    lateinit var worldMeta: WorldMeta

    override fun onEnable() {
        listener(this@Bootstrap)

        B.plugin = this@Bootstrap
        Anime.include(Kit.STANDARD, Kit.EXPERIMENTAL, Kit.NPC, Kit.DIALOG, Kit.LOOTBOX)
        Games5eQueue.register()

        Platforms.set(PlatformDarkPaper())

        worldMeta = WorldMeta(Cristalix.loadMap("TDSIM", "2"))

        val core = CoreApi.get()
        core.registerService(ILobbyService::class.java, LobbyService())

        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.maxPlayers = 150
        info.readableName = "§cTowerDefence Lobby #${info.realmId.id}"
        info.groupName = "TWD"
        info.isLobbyServer = true
        info.servicedServers = arrayOf("TWD")
        info.saveRealm = info.realmId

        val lobbyService = ILobbyService.get()
        lobbyService.setItem(0, ItemBuilder(Material.COMPASS).text("§eВойти в очередь").build(), { true }) {
            Games5eQueue.joinQueue(it)
        }
        lobbyService.addSpawnLocation(worldMeta.getLabel("conveyor"))

        ModLoader.loadAll("mods")
        ModLoader.onJoining("tower-game-mod-bundle")

        command("spawn") { player, args ->
            val entityType = EntityType.valueOf(args[0])
            val velocity  = args[1].toDouble()
            val uuid = UUID.randomUUID().toString()
            if (!EntityType.values().contains(entityType)) return@command

            ModTransfer()
                .string(uuid)
                .integer(entityType.typeId.toInt())
                .double(velocity)
                .long(System.currentTimeMillis())
                .send("tower:spawn-mob", player)
        }
    }

    @EventHandler
    private fun PlayerJoinEvent.join() {
        B.postpone(20) {
            val conveyorsLabels = worldMeta.getLabels("conveyor")
            conveyorsLabels.sortBy { it.tagInt }

            ModTransfer()
                .integer(conveyorsLabels.size).apply {
                    conveyorsLabels.forEach { v3(it.x + 0.5, it.y, it.z + 0.5) }
                }
                .send("tower:create-tapeline", player)
        }
    }
}