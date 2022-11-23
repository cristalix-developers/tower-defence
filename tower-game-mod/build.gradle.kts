import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler

plugins {
    id("anime.mod-bundler")
    id("org.hidetake.ssh")
}

var remote: Any? = null

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("ru.cristalix:client-api:10.2-SNAPSHOT")
    implementation("ru.cristalix:client-sdk:10.2-SNAPSHOT")
    implementation("ru.cristalix:uiengine:10.2-SNAPSHOT")
    implementation("implario:humanize:1.1.3")

    implementation(project(":commons"))
}

mod {
    name = "TowerDefence"
    main = "ru.crazylegend.tower.Bootstrap"
    author = "CrazyLegend_"
}

remotes {
    remote = withGroovyBuilder {
        "create"("mokou") {
            setProperty("host", "mokou.dedi.c7x.dev")
            setProperty("user", "crazylegend")
            setProperty("identity", file(System.getenv("PRIVATE_KEY")))
            setProperty("knownHosts", AllowAnyHosts.instance)
        }
    }
}

tasks {
    register("upload-game-mod") {
        group = "development"
        doLast {
            ssh.run(delegateClosureOf<RunHandler> {
                session(remote, delegateClosureOf<SessionHandler> {
                    put(hashMapOf("from" to proguardJar.get().outputFile.get().asFile, "into" to "/home/crazylegend/towerDefence-lobby/mods"))
                })
            })
        }
        dependsOn(proguardJar)
    }
}