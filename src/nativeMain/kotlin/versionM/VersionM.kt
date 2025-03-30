package versionM

import ExecutablePathDir
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class VersionM {
    companion object {
        fun getVersion(): String? {
            val content = FileSystem.SYSTEM.read(getPathOfVersion()) {
                readUtf8()
            }
            val lines = content.split("\n")
            for (line in lines) {
                if (line.startsWith("version=")) {
                    val version = line.substringAfter("version=")
                    //println("Version: $version")
                    return version
                }
            }
            return null
        }

        fun getPathOfVersion(): Path {
            val nameFile = "version.properties"
            return if (FileSystem.SYSTEM.exists(ExecutablePathDir.resolve(nameFile))) {
                ExecutablePathDir.resolve(nameFile)
            } else {
                FileSystem.SYSTEM.canonicalize("./$nameFile".toPath())
            }
        }
    }
}