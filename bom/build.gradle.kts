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
        api(libs.adventure.api)
        api(libs.adventure.platform.bukkit)
    }
}
