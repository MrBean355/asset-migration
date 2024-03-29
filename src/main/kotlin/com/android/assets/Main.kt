package com.android.assets

import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val argList = args.toMutableList()
    var dryRun = false
    var deleteMode = false
    var mapping = ""
    val directories = mutableSetOf<String>()

    while (argList.isNotEmpty()) {
        when (val arg = argList.removeAt(0)) {
            "--dry-run" -> dryRun = true
            "--delete" -> deleteMode = true
            "--mapping" -> mapping = argList.removeAt(0)
            "--include" -> directories += argList.removeAt(0)
            else -> {
                println("Unexpected argument: $arg")
                printUsage()
                return
            }
        }
    }

    if (mapping.isBlank()) {
        println("Error: no mapping file specified. Use the --mapping argument to specify one.")
        printUsage()
        return
    }
    if (directories.isEmpty()) {
        println("Error: no target directories specified. Use the --include argument to specify one or more.")
        printUsage()
        return
    }

    println("""
        ===== Asset Migration =====
        Dry run     : $dryRun
        Delete mode : $deleteMode
        Mapping     : $mapping
        ${directories.joinToString(prefix = "Include     : ", separator = "\n        Include     : ")}
        
    """.trimIndent())

    // Construct here to warn about assets without replacements before starting.
    println("Parsing input mapping file...")
    val assetMapping = CsvFileAssetMapping(mapping)
    println("Done parsing. Loaded ${assetMapping.mappings.size} mappings & ${assetMapping.deprecatedAssets.size} \"deprecated\" assets.")

    print("\nType 'go' to continue: ")
    if (readLine() != "go") {
        println("Bye")
        return
    }

    var totalFiles = 0
    val duration = measureTimeMillis {
        totalFiles = AssetMigrator(dryRun = dryRun, deleteMode = deleteMode, mapping = assetMapping)
                .run(directories)
    }
    println("Done! Processed a total of $totalFiles files in $duration ms.")
}

private fun printUsage() {
    println("""
        Available arguments (order doesn't matter):
        --mapping [file]      : CSV file to read mappings from. Required.
        --include [directory] : Include a directory in the migration. Can provide multiple times. Must provide at least one.
        --delete              : Delete old assets instead of replacing usages. 
        --dry-run             : Perform a dry run; don't actually modify any files.
    """.trimIndent())
}