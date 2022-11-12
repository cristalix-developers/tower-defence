package ru.crazylegend.tower

import dev.implario.bukkit.event.on
import dev.implario.games5e.sdk.cristalix.Cristalix
import dev.implario.games5e.node.Game
import me.func.mod.conversation.ModLoader
import me.func.world.MapLoader
import org.bukkit.Location
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.transfer.TransferService
import java.util.*
import org.bukkit.event.block.BlockFadeEvent as BlockFadeEvent

data class TowerDefenceSettings(
    val teams: List<List<UUID>>
)

class TowerDefenceGame(
    gameId: UUID,
    settings: TowerDefenceSettings
): Game(gameId) {

    val map = MapLoader.load("TDSIM", "2")

    val cristalix = Cristalix.connectToCristalix(this, "TWDG", "Tower Defence", true)!!
    val transferService = TransferService(cristalix.client)

    override fun acceptPlayer(event: AsyncPlayerPreLoginEvent) = cristalix.acceptPlayer(event)

    override fun getSpawnLocation(player: UUID): Location = map.getLabel("spawn")!!

    init {
        settings.teams.flatten().chunked(4).forEachIndexed { index, element ->
            after(index * 20L) {
                transferService.transferBatch(element, cristalix.realmId)
            }
        }

        context.on<PlayerJoinEvent> {
            after(5) {
                player.sendMessage("123")
                player.inventory.clear()
                player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
                ModLoader.send("tower-game-mod-bundle", player)
            }
        }

        cristalix.updateRealmInfo()

        context.on<BlockGrowEvent> { isCancelled = true }
        context.on<BlockDispenseEvent> { isCancelled = true }
        context.on<BlockMultiPlaceEvent> { isCancelled = true }
        context.on<BlockFormEvent> { isCancelled = true }
        context.on<BlockFromToEvent> { isCancelled = true }
        context.on<BlockFadeEvent> { isCancelled = true }
        context.on<BlockSpreadEvent> { isCancelled = true }
        context.on<BlockPhysicsEvent> { isCancelled = true  }
        context.on<FoodLevelChangeEvent> { foodLevel = 20 }
        context.on<BlockPlaceEvent> { isCancelled = true }
        context.on<BlockBreakEvent> { isCancelled = true }
        context.on<CraftItemEvent> { isCancelled = true }
        context.on<EntityRegainHealthEvent> { isCancelled = true }
        context.on<BlockPhysicsEvent> { isCancelled = true }
    }
}