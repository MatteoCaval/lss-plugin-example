import io.kotest.core.spec.style.FreeSpec
import org.gradle.testkit.runner.GradleRunner

class TestGreetingPlugin : FreeSpec({

     GradleRunner.create().build()
})