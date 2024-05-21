plugins {
    id("java-platform")
    id("maven-publish")
}

publishing {
    publications {
        repositories {
            maven("https://ci.mg-dev.eu/plugin/repository/everything") {
                name = "MGDev"
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }

        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
        }
    }
}

javaPlatform.allowDependencies()

dependencies {
    api(platform(libs.cloud.core.bom))
    api(platform(libs.cloud.minecraft.bom))

    constraints {
        // Adding BKCommonLib itself keeps maven happy. It's not needed with gradle.
        // It's a bit of a strange circular thing though. Ugh.
        api("$group:BKCommonLib:$version")

        api(libs.adventure.api)
        api(libs.adventure.platform.bukkit)
    }
}
