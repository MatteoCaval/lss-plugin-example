plugins {
    // No magic: calls a method running behind the scenes the same of id("org.jetbrains.kotlin-$jvm")
    kotlin("jvm") version "1.5.31" // version is necessary
    alias(libs.plugins.gitSemVer)
    `java-gradle-plugin`
    alias(libs.plugins.publishPlugin)
    `maven-publish`
    jacoco
    id("pl.droidsonroids.jacoco.testkit") version "1.0.9"
}

// Configuration of software sources
repositories {
    mavenCentral() // points to Maven Central
}


// This task creates a file with a classpath descriptor, to be used in tests
val createClasspathManifest by tasks.registering { // This delegate uses the variable name as task name
    val outputDir = File(buildDir, name) // We will write in this folder

    inputs.files(sourceSets.main.get().runtimeClasspath) // Our input is a ready runtime classpath
    // Note: due to the line above, this task implicitly requires our plugin to be compiled!
    outputs.dir(outputDir) // we register the output directory as an output of the task
    doLast { // This is the task the action will execute
        outputDir.mkdirs() // Create the directory infrastructure
        // Write a file with one classpath entry per line
        file("$outputDir/plugin-classpath.txt").writeText(
            sourceSets.main.get().runtimeClasspath
                .joinToString("\n")
        )
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    // "implementation" is a configuration created by by the Kotlin plugin
    implementation(kotlin("stdlib-jdk8")) // "kotlin" is an extension method of DependencyHandler
    // The call to "kotlin" passing `module`, returns a String "org.jetbrains.kotlin:kotlin-$module:<KotlinVersion>"

    testImplementation(gradleTestKit()) // Test implementation: available for testing compile and runtime
    val kotestVersion = "4.6.4"
    fun kotest(module: String) = "io.kotest:kotest-$module:$kotestVersion"
    testImplementation(kotest("runner-junit5")) // for kotest framework
    testImplementation(kotest("assertions-core")) // for kotest core assertions
    testImplementation(kotest("assertions-core-jvm")) // for kotest core jvm assertions
    testRuntimeOnly(files(createClasspathManifest))
}


tasks.withType<Test> { // The task type is defined in the Java plugin
    dependsOn(createClasspathManifest) // before execute the test, the task needs to be executed
    useJUnitPlatform() // Use JUnit 5 engine
    testLogging {
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}


group = "io.github.matteocaval"

pluginBundle {
    website = "https://matteocaval.github.io/"
    vcsUrl = "https://github.com/MatteoCaval/lss-plugin-example"
    tags = listOf("lss", "greeting", "example")
}

gradlePlugin {
    plugins {
        create("lss-plugin-example") {
            id = "$group.${project.name}"
            displayName = "Lss Plugin Example"
            description = "Example plugin"
            implementationClass = "it.unibo.lss.firstplugin.GreetingPlugin"
            group = "io.github.matteocaval"
        }
    }
}

gitSemVer {
    buildMetadataSeparator.set("-")
}

tasks.jacocoTestReport {
    reports {
//        xml.isEnabled = true // Useful for processing result automatically
        html.isEnabled = true // Useful for human inspection
    }
}

// Disable JaCoCo on Windows, see: https://issueexplorer.com/issue/koral--/jacoco-gradle-testkit-plugin/9
tasks.jacocoTestCoverageVerification {
    enabled = !org.apache.tools.ant.taskdefs.condition.Os.isFamily(
        org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { // aggressive compile settings
    kotlinOptions {
//        allWarningsAsErrors = true
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
