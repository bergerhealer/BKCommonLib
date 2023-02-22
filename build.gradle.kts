plugins {
    id("java-library")
    id("com.bergerkiller.mountiplex") version "2.92-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

val minecraftVersion = "1.19.3"
val buildNumber = System.getenv("BUILD_NUMBER") ?: "NO-CI"

group = "com.bergerkiller.bukkit"
version = "1.19.3-v3-SNAPSHOT"

repositories {
    mavenLocal {
        // Used to access a server JAR for testing
        // TODO Use Paperclip instead
        content {
            includeGroup("org.spigotmc")
            includeGroup("com.mojang")
        }
    }
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")

    // Repo for TeamBergerhealer plugins, modules and several of its (soft) dependencies. Also used for:
    // - Milkbowl Vault
    // - Comphenix ProtocolLib
    // - Aikar minecraft-timings
    // - Myles ViaVersion
    maven("https://ci.mg-dev.eu/plugin/repository/everything/")
}

// Configuration for shaded dependencies which should not be added to the published Maven .pom
val internal = configurations.create("internal")
configurations {
    compileOnly {
        extendsFrom(internal)
    }
}

dependencies {
    //
    // Server dependencies
    //

    // Spigot API includes the Bukkit API and is what plugins generally use
    compileOnly("org.spigotmc:spigot-api:$minecraftVersion-R0.1-SNAPSHOT")
    // We also depend on netty for the network logic, which is available in public repo
    compileOnly("io.netty:netty-all:4.1.42.Final")
    // Log4j that is used inside the server
    compileOnly("org.apache.logging.log4j:log4j-api:2.17.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.17.0")

    //
    // Dependencies shaded into the library for internal use
    //

    // Mountiplex is included in BKCommonLib at the same package
    api("com.bergerkiller.mountiplex:Mountiplex:2.92-SNAPSHOT")
    // Region change tracker is included in BKCommonLib for the region block change event
    internal("com.bergerkiller.bukkit.regionchangetracker:BKCommonLib-RegionChangeTracker-Core:1.2")
    // Aikar's minecraft timings library, https://github.com/aikar/minecraft-timings
    internal("co.aikar:minecraft-timings:1.0.4") {
        isTransitive = false
    }
    // GSON isn't available in spigot versions prior to 1.8.1, shade it in order to keep 1.8 compatibility
    internal("com.google.code.gson:gson:2.8.9")

    //
    // Optional provided dependencies that BKCommonLib can talk with
    //

    // ViaVersion API
    compileOnly("us.myles:viaversion:3.2.1")
    // Vault hook for special permissions handling
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    // ProtocolLib hook for protocol handling
    compileOnly("com.comphenix.protocol:ProtocolLib-API:4.4.0")

    //
    // Cloud command framework
    // Is relocated - requires appropriate relocation in plugins using it
    //
    internal("org.bergerhealer.cloud.commandframework:cloud-paper:1.8.0-SNAPSHOT")
    internal("org.bergerhealer.cloud.commandframework:cloud-annotations:1.8.0-SNAPSHOT")
    internal("org.bergerhealer.cloud.commandframework:cloud-minecraft-extras:1.8.0-SNAPSHOT")
    internal("me.lucko:commodore:1.13") {
        isTransitive = false
    }
    internal("net.kyori:adventure-api:4.12.0")
    internal("net.kyori:adventure-platform-bukkit:4.2.0")

    //
    // Test dependencies
    //

    testImplementation("org.spigotmc:spigot:$minecraftVersion-R0.1-SNAPSHOT")
    testImplementation("org.mockito:mockito-core:2.22.0")
    testImplementation("junit:junit:4.13")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven("https://ci.mg-dev.eu/plugin/repository/everything") {
            name = "MGDev"
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

mountiplex {
    generateTemplateHandles()
    remapAnnotationStrings()
}

tasks {
    generateTemplateHandles {
        source.set("com/bergerkiller/templates/init.txt")
        target.set("com/bergerkiller/generated")
        variables.put("version", minecraftVersion)
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<JavaCompile>().configureEach {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    javadoc {
        options.encoding = "UTF-8"
        // TODO fix those errors
        isFailOnError = false
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
    }

    processResources {
        from("src/main/templates")
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(
                "version" to version,
                "build" to buildNumber,
                "url" to "https://github.com/bergerhealer/BKCommonLib",
                "authors" to "bergerkiller, lenis0012, timstans, bubba1234119, KamikazePlatypus, mg_1999, Friwi"
            )
        }
    }

    shadowJar {
        val prefix = "com.bergerkiller.bukkit.common.dep"
        relocate("co.aikar.timings.lib", "$prefix.timingslib")
        relocate("com.google.gson", "$prefix.gson")

        // Cloud command framework and its dependencies
        relocate("cloud.commandframework", "$prefix.cloud")
        relocate("io.leangen.geantyref", "$prefix.typetoken")
        relocate("me.lucko.commodore", "$prefix.me.lucko.commodore")
        relocate("net.kyori", "$prefix.net.kyori")

        // Mountiplex and its dependencies
        val mountiplexPrefix = "com.bergerkiller.mountiplex.dep"
        relocate("org.objectweb.asm", "$mountiplexPrefix.org.objectweb.asm")
        relocate("org.objenesis", "$mountiplexPrefix.org.objenesis")
        relocate("javassist", "$mountiplexPrefix.javassist")

        configurations.add(internal)

        dependencies {
            exclude(dependency("org.apiguardian:apiguardian-api"))
            exclude(dependency("org.checkerframework:checker-qual"))
        }

        destinationDirectory.set(buildDir)
        archiveFileName.set("${project.name}-${project.version}-$buildNumber.jar")

        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
