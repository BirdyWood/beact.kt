import generator.Generator
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UShortVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.toKString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.windows.GetModuleFileNameW
import platform.windows.GetModuleHandleW
import platform.windows.MAX_PATH
import platform.windows.PathRemoveFileSpecW
import versionM.VersionM

@OptIn(ExperimentalForeignApi::class)
val ExecutablePathDir: Path by lazy {
    // Get the path to the EXE.
    val hmodule = GetModuleHandleW(null)
    val wstr = nativeHeap.allocArray<UShortVar>(MAX_PATH)
    GetModuleFileNameW(hmodule, wstr, MAX_PATH.toUInt())
    // Strip the filename leaving just the directory.
    PathRemoveFileSpecW(wstr)
    wstr.toKString().toPath()
}


val CLI = CLIBeact("beact", VersionM.getVersion().toString(), "Beact CLI")
    .command(
        name = "build",
        help = "Build the proejct",
        run = { args, opts ->
            println("Building the project: $args $opts")

            val workDir = FileSystem.SYSTEM.canonicalize(".".toPath().resolve(args["target"]?: "")).toString()
            println("Working Directory = $workDir")
            /*val currentRelativePath = Paths.get(workDir, target)
            val s = currentRelativePath.toAbsolutePath().toString()
            println("Building the dir: $s")*/
            Generator().generate(".".toPath().resolve(args["target"]?: "").toString())
        },
        arguments = listOf(
            Argument(
                name = "target",
                help = "Target option",
                required = false,
            )
        )
    )
    .command(
        name = "clean",
        help = "Clean the project",
        arguments = listOf(
            Argument(
                name = "target",
                help = "Target option",
                required = false,
            )
        ),
        run = { args, opts ->
            println("Testing the project: $args $opts")
        }
    )
    .option(
        name = "target",
        shortName = "t",
        help = "Target option"
    ){
        println("Target option: $it")
    }


fun main(args: Array<String>) {
    CLI.main(args)

    /*println(args.joinToString(", "))
    println(PrettyPrintJson.encodeToString(message))*/
    /*
    if (args[0] == "test" && args[1] == "test"){
        println("Test")
    }*/
}
