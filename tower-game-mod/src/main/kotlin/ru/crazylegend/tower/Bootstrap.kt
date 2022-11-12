package ru.crazylegend.tower

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.crazylegend.tower.conveyor.Conveyor
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.util.*

class Bootstrap : KotlinMod() {

    private var conveyors = mutableMapOf<Int, Conveyor>()

    override fun onEnable() {
        println(123)
        UIEngine.initialize(this)

        registerChannel("tower:conveyor-create") {

            val id = readInt()
            conveyors[id] = Conveyor(
                id,
                readDouble(),
                readDouble(),
                readDouble(),
                readDouble(),
                readDouble(),
                readDouble(),
                readDouble(),
                readDouble()
            )
        }

        registerChannel("tower:conveyor-add") {
            val uid = readInt()
            val standId = UUID.fromString(NetUtil.readUtf8(this))

            val x = readDouble()
            val y = readDouble()
            val z = readDouble()

            conveyors[uid]!!.createStand(standId, x, y, z)
        }

        registerChannel("tower:conveyor-velocity") {

            val id = readInt()
            conveyors[id]!!.velocity = readDouble()
        }

        registerChannel("tower:conveyor-stand") {

            val id = readInt()
            val standId = UUID.fromString(NetUtil.readUtf8(this))

            conveyors[id]!!.removeStand(standId)
        }

        registerChannel("tower:conveyor-color-item") {

            val id = readInt()
            val standId = UUID.fromString(NetUtil.readUtf8(this))

            conveyors[id]!!.changeColor(standId, readInt())
        }

        registerHandler<GameLoop> {

            val now = System.currentTimeMillis().toDouble()

            conveyors.values.forEach {
                if (it.lastTime < 0.025) it.lastTime = now
                if ((now - it.lastTime) / 1000 > 0.025) {
                    it.lastTime = now

                    it.moveRectangles(it.velocity / 40)
                    it.moveStands(it.velocity / 40)
                }
            }
        }
    }
}