package template

import ExecutablePathDir
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

fun getAllTemplates(): List<String> {
    val files = FileSystem.SYSTEM.list(getPathTemplate())
    val templateFiles = mutableListOf<String>()
    println("template path: ${getPathTemplate()}")
    for (file in files) {
        if (FileSystem.SYSTEM.metadata(file).isRegularFile) {
            val fileName = file.name
            if (fileName.endsWith(".bml")) {
                val content = FileSystem.SYSTEM.read(file) {
                    readUtf8()
                }
                templateFiles.add(content)
            }
        }
    }

    return templateFiles
}

fun getPathTemplate(): Path {
    return if (FileSystem.SYSTEM.exists(ExecutablePathDir.resolve("templates"))){
        ExecutablePathDir.resolve("templates")
    }else{
        FileSystem.SYSTEM.canonicalize("./src/nativeMain/resources/templates".toPath())
    }
}