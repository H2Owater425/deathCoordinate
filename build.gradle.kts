plugins {
    kotlin("jvm") version "1.8.21"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "vg.h2o"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.github.monun:kommand-api:3.1.3")

    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        jvmArgs("-javaagent:../asm/build/libs/asm-0.1.0.jar")
    }

    runMojangMappedServer {
        jvmArgs("-javaagent:../asm/build/libs/asm-0.1.0.jar")
    }
}