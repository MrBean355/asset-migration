package com.android.assets

import java.io.File

/**
 * Pattern for valid replacements.
 *
 * Don't replace usages when that usage contains the desired asset name as a substring. For example, when replacing
 * `icn_person`, we don't want to also replace `icn_person_2` or `blue_icn_person` (because they are different assets).
 */
private const val ASSET_PATTERN = "([^a-z0-9_])%s([^a-z0-9_])"

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
        directories.forEach { dir ->
            output += "\nProcessing directory: $dir..."
            val dirFile = File(dir)
            if (!dirFile.exists() || !dirFile.isDirectory) {
                output += "Error: target doesn't exist or is not a directory."
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
            var fileContent = file.readText()
            val matches = mapping.filterKeys {
                fileContent.contains(Regex(ASSET_PATTERN.format(it)))
            }
            if (matches.isEmpty()) {
                // No replacements to be made.
                return
            }
            output += "\tProcessing file: ${file.path}..."
            matches.forEach { (old, new) ->
                output += "\t\tReplacing: $old -> $new"
                fileContent = fileContent.replace(Regex(ASSET_PATTERN.format(old)), "$1$new$2")
            }
            if (!dryRun) {
                file.writeText(fileContent)
            }
        }
    }
}