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

    /*********************************************************************************
     * Test
     */

    // Lombok
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    // Vert.x
    testImplementation(libs.vertx.junit5)
    // AssertJ
    // TODO: check if testCompileOnly works as well
    testImplementation(libs.assertj)
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
