plugins {
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Weld
    api(libs.weld)

    // Vert.x
    api(libs.vertx)

    // RESTEasy
//    implementation("org.jboss.resteasy:resteasy-vertx:6.2.11.Final")
//    implementation(platform("org.jboss.resteasy.microprofile:resteasy-microprofile-bom:3.0.1.Final"))
//    implementation("org.jboss.resteasy.microprofile:")

    /*
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
