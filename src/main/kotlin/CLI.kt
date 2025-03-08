package org.bw.beact

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.bw.beact.generator.Generator
import java.nio.file.Paths

public fun main(args: Array<String>) {
    println()
    Commands().subcommands(Build(), Preview(), Rebuild(), Clean()).main(args)
}

class MyApp : CliktCommand() {
    val name by option().required()
    val greeting by option().default("Hello")
    val command by argument().default("build")

    override fun run() {
        println("$greeting, $name!")
        println("command: $command")
    }
}

class Commands : CliktCommand(name = "beact") {
    init {
        context {
            // Only suggest corrections that start with the entered value
            suggestTypoCorrection = { enteredValue, possibleValues ->
                possibleValues.filter { it.startsWith(enteredValue) }
            }
        }
    }
    override fun run() = Unit
    override fun help(context: Context) = """
    Welcome in Beact ${VersionUtil.applicationVersion}
    """.trimIndent()
}

class Build : CliktCommand() {

    val target by argument().default("")

    override fun help(context: Context) = "Build the project"
    override fun run() {
        val workDir = System.getProperty("user.dir")
        println("Working Directory = $workDir");
        val currentRelativePath = Paths.get(workDir, target)
        val s = currentRelativePath.toAbsolutePath().toString()
        println("Building the dir: $s")
        Generator().generate(s)
    }
}

class Preview : CliktCommand() {
    val target by argument().default("")

    override fun help(context: Context) = "Preview the project"
    override fun run() {
        val workDir = System.getProperty("user.dir")
        println("Working Directory = $workDir");
        val currentRelativePath = Paths.get(workDir, target)
        val s = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")
        println("Building the dir: $s")
        println("Building the $target")
    }
}

class Rebuild : CliktCommand() {
    val target by argument().default("")

    override fun help(context: Context) = "Rebuild the project"
    override fun run() {
        TODO("""The code below doesn't work as expected: 
            ```kotlin
            Clean().run()
            Build().run()
            ```
        """)
    }
}

class Clean : CliktCommand() {
    val target by argument().default("world")

    override fun help(context: Context) = "Clean the project"
    override fun run() {
        println("Cleaning the $target")
    }
}