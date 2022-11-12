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
import me.func.mod.util.listener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.lobby.ILobbyService
import ru.cristalix.core.lobby.LobbyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import java.util.*

class Bootstrap : JavaPlugin() {

    override fun onEnable() {
        B.plugin = this@Bootstrap
        Anime.include(Kit.STANDARD, Kit.EXPERIMENTAL, Kit.NPC, Kit.DIALOG, Kit.LOOTBOX)
        Games5eQueue.register()

        Platforms.set(PlatformDarkPaper())

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
    }
}