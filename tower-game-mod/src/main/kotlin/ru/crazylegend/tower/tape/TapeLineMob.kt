package ru.crazylegend.tower.tape

import dev.xdark.clientapi.entity.EntityLivingBase
import ru.crazylegend.tower.V3
import ru.cristalix.uiengine.UIEngine
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

data class TapeLineMob(
    val uuid: UUID,
    var mobType: Int,
    var mobVelocity: Double,
    var mobTimeSpawn: Long
) {

    var tapeLineMobCompanion = Companion
    companion object {
        val entityLineMobs: MutableMap<UUID, EntityLivingBase> = mutableMapOf()
    }
    fun createMob(uuid: UUID, spawnLocation: V3) {
        val mob = UIEngine.clientApi.entityProvider().newEntity(mobType, UIEngine.clientApi.minecraft().world)
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        mob.teleport(spawnLocation.x, spawnLocation.y, spawnLocation.z)
        entityLineMobs[uuid] = mob as EntityLivingBase
    }

    fun getPosition(mobLocations: MutableList<V3>, timeFromSpawn: Double, velocity: Double): V3 {
        var firstLocation = mobLocations.first()
        var timeTotal = timeFromSpawn
        mobLocations.drop(1).forEach { nextLocation ->
            val timeForThisLine =
                sqrt((firstLocation.x - nextLocation.x).pow(2.0) + (firstLocation.z - nextLocation.z).pow(2.0)) / velocity
            if (timeTotal > timeForThisLine) {
                firstLocation = nextLocation
                timeTotal -= timeForThisLine
                return@forEach
            }
            val percent = timeTotal / timeForThisLine
            val dX = nextLocation.x - firstLocation.x
            val dZ = nextLocation.z - firstLocation.z
            return V3(firstLocation.x + dX * percent, firstLocation.y, firstLocation.z + dZ * percent)
        }
        return firstLocation
    }
}