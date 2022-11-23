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

tasks.register("upload-stack") {
    group = "development"

    dependsOn(getAllTasks(true)[project("tower-game")]?.filter { it.name == "upload-game" })
    dependsOn(getAllTasks(true)[project("tower-game-mod")]?.filter { it.name == "upload-game-mod" })
    dependsOn(getAllTasks(true)[project("tower-lobby")]?.filter { it.name == "upload-game-lobby" })
}