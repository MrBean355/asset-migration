package com.android.assets

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileFilter
import java.util.Date
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class AssetMigrator(private val dryRun: Boolean, private val deleteMode: Boolean, private val mapping: AssetMapping) {

    /**
     * Replace usages of old assets with new ones.
     *
     * @return total number of files processed.
     */
    fun run(directories: Collection<String>): Int {
        val result = AtomicInteger()
        runBlocking(context = IO) {
            directories.forEach {
                launch { result.addAndGet(runSingle(it)) }
            }
        }
        return result.get()
    }

    private suspend fun runSingle(directory: String): Int {
        println("Processing directory: $directory...")
        val dirFile = File(directory)
        val logs = ConcurrentLinkedQueue<String>()
        logs += "${Date()}\nProcessing ${dirFile.absolutePath}..."
        if (!dirFile.exists() || !dirFile.isDirectory) {
            logs += "Error: target doesn't exist or is not a directory."
            return 0
        }
        val allFiles = if (deleteMode) {
            dirFile.getAllFiles()
        } else {
            val srcDirectories = dirFile.listFiles(FileFilter { it.name == "src" }).orEmpty()
            if (srcDirectories.isEmpty()) {
                logs += "Error: please run on the root directory of a module (i.e. one that contains the 'src' directory)."
                return 0
            }
            srcDirectories.single().getAllFiles()
        }
        logs += "${allFiles.size} total files to process."
        coroutineScope {
            if (deleteMode) {
                allFiles.filter { it.nameWithoutExtension in mapping.mappings.keys }.forEach {
                    logs += "Deleting old asset: ${it.name}"
                    if (!dryRun) {
                        it.delete()
                    }
                }
            } else {
                allFiles.forEach {
                    launch { logs += makeChanges(it) }
                }
            }
        }
        getLogFile(dirFile.name).apply {
            appendLine(logs.joinToString(separator = "\n"))
            appendLine("\nDone!")
        }
        return allFiles.size
    }

    private fun getLogFile(directory: String): File {
        val logFile = File("${directory.substringAfterLast(File.separatorChar)}-log.txt")
        if (logFile.exists()) {
            logFile.delete()
        }
        logFile.createNewFile()
        return logFile
    }

    private fun File.appendLine(text: String) {
        appendText("$text\n")
    }

    private fun File.getAllFiles(): List<File> {
        val files = mutableListOf<File>()
        listFiles()?.forEach {
            if (it.isDirectory) {
                files += it.getAllFiles()
            } else {
                files += it
            }
        }
        return files
    }

    private fun makeChanges(file: File): List<String> {
        val logs = mutableListOf<String>()
        var fileContent = file.readText()
        val applicableReplacements = mapping.mappings.filterKeys {
            fileContent.containsAssetName(it)
        }
        if (applicableReplacements.isEmpty()) {
            // No replacements to be made.
            return emptyList()
        }
        logs += "\nProcessing file: ${file.name}..."
        val deprecatedAssetUsages = mapping.deprecatedAssets.filter { fileContent.containsAssetName(it) }
        if (deprecatedAssetUsages.isNotEmpty()) {
            logs += "\tDeprecated assets used: $deprecatedAssetUsages"
        }
        applicableReplacements.forEach { (old, new) ->
            logs += "\tReplacing: $old -> $new"
            fileContent = fileContent.replaceAssetName(old, new)
        }
        if (!dryRun) {
            file.writeText(fileContent)
        }
        return logs
    }
}