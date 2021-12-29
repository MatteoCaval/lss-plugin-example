plugins {
    // No magic: calls a method running behind the scenes the same of id("org.jetbrains.kotlin-$jvm")
    kotlin("jvm") version "1.5.31" // version is necessary
}

// Configuration of software sources
repositories {
    mavenCentral() // points to Maven Central
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    // "implementation" is a configuration created by by the Kotlin plugin
    implementation(kotlin("stdlib-jdk8")) // "kotlin" is an extension method of DependencyHandler
    // The call to "kotlin" passing `module`, returns a String "org.jetbrains.kotlin:kotlin-$module:<KotlinVersion>"

    testImplementation(gradleTestKit()) // Test implementation: available for testing compile and runtime
    val kotestVersion = "5.0.2"
    fun kotest(module: String) = "io.kotest:kotest-$module:$kotestVersion"
    testImplementation(kotest("runner-junit5")) // for kotest framework
    testImplementation(kotest("assertions-core")) // for kotest core assertions
    testImplementation(kotest("assertions-core-jvm")) // for kotest core jvm assertions
}

tasks.withType<Test> { // The task type is defined in the Java plugin
    useJUnitPlatform() // Use JUnit 5 engine
    testLogging {
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}