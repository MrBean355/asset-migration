package com.android.assets

import java.io.File

object AssetMigrator {

    fun run(dryRun: Boolean, output: Output, directories: Collection<String>, mapping: AssetMapping) {
        directories.forEach { dir ->
            output.append("\nProcessing $dir...")
            val dirFile = File(dir)
            if (!dirFile.exists() || !dirFile.isDirectory) {
                output.append("Error: target doesn't exist or is not a directory.")
            } else {
                makeChanges(dryRun, output, dirFile, mapping.getAllMappings())
            }
        }
    }

    private fun makeChanges(dryRun: Boolean, output: Output, file: File, mapping: Map<String, String>) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                makeChanges(dryRun, output, it, mapping)
            }
        } else {
            val originalContent = file.readText()
            val matches = mapping.filterKeys { originalContent.contains(it) }
            var newContent = originalContent + "" // copy
            matches.forEach { (old, new) ->
                output.append("Replace $old -> $new")
                newContent = newContent.replace(old, new)
            }
        }
    }
}