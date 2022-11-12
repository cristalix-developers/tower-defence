package ru.crazylegend.tower.condition

import dev.implario.bukkit.event.EventContext
import ru.crazylegend.tower.TowerDefenceGame

interface Condition {

    val duration: Int
    val game: TowerDefenceGame
    val fork: EventContext

    fun enterCondition()
    fun leaveCondition() { fork.unregisterAll() }
}