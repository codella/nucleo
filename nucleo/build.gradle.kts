plugins {
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    /*********************************************************************************
     * Main
     */

    // Flogger
    api(libs.flogger)
    implementation(libs.flogger.slf4j.backend)
    // Slf4j
    api(libs.slf4j)
    // Guava
    implementation(libs.guava)
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    // Weld
    api(libs.weld)
    // Vert.x
    api(libs.vertx.core)
    api(libs.vertx.web)
    // RESTEasy
    api(platform(libs.resteasy.bom))
    api(libs.resteasy.vertx)
    // SmallRye Config
    api(libs.smallrye.config)

    // SmallRye Fault Tolerance
    // COMMENTARY:
    // Those dependencies are mentioned in the SmallRye Fault Tolerance docs:
    // - https://smallrye.io/docs/smallrye-fault-tolerance/6.8.0/integration/intro.html
    // TODO: Enable metrics
    implementation(libs.smallrye.fault.tolerance.api)
    implementation(libs.smallrye.fault.tolerance.core)
    implementation(libs.smallrye.fault.tolerance)
    implementation(libs.microprofile.fault.tolerance.api)
    implementation(libs.microprofile.config.api)
    implementation(libs.smallrye.reactive.converter.api)
    implementation(libs.jboss.logging)

    // SmallRye Health
    implementation(libs.smallrye.health)
    // COMMENTARY:
    // Needed to render response payloads, like SmallRyeHealth
    implementation(libs.jackson.databind)

    /*********************************************************************************
     * Test
     */

    // Lombok
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    // Vert.x
    testImplementation(libs.vertx.junit5)
    // AssertJ
    testImplementation(libs.assertj)
    // Logback
    testImplementation(libs.logback)
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
