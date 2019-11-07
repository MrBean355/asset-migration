package com.android.assets

import java.io.File

/** A method of receiving asset mappings. */
interface AssetMapping {

    /** Map of old asset names to new ones. */
    fun getAllMappings(): Map<String, String>

    /** Assets which shouldn't be used by don't have replacements. */
    fun getInvalidAssets(): Set<String>
}

private const val CSV_COLUMN_SEPARATOR = ','

class CsvFileAssetMapping(fileName: String) : AssetMapping {
    private val mappings = mutableMapOf<String, String>()
    private val invalidAssets = mutableSetOf<String>()

    init {
        val file = File(fileName)
        require(file.exists() && file.isFile) { "Not an existing file: ${file.absolutePath}" }
        file.readLines().drop(1).forEach { line ->
            val cols = line.split(CSV_COLUMN_SEPARATOR)
            val oldName = cols.first().substringBeforeLast('.')
            require(oldName.isNotBlank() && oldName.isValidAssetName()) { "Invalid asset name: $oldName" }
            if (cols.size > 1) {
                val newName = cols[1].substringBeforeLast('.')
                if (newName.isBlank()) {
                    invalidAssets += oldName
                } else {
                    require(newName.isValidAssetName()) { "Invalid asset name: $newName" }
                    require(!mappings.containsKey(oldName)) { "$oldName has multiple replacements" }
                    mappings += oldName to newName
                }
            } else {
                invalidAssets += oldName
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

    override fun getInvalidAssets() = invalidAssets
}