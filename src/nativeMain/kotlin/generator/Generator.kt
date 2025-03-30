package generator

import errorSys.ErrorSys
import converter.BMLConverter
import okio.FileSystem
import okio.Path.Companion.toPath


class Generator {

    /*fun test(){
        val directoryPath = Paths.get(System.getProperty("user.dir"), "\\src\\main\\resources\\test_bml").toString()
        var result = generate(directoryPath)
        println(result)
    }*/


    /* Config ErrorSys */
    var printOnAdding = true
    var breakOnError = true

    val error = ErrorSys("BML_Generator", printOnAdding, breakOnError)

    /* Config Main Class */
    val converter = BMLConverter()
    val outputDir= "output/html"

    fun generate(srcDir: String){
        val listBMLFiles = findAllFiles(srcDir, arrayOf("target", "output", "out", ".idea", ".git"))


        listBMLFiles.forEach { file ->
            val inputString = FileSystem.SYSTEM.read(file.toPath()) {
                readUtf8()
            }

            converter.importFileFun = { path ->
                println("Importing file: ${(file.toPath().parent?: "".toPath()).resolve(path)}")
                FileSystem.SYSTEM.read((file.toPath().parent?: "".toPath()).resolve(path)) {
                    readUtf8()
                }
            }
            val result = converter.convert(inputString)
            //println(result)

            if (result.hasError) {
                result.error.forEach { error.addError(it) }
            }
            if (result.needToExport) {
                val filename = file.toPath().name

                val pathToFileInOut = srcDir.toPath().resolve(outputDir).resolve(filename.replace(".bml", ".html")).toString()
                checkOutputDir(srcDir)

                writeToFile(pathToFileInOut, result.htmlCode)

                val filepath = file.toPath().parent.toString()
                //println(filepath)
                result.otherSourceToImport.forEach { path ->
                    println("Copying file: $path")
                    FileSystem.SYSTEM.copy(filepath.toPath().resolve(path),srcDir.toPath().resolve(outputDir).resolve(path))
                }
            }
        }
        //Paths.get()
    }

    fun checkOutputDir(srcDir: String){
        val arrOutDir = outputDir.split("/")
        for (i in 0 until arrOutDir.size){
            val path = srcDir.toPath().resolve(arrOutDir.subList(0, i+1).joinToString("/"))
            if (FileSystem.SYSTEM.exists(path)){
                FileSystem.SYSTEM.createDirectory(path)
            }
        }
    }

    fun writeToFile(path: String, content: String){
        println("Writing to file: $path")
        // Check if the directory exists
        path.toPath().parent?.let {
            if (!FileSystem.SYSTEM.exists(it)) {
                // Create the directory if it doesn't exist
                FileSystem.SYSTEM.createDirectories(it)
            }
        }

        FileSystem.SYSTEM.write(path.toPath()) {
            writeUtf8(content)
        }
    }

    fun getPathFromDir(pathDir: String, pathFile: String): String{
        return pathDir.toPath().resolve(pathFile).toString()
    }

    fun findAllFiles(srcDir: String, excludeDIr: Array<String>): List<String>{
        val directory = srcDir.toPath()

        val listBMLFiles = mutableListOf<String>()

        val files = FileSystem.SYSTEM.listRecursively(directory)


        files.forEach { file ->
            //File
            val md = FileSystem.SYSTEM.metadata(file)
            if (md.isRegularFile) {
                val extension = file.name.split(".").last()
                if (extension == "bml") {
                    println("✅ ✔" + file.name)
                    listBMLFiles.add(file.toString())
                }
            }else if (md.isDirectory && !excludeDIr.contains(file.name)){
                listBMLFiles += findAllFiles(file.toString(), excludeDIr)
            }

            // Dir
        }
        if (listBMLFiles.isEmpty()) println("No files found or directory does not exist.")

        return listBMLFiles
    }
}