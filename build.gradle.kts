import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val navTokenSupportVersion = "5.0.16"
val logbackEncoderVersion = "8.0"
val postgresqlVersion = "42.7.5"
val testcontainersVersion = "1.20.4"
val jacksonVersion = "2.18.2"
val azureAdClient = "0.0.7"
val assertjVersion = "3.27.3"
val awaitilityVersion = "4.2.2"
val wiremockVersion = "3.10.0"
val micrometerRegistryPrometheusVersion = "1.14.3"
val mockitoKotlinVersion = "5.4.0"
val unleashVersion = "9.2.6"
val jsonUnitVersion = "4.1.0"
val guavaVersion = "33.4.0-jre"
val jschVersion = "0.2.23"
// val httpClient5Version = "5.3.1" // TODO: 5.4 feiler med NoClassDefFoundError
val httpClient5Version = "5.4.1"
val hibernateValidatorVersion = "8.0.1.Final"

val snappyJavaVersion = "1.1.10.7"
val snakeYamlVersion = "2.3"
val apacheSshdVersion = "2.14.0"

plugins {
    val kotlinVersion = "2.1.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.4.2"
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
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryPrometheusVersion")
    implementation("org.springframework:spring-aspects")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")

    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("no.nav.pensjonopptjening:pensjon-opptjening-azure-ad-client:$azureAdClient")
    implementation("io.getunleash:unleash-client-java:$unleashVersion")
    implementation("com.google.guava:guava:$guavaVersion")

    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClient5Version")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

    implementation("org.apache.sshd:sshd-core:$apacheSshdVersion")
    implementation("org.apache.sshd:sshd-sftp:$apacheSshdVersion")

    // These are transitive dependencies, but overriding them on top level due to vulnerabilities
    // (and in some cases, the wrong version being picked)
    implementation("org.xerial.snappy:snappy-java:$snappyJavaVersion")
    implementation("org.yaml:snakeyaml:$snakeYamlVersion")

    implementation("com.github.mwiede:jsch:$jschVersion")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    // Test - setup
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("org.wiremock:wiremock-jetty12:$wiremockVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
}

tasks.test {
    maxParallelForks = 1
    useJUnitPlatform()
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
