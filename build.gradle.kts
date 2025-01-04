import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    kotlin("plugin.serialization") version "2.1.20-Beta1"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
    `maven-publish`
}

group = "de.dajooo"
version = System.getenv("VERSION") ?: "0.1.0-SNAPSHOT"

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
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.2")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.2")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-rc.2")
    implementation("me.devnatan:inventory-framework-platform-bukkit:3.2.0")
    implementation("me.devnatan:inventory-framework-platform-paper:3.2.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("de.dajooo:kommons-core:0.1.0-SNAPSHOT")
    implementation("de.dajooo:kommons-exposed:0.1.0-SNAPSHOT")
    implementation("de.dajooo:kommons-config:0.1.0-SNAPSHOT")
    implementation("de.dajooo:kommons-koin:0.1.0-SNAPSHOT")
    implementation("de.dajooo:kaper-core:0.1.0-SNAPSHOT")
    implementation("io.insert-koin:koin-core:4.1.0-Beta1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.0-Beta1")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    compileOnly("net.luckperms:api:5.4")
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
    }
}

publishing {
    repositories {
        val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")
        maven {
            name = if(isSnapshot) "dajoooPublicSnapshots" else "dajoooPublicReleases"
            url = uri(if(isSnapshot) {
                "https://repo.dajooo.de/public-snapshots"
            } else {
                "https://repo.dajooo.de/releases"
            })
            credentials {
                username = (System.getenv("MAVEN_USERNAME") ?: findProperty("mavenUsername")).toString()
                password = (System.getenv("MAVEN_PASSWORD") ?: findProperty("mavenPassword")).toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = project.name
            groupId = project.group.toString()
            version = project.version.toString()
        }
    }
}