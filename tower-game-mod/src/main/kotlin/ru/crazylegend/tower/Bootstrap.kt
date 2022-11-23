package ru.crazylegend.tower

import ru.crazylegend.tower.tape.TapeLineStrategy
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

lateinit var mod: Bootstrap

class Bootstrap : KotlinMod() {

    override fun onEnable() {
        mod = this@Bootstrap
        UIEngine.initialize(this)

        TapeLineStrategy
    }
}
