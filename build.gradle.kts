plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    // kotlin("plugin.jpa") removed because we move to MongoDB
}

group = "com.programacionMovil"
version = "0.0.1-SNAPSHOT"
description = "Backend Bus API"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Data MongoDB (replace JPA + Postgres)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Kotlin + JSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Hot reload (opcional)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// removed allOpen block because JPA annotations are not used with MongoDB
