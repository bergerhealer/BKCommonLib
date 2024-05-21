plugins {
    id("java-platform")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            groupId = "com.bergerkiller.bukkit"
            artifactId = "BKCommonLib-bom"
            version = "1.20.6-v1-SNAPSHOT"
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
