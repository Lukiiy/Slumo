plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.6.1"
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("26.1.1.build.+")
    compileOnly(files("lib/paper.jar")) // engine
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks {
    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
