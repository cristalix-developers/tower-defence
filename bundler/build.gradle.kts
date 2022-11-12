plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("kamillaova.proguard:proguard-gradle:7.2.2-SNAPSHOT")
}

gradlePlugin {
    plugins {
        create("Anime Mod Bundler") {
            id = "anime.mod-bundler"
            implementationClass = "anime.modbundler.ModBundlerPlugin"
        }
    }
}