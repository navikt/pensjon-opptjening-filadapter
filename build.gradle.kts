import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

val navTokenSupportVersion = "6.0.8"
val logbackEncoderVersion = "9.0"
val logbackAccessVersion = "1.5.34"
val jacksonVersion = "2.22.0"
val azureAdClient = "0.0.7"
val assertjVersion = "3.27.6"
val wiremockVersion = "4.0.0-beta.38"
val micrometerRegistryPrometheusVersion = "1.17.0"
val mockitoKotlinVersion = "6.1.0"
val jsonUnitVersion = "5.0.0"
val guavaVersion = "33.6.0-jre"
val jschVersion = "2.28.3"
val hibernateValidatorVersion = "9.1.0.Final"

val apacheSshdVersion = "2.18.0"
val okHttpVersion = "5.4.0"

plugins {
    val kotlinVersion = "2.4.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "4.1.0"
    id("com.github.ben-manes.versions") version "0.54.0"
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
