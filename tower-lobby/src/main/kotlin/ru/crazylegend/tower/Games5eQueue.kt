package ru.crazylegend.tower

import clepto.bukkit.B
import clepto.bukkit.routine.Do.after
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.NoopGameNode
import dev.implario.games5e.packets.PacketOk
import dev.implario.games5e.packets.PacketQueueEnter
import dev.implario.games5e.packets.PacketQueueLeave
import me.func.mod.Anime
import me.func.mod.ui.menu.queue.QueueView
import me.func.mod.ui.menu.queue.QueueViewer
import me.func.mod.util.after
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.lib.Futures
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

object Games5eQueue {

    private val client = CoordinatorClient(NoopGameNode())
    private val queueId = UUID.fromString("0ba9c870-c7e7-413a-85e5-b9adc856bf3d")
    private var maxPlayers = 0
    private val queueView = QueueView().apply {
        icon = "https://storage.c7x.dev/topil_ov/scp/icon.png"
        description = "§7В очереди §8» §fcurrent \n§7Осталось §8» §frequired"
        title = "§cTowerDefence: "
        onLeave { player ->
            leaveQueue(player)
        }
    }

    fun joinQueue(player: Player) {
        val queueOnline = client.queueOnline
        if (QueueViewer.views.contains(player.uniqueId)) {
            leaveQueue(player)
            Anime.killboardMessage(player, Formatting.error("Вы вышли с очереди"))
            return
        }
        if (!queueOnline.containsKey(queueId)) {
            Anime.killboardMessage(player, Formatting.error("Нет такой очереди"))
            return
        }

        Futures.timeout(
            client.client.send(
                PacketQueueEnter(
                    queueId,
                    arrayListOf(player.uniqueId), false, true,
                    HashMap()
                )
            ).awaitFuture(PacketOk::class.java), 1, TimeUnit.SECONDS
        ).whenComplete { _, err1 ->
            if (err1 != null) {
                err1.printStackTrace()
                player.sendMessage("§cОшибка: " + err1::class.java.simpleName)
            } else {
                Anime.killboardMessage(player, Formatting.fine("Вы добавлены в очередь!"))
                queueView.open(player)
            }
        }
    }

    fun register() {
        client.listenQueues()
        client.enable()
        after(10) {
            maxPlayers =
                client.allQueues.find { it.properties.queueId == queueId }?.properties?.globalMapDefinition?.size?.max
                    ?: 32
        }

        Bukkit.getScheduler().runTaskTimer(B.plugin, {
            Bukkit.getOnlinePlayers().forEach { player ->
                val online = getQueueOnline()
                QueueView().update(
                    player, queueView.description
                        .replace("current", online.toString())
                        .replace("required", (maxPlayers - online).toString())
                )
            }
        }, 10, 10)

        Bukkit.getScheduler().runTaskTimer(B.plugin, {
            val queue = client.allQueues.find { it.properties.queueId == queueId }
            val maxNow = queue?.properties?.globalMapDefinition?.size?.max ?: return@runTaskTimer
            if (maxNow != maxPlayers) maxPlayers = maxNow
        }, 20, 100)
    }


    fun leaveQueue(player: Player) {
        client.client.send(PacketQueueLeave(Collections.singletonList(player.uniqueId)))
        QueueViewer.views.remove(player.uniqueId)
        QueueView().close(player)
    }

    fun getQueueOnline() = client.queueOnline[queueId]  ?: 0
}