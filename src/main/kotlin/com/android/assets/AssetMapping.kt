package com.android.assets

import java.io.File

/** A method of receiving asset mappings. */
interface AssetMapping {
    fun getAllMappings(): Map<String, String>
}

private const val CSV_COLUMN_SEPARATOR = ','

class CsvFileAssetMapping(fileName: String) : AssetMapping {
    private val mappings = mutableMapOf<String, String>()

    init {
        val file = File(fileName)
        require(file.exists() && file.isFile) { "Not an existing file: ${file.absolutePath}" }
        file.readLines().drop(1).forEach { line ->
            val cols = line.split(CSV_COLUMN_SEPARATOR)
            val oldName = cols.first()
            require(oldName.isValidAssetName()) { "Invalid asset name: $oldName" }
            if (cols.size > 1) {
                val newName = cols[1]
                require(newName.isValidAssetName()) { "Invalid asset name: $newName" }
                val key = oldName.substringBeforeLast('.')
                require(!mappings.containsKey(key)) { "$key has multiple replacements" }
                mappings += key to newName.substringBeforeLast('.')
            } else {
                println("Warning: no replacement for $oldName.")
            }
        }

        mappings.keys.filter { it == mappings[it] }.forEach {
            println("Warning: tried to replace $it with itself! Ignoring this asset.")
            mappings.remove(it)
        }

        mappings.keys.filter { it in mappings.values }.forEach {
            println("Warning: $it is an old & new asset! This makes it risky to run the program multiple times on the same directory.")
        }
    }

    override fun getAllMappings() = mappings
}