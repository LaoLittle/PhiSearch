plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "org.laolittle.plugin"
version = "1.0.1"

repositories {
    mavenCentral()
}
