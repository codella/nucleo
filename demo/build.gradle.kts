plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Nucleo
    implementation(project(":nucleo"))
    // Logback
    implementation(libs.logback)
    // Vertx Dependencies
    implementation(libs.vertx.mysql)
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Testcontainers
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.mysql)
//    // Vertx Dependencies
    testImplementation(libs.vertx.mysql)
    testImplementation(libs.mysql.connector.j)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "dk.codella.demo.Demo"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
