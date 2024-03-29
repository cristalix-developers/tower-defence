import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler

plugins {
    id("com.github.johnrengelman.shadow")
    id("org.hidetake.ssh")
}

var remote: Any? = null

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("cristalix:dark-paper:21.02.03")
    compileOnly("ru.cristalix.core:bukkit-api:1.0.16-SNAPSHOT")

    implementation("clepto:clepto-bukkit:3.4.2")
    implementation("clepto:clepto-cristalix:3.0.2")

    implementation("dev.implario:kensuke-client-bukkit:2.1.10")
    implementation("dev.implario.bukkit:bukkit-tools:4.4.12")
    implementation("dev.implario.bukkit:dark-paper:1.0.0")
    implementation("ru.cristalix:boards-bukkit-api:3.0.15")
    implementation("me.func:visual-driver:3.2.7.RELEASE")
    implementation("implario:bukkit-worker-core:2.1.20")
    implementation("dev.implario.bukkit:kotlin-api:1.1.1")
    implementation("ru.cristalix:boards-bukkit-api:3.0.15")
    implementation("me.func:nightingale-api:1.0.13")

    implementation(project(":commons"))
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
    jar { enabled = false }
    build { dependsOn(shadowJar) }
    shadowJar { archiveFileName.set("lobby-server-td.jar") }

    register("upload-lobby") {
        group = "development"
        doLast {
            ssh.run(delegateClosureOf<RunHandler> {
                session(remote, delegateClosureOf<SessionHandler> {
                    put(
                        hashMapOf(
                            "from" to getByName<ShadowJar>("shadowJar").archiveFile.get().asFile,
                            "into" to "/home/crazylegend/towerDefence-lobby/plugins"
                        )
                    )
                })
            })
        }
        dependsOn(shadowJar)
    }
}