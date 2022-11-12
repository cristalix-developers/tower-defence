plugins {
    id("org.jetbrains.kotlin.jvm") apply false
}

allprojects {
    group = "ru.crazylegend.tower.Bootstrap"
    version = "1.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
}


// TODO(): 1. Настроить игру гейм сервиса. 2. Сделать мобов убиваемыми. 3. Сделать оружие игрока
