
plugins {
    application
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization").version("1.9.21")
}

group = "dev.rlqd.alinotify"
version = "1.0"

repositories {
    mavenCentral()
}

val ktor_version: String by project
val logback_version: String by project

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass.set("${group}.MainKt")
    distributions {
        main {
            contents {
                from(layout.projectDirectory.file("app.properties.example"))
            }
        }
    }
}
kotlin {
    jvmToolchain(17)
}
