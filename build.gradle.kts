import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
}

group = "dev.reimer"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}


tasks {
    // Compile Kotlin to JVM1.8 bytecode.
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    // Include project license in generated JARs.
    withType<Jar> {
        from(project.projectDir) {
            include("LICENSE")
            into("META-INF")
        }
    }

    // Generate Kotlin/Java documentation from sources.
    dokka {
        outputFormat = "html"
    }
}

// JAR containing Kotlin/Java documentation.
val javadoc = tasks.create<Jar>("javadocJar") {
    dependsOn(tasks.dokka)
    from(tasks.dokka.get().outputDirectory)
}

// JAR containing all source files.
val sources = tasks.create<Jar>("sourcesJar") {
    dependsOn("classes")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifact(sources) {
                classifier = "sources"
            }

            artifact(javadoc) {
                classifier = "javadoc"
            }
        }
    }
}