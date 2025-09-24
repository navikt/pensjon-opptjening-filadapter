import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val navTokenSupportVersion = "5.0.25"
val logbackEncoderVersion = "8.1"
val jacksonVersion = "2.18.2"
val azureAdClient = "0.0.7"
val assertjVersion = "3.27.3"
val wiremockVersion = "3.13.0"
val micrometerRegistryPrometheusVersion = "1.14.3"
val mockitoKotlinVersion = "5.4.0"
val jsonUnitVersion = "4.1.0"
val guavaVersion = "33.4.8-jre"
val jschVersion = "0.2.26"
val hibernateValidatorVersion = "8.0.1.Final"

val apacheSshdVersion = "2.15.0"

plugins {
    val kotlinVersion = "2.1.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.5.3"
    id("com.github.ben-manes.versions") version "0.52.0"
}

apply(plugin = "io.spring.dependency-management")

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework:spring-aspects")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("ch.qos.logback:logback-access:1.5.18")

    // Test - setup
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.wiremock:wiremock-jetty12:$wiremockVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
}

tasks.test {
    maxParallelForks = 1
//    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
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
