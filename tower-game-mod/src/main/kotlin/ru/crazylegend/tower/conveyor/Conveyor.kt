package ru.crazylegend.tower.conveyor

import dev.xdark.clientapi.entity.EntityArmorStand
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import java.util.*
import java.util.concurrent.ThreadLocalRandom

data class Conveyor(
    val id: Int,
    var startX: Double,
    var startY: Double,
    var startZ: Double,
    var finishX: Double,
    var finishY: Double,
    var finishZ: Double,
    var velocity: Double,
    var distance: Double
) {

    var stands = hashMapOf<UUID, EntityArmorStand>()
    var rectangles = arrayListOf<RectangleElement>()

    private val xDirection = when {
        startX > finishX -> -1.0
        startX < finishX -> 1.0
        else -> 0.0
    }
    private val zDirection = when {
        startZ > finishZ -> -1.0
        startZ < finishZ -> 1.0
        else -> 0.0
    }

    var lastTime = 0.0

    init {
        spawnRectangles()
    }

    private fun createRectangle(x: Double, y: Double, z: Double) {

        val rectangle = rectangle {
            textureLocation = ResourceLocation.of("cache/animation", "conveyer.png")
            size = V3(48.0, 48.0)
            origin = V3(0.335 + 0.335 / 2, 0.335 + 0.335 / 2)
            color = Color(150, 150, 150)

            rotation = Rotation(Math.PI * 3 / 2, 1.0, 0.0, 0.0)
        }

        val context = Context3D(V3()).apply {
            offset.x = x
            offset.z = z
            offset.y = y + 0.01

            rotation = Rotation(Math.PI * 2 * kotlin.math.abs(zDirection) / 4, 0.0, 1.0, 0.0)
            +rectangle
        }

        UIEngine.worldContexts.add(context)
        rectangles.add(rectangle)
    }

    fun changeColor(uuid: UUID, color: Int) {

        stands[uuid]!!.armorInventoryList.forEach {
            it.tagCompound = NBTTagCompound.of().apply { put("color", color) }
        }
    }

    fun createStand(uuid: UUID, x: Double, y: Double, z: Double) {

        val stand = UIEngine.clientApi.entityProvider()
            .newEntity(EntityProvider.ARMOR_STAND, UIEngine.clientApi.minecraft().world) as EntityArmorStand

        stand.setNoGravity(true)
        stand.setYaw(180.0F)
        stand.isInvisible = false

        UIEngine.clientApi.minecraft().world.spawnEntity(stand)
        stand.teleport(x, y, z)

        stands[uuid] = stand
    }


    private fun spawnRectangles() {

        val count = ((distance + 3) / 3).toInt()
        val x = if (xDirection == 0.0) 0.0 else 2.999
        val z = if (zDirection == 0.0) 0.0 else 2.999

        repeat(count) {
            createRectangle(startX + (it * x * xDirection), startY - (it * 0.0001), startZ + (it * z * zDirection))
        }
    }

    fun moveStands(v: Double) {

        stands.values.forEach { stand ->

            when {
                stand.world.getBlockState(stand.x, stand.y + 1.30, stand.z).id == 0 -> stand.teleport(stand.x, stand.y - (v * 3), stand.z)

                stand.x - finishX !in -v..v -> stand.teleport(stand.x + v * xDirection, stand.y, stand.z)

                stand.z - finishZ !in -v..v -> stand.teleport(stand.x, stand.y, stand.z + v * zDirection)
            }
        }
    }

    fun moveRectangles(v: Double) {

        rectangles.forEach { rectangle ->

            val needDirection = when {
                zDirection == 0.0 -> xDirection
                xDirection == 0.0 -> -zDirection
                else -> 0.0
            }

            rectangle.animate(0.025) {
                rectangle.textureFrom.x -= v * needDirection / 3
            }
        }
    }

    fun removeStand(uuid: UUID) {

        stands[uuid]!!.world.removeEntity(stands[uuid]!!)
    }

}
