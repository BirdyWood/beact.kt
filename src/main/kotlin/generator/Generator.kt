package org.bw.beact.generator

import org.bw.beact.converter.BMLConverter
import org.bw.beact.errorSys.ErrorSys
import org.junit.Test
import java.io.File
import java.io.FileFilter
import java.io.InputStream
import java.nio.file.Paths

class Generator {

    @Test
    fun test(){
        val directoryPath = Paths.get(System.getProperty("user.dir"), "\\src\\main\\resources\\test_bml").toString()
        var result = generate(directoryPath)
        println(result)
    }


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
            val inputStream: InputStream = File(file).inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }

            converter.importFileFun = { path ->
                File(getPathFromDir(File(file).parent, path)).inputStream().bufferedReader().use { it.readText() }
            }
            val result = converter.convert(inputString)
            //println(result)

            if (result.hasError) {
                result.error.forEach { error.addError(it) }
            }
            if (result.needToExport) {
                val filename = Paths.get(file).fileName.toString()

                val pathToFileInOut = Paths.get(srcDir,outputDir, filename.replace(".bml", ".html")).toString()
                checkOutputDir(srcDir)

                writeToFile(pathToFileInOut, result.htmlCode)

                val filepath = Paths.get(file).parent.toString()
                //println(filepath)
                result.otherSourceToImport.forEach { path ->
                    File(Paths.get(filepath, path).toString()).copyTo(File(Paths.get(srcDir, outputDir, path).toString()), true)
                }
            }
        }
        //Paths.get()
    }

    fun checkOutputDir(srcDir: String){
        val arrOutDir = outputDir.split("/")
        for (i in 0 until arrOutDir.size){
            val path = Paths.get(srcDir, arrOutDir.subList(0, i+1).joinToString("/")).toString()
            val directory = File(path)
            if (!directory.exists()){
                directory.mkdir()
            }
        }
    }

    fun writeToFile(path: String, content: String){
        File(path).writeText(content)
    }

    fun getPathFromDir(pathDir: String, pathFile: String): String{
        return Paths.get(pathDir, pathFile).toString()
    }

    fun findAllFiles(srcDir: String, excludeDIr: Array<String>): List<String>{
        val directory = File(srcDir)

        val listBMLFiles = mutableListOf<String>()

        val files = directory.listFiles(FileFilter{ it.isFile })

        files?.forEach { file ->
            val extension = file.extension
            if (extension == "bml"){
                println("✅ ✔" + file.name)
                listBMLFiles.add(file.path)
            }else {
                //println(file.name)
            }
        } ?: println("No files found or directory does not exist.")

        (directory.listFiles()?.filter { it.isDirectory && !excludeDIr.contains(it.nameWithoutExtension)})?.forEach { listBMLFiles += findAllFiles(it.path, excludeDIr) }

        return listBMLFiles
    }
}