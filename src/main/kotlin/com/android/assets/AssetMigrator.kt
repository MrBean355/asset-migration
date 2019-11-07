package com.android.assets

import java.io.File
import java.io.FileFilter

object AssetMigrator {

    /**
     * Replace usages of old assets with new ones.
     *
     * @param dryRun if `true`, don't actually make changes.
     * @param output output mechanism for logging.
     * @param directories directories to run on.
     * @param mapping mapping of old names to new names.
     */
    fun run(dryRun: Boolean, output: Output, directories: Collection<String>, mapping: AssetMapping) {
        val nameMappings = mapping.getAllMappings()
        output += "Using mappings: $nameMappings"
        directories.forEach { dir ->
            output += "\nProcessing directory: $dir..."
            val dirFile = File(dir)
            if (!dirFile.exists() || !dirFile.isDirectory) {
                output += "Error: target doesn't exist or is not a directory."
                return
            }
            val srcDirectories = dirFile.listFiles(FileFilter { it.name == "src" }).orEmpty()
            if (srcDirectories.isEmpty()) {
                output += "Error: please run on the root directory of a module."
                return
            }
            makeChanges(dryRun, output, srcDirectories.single(), nameMappings)
        }
    }

    private fun makeChanges(dryRun: Boolean, output: Output, file: File, mapping: Map<String, String>) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                makeChanges(dryRun, output, it, mapping)
            }
        } else {
            var fileContent = file.readText()
            val applicableReplacements = mapping.filterKeys {
                fileContent.containsAssetName(it)
            }
            if (applicableReplacements.isEmpty()) {
                // No replacements to be made.
                return
            }
            output += "\tProcessing file: ${file.name}..."
            applicableReplacements.forEach { (old, new) ->
                output += "\t\tReplacing: $old -> $new"
                fileContent = fileContent.replaceAssetName(old, new)
            }
            if (!dryRun) {
                file.writeText(fileContent)
            }
        }
    }
}