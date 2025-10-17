plugins {
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("com.github.johnrengelman.processes") version "0.5.0"
}

group = "com.programacionMovil"
version = "0.0.1-SNAPSHOT"
description = "Backend Bus API"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Kotlin + JSON
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

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

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}
