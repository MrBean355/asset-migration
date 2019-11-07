package com.android.assets

import java.io.File
import java.io.FileFilter

class AssetMigrator(
        private val dryRun: Boolean,
        private val output: Output,
        mapping: AssetMapping) {

    private val nameMappings = mapping.getAllMappings()
    private val invalidAssets = mapping.getInvalidAssets()

    /**
     * Replace usages of old assets with new ones.
     */
    fun run(directories: Collection<String>) {
        output += "Using mappings: $nameMappings"
        output += "Invalid assets: $invalidAssets"
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
            makeChanges(srcDirectories.single())
        }
    }

    private fun makeChanges(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                makeChanges(it)
            }
        } else {
            var fileContent = file.readText()
            val applicableReplacements = nameMappings.filterKeys {
                fileContent.containsAssetName(it)
            }
            if (applicableReplacements.isEmpty()) {
                // No replacements to be made.
                return
            }
            output += "\tProcessing file: ${file.name}..."
            val invalidAssetUsages = invalidAssets.filter { fileContent.containsAssetName(it) }
            if (invalidAssetUsages.isNotEmpty()) {
                output += "\t\tInvalid assets used: $invalidAssetUsages"
            }
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