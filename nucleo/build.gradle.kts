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
    api(libs.flogger.system.backend)
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
    implementation(libs.smallrye.config)

    /*********************************************************************************
     * Test
     */

    // Lombok
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
