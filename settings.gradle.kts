rootProject.name = "BKCommonLib"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://ci.mg-dev.eu/plugin/repository/everything/")
        mavenLocal {
            content {
                includeGroup("com.bergerkiller.mountiplex")
            }
        }
    }
}
