# Using Cloud Command Framework
BKCommonLib shades in the [Cloud Command Framework](https://github.com/Incendo/cloud "Cloud Command Framework") library so that depending plugins can use it. If you are new to Cloud, look through their [documentation](https://cloud.incendo.org/ "documentation") first.

### Technical
Cloud is shaded at a different package path (to avoid conflicts), so using the integrated cloud requires remapping it using the maven shade or gradle shadow plugin. BKCommonLib has a BOM (Bill of materials) hosted on the same repository BKCommonLib is, that includes information about the cloud dependency and its version. The below maven and gradle configurations make use of this.

Incendo Cloud jars can be found on the maven central repository, so they should be available automatically. The ci.mg-dev.eu repository proxies and caches the Cloud dependencies too.

### Version
Currently the 2.0.0 beta/release-candidate of Cloud is included.

### Maven
BKCommonLib has a BOM (Bill of materials) hosted on the same repository BKCommonLib is, that includes information about the cloud dependency and its version. The below snippets are for your project's `pom.xml`.

Begin by adding the repository where BKCommonLib is hosted:
```xml
    <repositories>
        <repository>
            <id>MG-Dev Jenkins CI Maven Repository</id>
            <url>https://ci.mg-dev.eu/plugin/repository/everything</url>
        </repository>
    </repositories>
```
Next, add BKCommonLib-bom under **dependencyManagement** (!). This automatically loads the information about what cloud version and its dependencies are used for a particular version of BKCommonLib. Next just add all the dependencies without specifying the version. The version is taken from the bom.
```xml
    <properties>
        <project.bkcommonlib.version>1.21.1-v1</project.bkcommonlib.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.bergerkiller.bukkit</groupId>
                <artifactId>BKCommonLib-bom</artifactId>
                <version>${project.bkcommonlib.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>com.bergerkiller.bukkit</groupId>
            <artifactId>BKCommonLib</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- Cloud Command Framework -->
        <dependency>
            <groupId>org.incendo</groupId>
            <artifactId>cloud-paper</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.incendo</groupId>
            <artifactId>cloud-annotations</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.incendo</groupId>
            <artifactId>cloud-minecraft-extras</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```
However, the library and its dependencies are shaded in at a different package than normal. So, you will have to include the maven shade plugin in your project to relocate them. Not doing so will cause a failure at runtime.
```xml
    <build>
        <plugins>
            <!-- Relocates references to the Cloud command framework to where they are in BKCommonLib -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.3</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <createDependencyReducedPom>false</createDependencyReducedPom>
                    <relocations>
                        <!-- BKCommonLib relocations of Cloud command framework -->
                        <relocation>
                            <pattern>org.incendo.cloud</pattern>
                            <shadedPattern>com.bergerkiller.bukkit.common.dep.cloud</shadedPattern>
                        </relocation>
                        <relocation>
                            <pattern>io.leangen.geantyref</pattern>
                            <shadedPattern>com.bergerkiller.bukkit.common.dep.typetoken</shadedPattern>
                        </relocation>
                        <relocation>
                            <pattern>me.lucko.commodore</pattern>
                            <shadedPattern>com.bergerkiller.bukkit.common.dep.me.lucko.commodore</shadedPattern>
                        </relocation>
                        <relocation>
                            <pattern>net.kyori</pattern>
                            <shadedPattern>com.bergerkiller.bukkit.common.dep.net.kyori</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### Gradle (Kotlin)
Depending on BKCommonLib will automatically tell Gradle what versions of Cloud and its dependencies to use. The below configuration assumes kotlin gradle syntax, and are for your project's `build.gradle.kts` file.

This does not work well with older versions of Gradle. Make sure to use **gradle 8.5** at least.


Begin by adding the repository where BKCommonLib is hosted:
```kotlin
repositories {
    maven("https://ci.mg-dev.eu/plugin/repository/everything/")
}
```
Next add the BKCommonLib dependency and the cloud dependencies. For cloud, no version has to be specified, which will be provided by BKCommonLib. It is recommended to make use of [version catalogs](https://docs.gradle.org/current/userguide/platforms.html#sub::toml-dependencies-format) to make this look cleaner, but this is optional.
```kotlin
dependencies {
    compileOnlyApi("com.bergerkiller.bukkit:BKCommonLib:1.21.1-v1")

    // Cloud integrated in BKCommonLib
    compileOnly("org.incendo:cloud-paper")
    compileOnly("org.incendo:cloud-annotations")
    compileOnly("org.incendo:cloud-minecraft-extras")
}
```
Finally, make sure the cloud dependency is shaded correctly when compiling your plugin:
Also, don't forget to add the Java library.
```kotlin
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java-library")
}

tasks {
    shadowJar {
        val commonPrefix = "com.bergerkiller.bukkit.common.dep"
        relocate("org.incendo.cloud", "$commonPrefix.cloud")
        relocate("io.leangen.geantyref", "$commonPrefix.typetoken")
        relocate("me.lucko.commodore", "$commonPrefix.me.lucko.commodore")
        relocate("net.kyori", "$commonPrefix.net.kyori")
    }
}
```

### Gradle (Groovy)
While using `build.gradle`, you will have a small difference in syntax.

Add the following plugins (`java` should be there already when creating your project):
```kotlin
plugins {
    id "java"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id "java-library"
}
```

Then you will add the maven repository for BKCommonlib:
```kotlin
repositories {
    mavenCentral()
    maven {
        url = "https://ci.mg-dev.eu/plugin/repository/everything"
    }
}
```

Then add it to the dependencies:
```kotlin
dependencies {
    // BKCommonLib
    compileOnlyApi("com.bergerkiller.bukkit:BKCommonLib:1.21.1-v1")

    // Cloud integrated in BKCommonLib
    compileOnly("org.incendo:cloud-paper")
    compileOnly("org.incendo:cloud-annotations")
    compileOnly("org.incendo:cloud-minecraft-extras")

    // Adventure MiniMessage
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}
```

Finally, you can shadow the imported plugins:
```kotlin
tasks {
    shadowJar {
        def commonPrefix = "com.bergerkiller.bukkit.common.dep"
        relocate("org.incendo.cloud", "${commonPrefix}.cloud")
        relocate("io.leangen.geantyref", "${commonPrefix}.typetoken")
        relocate("me.lucko.commodore", "${commonPrefix}.me.lucko.commodore")
        relocate("net.kyori", "${commonPrefix}.net.kyori")
    }
}


