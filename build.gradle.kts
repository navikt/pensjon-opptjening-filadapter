import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

val navTokenSupportVersion = "7.0.2"
val logbackEncoderVersion = "9.0"
val logbackAccessVersion = "1.6.8"
val jacksonVersion = "2.23.0"
val azureAdClient = "0.0.9"
val assertjVersion = "3.27.6"
val wiremockVersion = "4.0.0-beta.44"
val micrometerRegistryPrometheusVersion = "1.18.0"
val mockitoKotlinVersion = "6.4.0"
val jsonUnitVersion = "6.4.1"
val guavaVersion = "33.8.0-jre"
val jschVersion = "2.28.8"
val hibernateValidatorVersion = "9.2.0.Final"

val apacheSshdVersion = "2.20.1"
val okHttpVersion = "5.5.1"

val kotlinTestVersion = "2.4.20"

plugins {
    val kotlinVersion = "2.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "4.2.0"
    id("io.github.ben-manes.versions") version "0.67.0"
}

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/maven-release") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // Native Gradle BOM-import (erstatter io.spring.dependency-management-pluginet). Constraints fra
    // spring-boot-dependencies gjelder transitivt til testImplementation (arver fra implementation).
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework:spring-aspects")

    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryPrometheusVersion")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.pensjonopptjening:pensjon-opptjening-azure-ad-client:$azureAdClient")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("org.apache.sshd:sshd-core:$apacheSshdVersion")
    implementation("org.apache.sshd:sshd-sftp:$apacheSshdVersion")
    implementation("com.github.mwiede:jsch:$jschVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("ch.qos.logback:logback-access:$logbackAccessVersion")

    testImplementation("com.squareup.okhttp3:mockwebserver:${okHttpVersion}")  // trengs for nav-token-support
// Test - setup
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.wiremock:wiremock:$wiremockVersion") // aggregator: httpclient-apache5 factory + core
    testImplementation("org.wiremock:wiremock-junit5:$wiremockVersion") // WireMockExtension
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")

    // Usikker på hvorfor denne er nødvendig, men den følger bare nesten kotlin-versjonen, og i siste bygg ble også kotlin-versjonen
    // overstyrt fra 2.4.20 til 2.4.21 (uskker på fra hvor, muligens spring-boot)
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTestVersion")
}

tasks.test {
    maxParallelForks = 1
//    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED,
        )
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA", "RC", "-M", "-rc-", "Alpha").any { version.uppercase().contains(it) }
}
