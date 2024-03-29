@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://repo.c7x.dev/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRI_ARC_REPO_LOGIN")
                        ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRI_ARC_REPO_PASSWORD")
                        ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        maven {
            url = uri("https://repo.c7x.dev/repository/arcades/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRI_ARC_REPO_LOGIN")
                        ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRI_ARC_REPO_PASSWORD")
                        ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
    }

    includeBuild("bundler")

    plugins {
        kotlin("jvm") version "1.6.21"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("org.hidetake.ssh") version "2.10.1"
    }

}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.c7x.dev/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRI_ARC_REPO_LOGIN")
                        ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRI_ARC_REPO_PASSWORD")
                        ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
    }
}



rootProject.name = "towerDefence"

arrayOf(
    "tower-game",
    "tower-game-mod",
    "tower-lobby",
    "commons"
).forEach { include(it) }
