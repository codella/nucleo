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
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
