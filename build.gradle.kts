import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory)
    `maven-publish`
}

group = "de.dajooo"
version = properties["version"] ?: "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "sonatype-snapshots"
    }
    maven("https://repo.dajooo.de/public-snapshots") {
        name = "dajooo-public-snapshots"
    }
}

dependencies {
    compileOnly(libs.paper.api)
    implementation(libs.kotlin.stdlib)
    implementation(libs.lamp.common)
    implementation(libs.lamp.bukkit)
    implementation(libs.lamp.brigadier)
    compileOnly(libs.inventory.framework.bukkit)
    compileOnly(libs.inventory.framework.paper)
    implementation(libs.mccoroutine.api)
    implementation(libs.mccoroutine.core)
    implementation(libs.coroutines)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.kommons.core)
    implementation(libs.kommons.exposed)
    implementation(libs.kommons.config)
    implementation(libs.kommons.koin)
    implementation(libs.kaper.core)
    implementation(libs.koin.core)
    implementation(libs.koin.slf4j)
    implementation(libs.kotlin.logging)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.semver)
    compileOnly(libs.luckperms)
}

kotlin {
    jvmToolchain(21)
}

tasks {
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    withType<JavaCompile> {
        // Preserve parameter names in the bytecode
        options.compilerArgs.add("-parameters")
    }
    withType<KotlinJvmCompile> {
        compilerOptions {
            javaParameters = true
        }
    }
    runServer {
        minecraftVersion("1.21.4")
    }
}

paperPluginYaml {
    name = "BetterSurvival"
    main = "de.dajooo.bettersurvival.BetterSurvivalPlugin"
    authors.add("dajooo")
    apiVersion = "1.21"
    website = "https://dario.lol"
    dependencies {
        server("LuckPerms", PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
        server("InventoryFramework", PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
    }
}
