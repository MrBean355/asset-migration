package com.android.assets

import java.io.File

object AssetMigrator {

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

    private val valid = ('a'..'z').toSet() + ('0'..'9').toSet() + '_'

    private fun makeChanges(dryRun: Boolean, output: Output, file: File, mapping: Map<String, String>) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                makeChanges(dryRun, output, it, mapping)
            }
        } else {
            var fileContent = file.readText()
            val matches = mapping.filterKeys { fileContent.contains(it) }
            if (matches.isEmpty()) {
                // No replacements to be made.
                return
            }
            output += "\tProcessing file: ${file.path}..."
            matches.forEach { (old, new) ->
                output += "\t\tReplace $old -> $new"
                fileContent = maybeMakeReplacement(fileContent, old, new)
            }
            if (!dryRun) {
                file.writeText(fileContent)
            }
        }
    }

    private fun maybeMakeReplacement(content: String, old: String, new: String): String {
        val builder = StringBuilder()
        val parts = content.split(Regex.fromLiteral(old))
        parts.forEachIndexed { index, s ->
            if (index < parts.size - 1) {
                if (parts[index + 1].first().isValid()) {
                    // FIXME
                    println("Panic")
//                    builder.append(s).append(old).append(parts[index + 1])
                } else {
                    builder.append(s).append(new)
                }
            }
        }

        return builder.toString()
    }

    private fun Char.isValid(): Boolean {
        return valid.contains(this)
    }
}