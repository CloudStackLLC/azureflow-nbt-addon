import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.21"
}

group = "io.rokuko.azureflow"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "spigotmc-repo"
        setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    compileOnly(fileTree("libs") {
        include("*.jar")
    })

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.compileJava.configure {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = project.providers.provider {
            JvmTarget.JVM_1_8
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<JavaCompile> {
//    options.compilerArgs = listOf("-source", "1.8", "-target", "1.8")
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.jar.configure {
    destinationDirectory.set(File("E:\\Minecraft\\Aurorium\\server\\10003-Dungeon_1-paper-1.16.5-794-副本\\plugins"))
}
