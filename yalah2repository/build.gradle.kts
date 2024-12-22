val exposedVersion = "0.56.0"


plugins {
    kotlin("jvm") version "2.0.21"
    id("java-library")
}

group = "nl.joozd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://joozd.nl/nexus/repository/maven-releases/")
    }
}

dependencies {
    testImplementation(kotlin("test"))

    // YALA interfaces
    implementation("nl.joozd.yalainterfaces:yalainterfaces:0.4-DEV")

    implementation("nl.joozd.yalaprerequisites:yalaprerequisites:0.1")

    //H2 database
    implementation("com.h2database:h2:2.3.232")

    // Exposed (database stuff)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // logging api
    implementation("org.slf4j:slf4j-api:2.0.16")

    // for slf4j
    testImplementation("org.slf4j:slf4j-simple:2.0.16")      // simple version with no frills, for testing


}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}