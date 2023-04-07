plugins {
    id("java-library")
    id("com.bergerkiller.mountiplex") version "2.93"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

val buildNumber = System.getenv("BUILD_NUMBER") ?: "NO-CI"

group = "com.bergerkiller.bukkit"
version = "1.19.4-v1"

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
    compileOnly(libs.spigot.api)
    // We also depend on netty for the network logic, which is available in public repo
    compileOnly(libs.netty.all)
    // Log4j that is used inside the server
    compileOnly(libs.log4j.api)
    compileOnly(libs.log4j.core)

    //
    // Dependencies shaded into the library for internal use
    //

    // Mountiplex is included in BKCommonLib at the same package
    api(libs.mountiplex)
    // Region change tracker is included in BKCommonLib for the region block change event
    api(libs.regionchangetracker)
    // Aikar's minecraft timings library, https://github.com/aikar/minecraft-timings
    internal(libs.timings) {
        isTransitive = false
    }
    // GSON isn't available in spigot versions prior to 1.8.1, shade it in order to keep 1.8 compatibility
    internal(libs.gson)

    //
    // Optional provided dependencies that BKCommonLib can talk with
    //

    // ViaVersion API
    compileOnly(libs.viaversion)
    // Vault hook for special permissions handling
    compileOnly(libs.vault)
    // ProtocolLib hook for protocol handling
    compileOnly(libs.protocollib)

    //
    // Cloud command framework
    // Is relocated - requires appropriate relocation in plugins using it
    //
    internal(libs.cloud.paper)
    internal(libs.cloud.annotations)
    internal(libs.cloud.minecraft.extras)
    internal(libs.commodore) {
        isTransitive = false
    }
    internal(libs.adventure.api)
    internal(libs.adventure.platform.bukkit)

    //
    // Test dependencies
    //

    testImplementation(libs.spigot)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
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
        variables.put("version", libs.versions.minecraft)
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
        isFailOnError = true
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
