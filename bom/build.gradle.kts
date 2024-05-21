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

dependencies {
    constraints {
        api("org.bergerhealer.cloud.commandframework:cloud-paper:1.8.4")
        api("org.bergerhealer.cloud.commandframework:cloud-annotations:1.8.4")
        api("org.bergerhealer.cloud.commandframework:cloud-minecraft-extras:1.8.4")
    }
}
