package com.android.assets

import java.io.File

/** A method of receiving asset mappings. */
interface AssetMapping {
    fun getAllMappings(): Map<String, String>
}

class TestAssetMapping : AssetMapping {

    override fun getAllMappings(): Map<String, String> {
        return mapOf(
                "app_icon" to "app_logo_primary",
                "icn_alert" to "icn_alert_grey",
                "icn_payment" to "icn_payment_white"
        )
    }
}

private const val CSV_COLUMN_SEPARATOR = ';'

class CsvFileAssetMapping(fileName: String) : AssetMapping {
    private val mappings = mutableMapOf<String, String>()

    init {
        val file = File(fileName)
        require(file.exists() && file.isFile) { "Not an existing file: ${file.absolutePath}" }
        file.readLines().drop(1).forEach { line ->
            val cols = line.split(CSV_COLUMN_SEPARATOR)
            require(cols.size <= 2) { "Invalid CSV format: $line" }
            val oldName = cols.first()
            require(oldName.isValidAssetName()) { "Invalid asset name: $oldName" }
            if (cols.size == 2) {
                val newName = cols[1]
                require(newName.isValidAssetName()) { "Invalid asset name: $newName" }
                mappings += oldName to newName
            } else {
                println("Warning: no replacement for $oldName.")
            }
        }
    }

    override fun getAllMappings() = mappings
}