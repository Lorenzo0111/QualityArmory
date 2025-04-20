# Adding the Dependency

In order to use the QualityArmory API you need to add the plugin as a dependency. Replace VERSION with the latest version:

<figure><img src="https://img.shields.io/github/v/release/Lorenzo0111/QualityArmory" alt="If this does not load, enable JavaScript on your browser" width="188"><figcaption></figcaption></figure>

### Java Dependency

{% tabs %}
{% tab title="Maven" %}
To add QA to your project using maven, copy the following into the POM.xml.

```xml
<repository>
    <id>codemc-repo</id>
    <url>https://repo.codemc.io/repository/maven-public/</url>
</repository>

<dependency>
    <groupId>me.zombie_striker</groupId>
    <artifactId>QualityArmory</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```
{% endtab %}

{% tab title="Gradle" %}
To add QA to your project using gradle, copy the following into the build.gradle.

```gradle
repositories {
    maven {
        name = "codemc-repo"
        url = "https://repo.codemc.io/repository/maven-public/"
    }
}

dependencies {
    compileOnly 'me.zombie_striker:QualityArmory:VERSION'
}
```
{% endtab %}

{% tab title="Gradle KTS" %}
To add QA to your project using gradle, copy the following into the build.gradle.kts.

```gradle
repositories {
    maven {
        name = "codemc-repo"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("me.zombie_striker:QualityArmory:VERSION")
}
```
{% endtab %}
{% endtabs %}

### Plugin Dependency

Add the plugin as a dependency to your plugin by editing the `plugin.yml` file by including the following line under the `depend` section:

```yaml
# Use this if QualityArmory is required
depend: [QualityArmory]
# Use this if QualityArmory is optional
softdepend: [QualityArmory]
```

This ensures that your plugin will load after QualityArmory. If your plugin requires specific features from QualityArmory at runtime, remember to also use appropriate checks in your code to confirm that QualityArmory is enabled.
