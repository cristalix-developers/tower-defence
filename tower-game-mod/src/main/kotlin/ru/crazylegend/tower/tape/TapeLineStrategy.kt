package ru.crazylegend.tower.tape

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.crazylegend.tower.V3
import ru.crazylegend.tower.mod
import java.util.*
import kotlin.math.atan2

object TapeLineStrategy {

    private lateinit var spawnLocation: V3

    private val tapeLocations: MutableList<V3> = mutableListOf()
    private val tapeLineMobs: MutableMap<UUID, TapeLineMob> = mutableMapOf()

    private var lastTime = 0.0

    init {
        mod.registerChannel("tower:create-tapeline") {
            val sizeArray = readInt()

            repeat(sizeArray) {
                val x = readDouble()
                val y = readDouble()
                val z = readDouble()
                val mobLocation = V3(x, y, z)
                tapeLocations.add(mobLocation)
            }

            spawnLocation = V3(
                tapeLocations.first().x, tapeLocations.first().y, tapeLocations.first().z
            )
        }

        mod.registerChannel("tower:spawn-mob") {

            val mobUuid = UUID.fromString(NetUtil.readUtf8(this))
            val mobType = readInt()
            val mobVelocity = readDouble()
            val mobTimeSpawn = readLong()

            tapeLineMobs[mobUuid] = TapeLineMob(mobUuid, mobType, mobVelocity, mobTimeSpawn)

            if (!tapeLineMobs.contains(mobUuid)) return@registerChannel
            tapeLineMobs[mobUuid]?.createMob(mobUuid, spawnLocation)
        }

        mod.registerHandler<GameLoop> {
            val now = System.currentTimeMillis().toDouble()

            tapeLineMobs.values.forEach { lineMob ->
                lineMob.tapeLineMobCompanion.entityLineMobs.values.forEach { entity ->
                    val position = lineMob.getPosition(
                        tapeLocations,
                        now - lineMob.mobTimeSpawn,
                        lineMob.mobVelocity
                    )

                    val rotation = Math.toDegrees(
                        -atan2(position.x - entity.x, position.z - entity.z)
                    ).toFloat()

                    entity.rotationYawHead = rotation
                    entity.setYaw(rotation)
                    entity.teleport(position.x, position.y, position.z)
                }
            }
        }
    }
}