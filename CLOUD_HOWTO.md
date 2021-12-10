# Using Cloud Command Framework
BKCommonLib shades in a custom fork of the [Cloud Command Framework](https://github.com/bergerhealer/cloud "Cloud Command Framework") library so that depending plugins can use it. If you are new to Cloud, look through their [documentation](https://incendo.github.io/cloud/ "documentation") first.

### Version
Currently the 1.6.0 release of Cloud is included.

### Maven
The bergerhealer cloud fork is hosted on the same repository as BKCommonLib:
```xml
    <repository>
        <id>MG-Dev Jenkins CI Maven Repository</id>
        <url>https://ci.mg-dev.eu/plugin/repository/everything</url>
    </repository>
```
Depend on BKCommonLib, and the Cloud library is transitively included:
```xml
        <dependency>
            <groupId>com.bergerkiller.bukkit</groupId>
            <artifactId>BKCommonLib</artifactId>
            <version>1.18.1-v1</version>
            <scope>provided</scope>
        </dependency>
```
However, the library and its dependencies are shaded in at a different package than normal. So, you will have to include the maven shade plugin in your project to relocate them. Not doing so will cause a failure at runtime.
```xml
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
                            <pattern>cloud.commandframework</pattern>
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
        </plugins>```