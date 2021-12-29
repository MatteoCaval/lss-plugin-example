import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class TestGreetingPlugin : FreeSpec({
    val manifest = Thread.currentThread().contextClassLoader
        .getResource("plugin-classpath.txt")
        ?.readText()
    require(manifest != null) {
        "Could not load manifest"
    }
    val tempDir = tempdir()
    tempDir.mkdirs()
    val buildGradleKts = with(File(tempDir, "build.gradle.kts")) {
        writeText(
            """
                    plugins {
                         id("it.unibo.lss.greetings")
                    }
                    greetings {
                         greetWith {
                              "Ciao"
                         }
                    }
                    """.trimIndent()
        )
    }
    //create a gradle runner
    val result = GradleRunner.create()
        .withPluginClasspath(manifest.lines().map { File(it) })
        .withProjectDir(tempDir)
        .withArguments("greet")
        .build()
    println(result.output)
    result.tasks.forEach { it.outcome shouldBe TaskOutcome.SUCCESS }
    result.tasks.size shouldBe 1
    result.output shouldContain "Ciao"
})