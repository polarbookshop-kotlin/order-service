import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.2.9"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
	implementation("org.springframework.cloud:spring-cloud-function-context")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:r2dbc")
	testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.flywaydb:flyway-core:10.16.0")
	implementation("org.flywaydb:flyway-database-postgresql:10.16.0")
	runtimeOnly("org.springframework:spring-jdbc")
	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
	implementation("org.springframework.boot:spring-boot-configuration-processor:3.3.2")
	// https://mvnrepository.com/artifact/com.squareup.okhttp3/mockwebserver
	testImplementation("com.squareup.okhttp3:mockwebserver")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.named<BootBuildImage>("bootBuildImage"){
	builder = "docker.io/paketobuildpacks/builder-jammy-base"
	imageName = project.name
	environment = mapOf("BP_JVM_VERSION" to "17")
	docker {
		publishRegistry {
			username = project.findProperty("registryUsername") as String?
			password= project.findProperty("registryToken") as String?
			url=project.findProperty("registryUrl") as String?
		}
	}
}