package it.unibo.lss.firstplugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class GreetingTask : DefaultTask() {

    // instead of exposing a string, we expose a property, everything is lazy
    // properties are provider with an additional set method
    // only inside the task action we do the get of the property
    @Input
    val greeting: Property<String> = project.objects.property<String>(String::class.java) // Lazy property creation

    @Internal // Read-only property calculated from `greeting`
    val message: Provider<String> = greeting.map { "$it Gradle" }

    @TaskAction
    fun printMessage() {
        // "logger" is a property of DefaultTask
        logger.quiet(message.get())
    }
}


//dsl entry point
open class GreetingExtension(private val project: Project) {
    val defaultGreeting: Property<String> = project.objects.property(String::class.java)
        .apply { convention("Hello from") } // Set a conventional value

    // A DSL would go there
    fun greetWith(greeting: () -> String) = defaultGreeting.set(greeting())
}


class GreetingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Create the extension
        val extension = target.extensions.create("greetings", GreetingExtension::class.java, target)
        // Create the task
        target.tasks.register("greet", GreetingTask::class.java).get().run {
            // Set the default greeting to be the one configured in the extension
            greeting.set(extension.defaultGreeting)
            // Configuration per-task can still be changed manually by users
        }
    }
}