import kotlin.collections.contains

/**
 * Class representing a Command Line Interface (CLI).
 *
 * @property name The name of the CLI.
 * @property version The version of the CLI (default is "v1.0.0").
 * @property help The help text for the CLI.
 */
class CLIBeact(val name: String, val version: String = "v1.0.0", val help: String = "") {
    private val commands = mutableMapOf<String, Command>()
    private val options = mutableListOf<Option>()

    /**
     * Main entry point of the CLI.
     *
     * @param args The arguments passed to the CLI.
     */
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            help()
        } else {
            val lsArgs = args.asList()
            val cmdName = lsArgs[0]
            val cmd = commands[cmdName]
            if (cmd == null) {
                println("Command not found")
            } else {
                execute(cmd, lsArgs)
            }
        }
    }

    /**
     * Executes a command with the provided arguments.
     *
     * @param cmd The command to execute.
     * @param args The arguments passed to the command.
     */
    private fun execute(cmd: Command, args: List<String>) {
        val argsWithoutOption = args.toMutableList()
        val mapsOptions = mutableMapOf<String, Boolean>()
        args.filter { it.startsWith("-") || it.startsWith("--") }.forEachIndexed { i, arg ->
            val option = options.find { it.cmdName.contains(arg) }
            if (option != null) {
                mapsOptions.put(option.name, true)
                option.runCmd(true)
                argsWithoutOption.removeAt(i)
            } else {
                println("Unknown option: $arg")
            }
        }
        options.filter{ !mapsOptions.contains(it.name) }.forEach {
            mapsOptions.put(it.name, false)
            if (!it.onlyCallWhenIsTrue) {
                it.runCmd(false)
            }
        }


        if (cmd.arguments.isEmpty()) {
            cmd.runCmd(argsWithoutOption, mapsOptions)
        } else {
            val options = argsWithoutOption.subList(1, args.size)
            if (options.size < cmd.getNbRequired()) {
                println("Missing required options")
            } else {
                cmd.runCmd(options, mapsOptions)
            }
        }

    }

    /**
     * Adds a command to the CLI.
     *
     * @param name The name of the command.
     * @param help The help text of the command.
     * @param arguments The list of arguments for the command.
     * @param run The function to execute for the command.
     * @return The instance of CLIBeact.
     */
    fun command(
        name: String,
        help: String = "",
        arguments: List<Argument> = listOf(),
        run: (Map<String, String>, Map<String, Boolean>) -> Unit
    ): CLIBeact {
        commands.put(name, Command(name, help, arguments, run))
        return this
    }

    /**
     * Adds an option to the CLI.
     *
     * @param name The name of the option.
     * @param shortName The short name of the option.
     * @param help The help text for the option.
     * @param onlyCallWhenIsTrue Indicates if the option should be called only when it is true.
     * @param run The function to execute for the option.
     * @return The instance of CLIBeact.
     */
    fun option(name: String, shortName: String, help: String = "", onlyCallWhenIsTrue: Boolean = false, run: (Boolean) -> Unit = {}): CLIBeact {
        val option = Option(name, shortName, help, onlyCallWhenIsTrue, run)
        options.add(option)
        return this
    }

    /**
     * Displays the help text of the CLI.
     */
    private fun help() {
        println("Welcome in $name $version")
        if (help != "") {
            println(help)
        }
        println("\nUsage: $name [<options>] <command> [<args>]")
        if (options.isNotEmpty()) {
            println("\nOptions:")
            options.forEach {
                println("  ${it.name[1]}, ${it.name[0]} - ${it.help}")
            }
        }
        if (commands.isNotEmpty()) {
            println("\nCommands:")
            commands.forEach {
                println("  ${it.key} - ${it.value.help}")
                //TODO: Add arguments
                println("    Usage: $name "+ it.value.getUsageString())
                /*it.value.arguments.forEach {
                    if (it.required) {
                        println("    ${it.name} - ${it.help} (required)")
                    } else {
                        println("    ${it.name} - ${it.help}")
                    }
                }*/
            }
        }
    }
}


/**
 * Class representing a command of the CLI.
 *
 * @property name The name of the command.
 * @property help The help text for the command.
 * @property arguments The list of arguments for the command.
 * @property run The function to execute for the command.
 */
data class Command(
    val name: String,
    val help: String,
    val arguments: List<Argument>,
    val run: (Map<String, String>, Map<String, Boolean>) -> Unit
) {
    /**
     * Executes the command with the provided arguments and options.
     *
     * @param args The arguments passed to the command.
     * @param options The options passed to the command.
     */
    fun runCmd(args: List<String> = listOf(), options: Map<String, Boolean>) {
        var maps = mutableMapOf<String, String>()

        for (i in 0 until arguments.size) {
            val argument = arguments[i]
            if (args.size > i) {
                maps.put(argument.name, args[i])
            } else {
                if (argument.required) {
                    println("Missing required argument: <${argument.name}>")
                    return
                }
                maps.put(argument.name, argument.default)
            }
        }
        run(maps, options)
    }

    /**
     * Returns the number of required arguments for the command.
     *
     * @return The number of required arguments.
     */
    fun getNbRequired(): Int {
        return arguments.filter { it.required }.size
    }

    /**
     * Returns the usage string of the command.
     *
     * @return The usage string.
     */
    fun getUsageString(): String {
        return "$name "+arguments.joinToString(" ") { if (it.required)"<"+it.name+">" else "["+it.name+"]" }
    }
}

/**
 * Class representing a command argument.
 *
 * @property name The name of the argument.
 * @property help The help text for the argument.
 * @property required Indicates if the argument is required.
 * @property default The default value of the argument.
 */
data class Argument(val name: String, val help: String = "", val required: Boolean = false, val default: String = "")

/**
 * Class representing an option of the CLI.
 *
 * @property name The name of the option.
 * @property shortName The short name of the option.
 * @property help The help text for the option.
 * @property onlyCallWhenIsTrue Indicates if the option should be called only when it is true.
 * @property run The function to execute for the option.
 */
data class Option(var name: String, val shortName: String = name.first().toString(), val help: String = "", val onlyCallWhenIsTrue: Boolean = false, val run: (Boolean) -> Unit) {
    val cmdName = listOf("--$name", "-$shortName")

    /**
     * Executes the function associated with the option.
     *
     * @param v The boolean value passed to the function.
     */
    fun runCmd(v: Boolean) {
        run(v)
    }
}

