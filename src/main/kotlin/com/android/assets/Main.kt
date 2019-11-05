package com.android.assets

fun main(args: Array<String>) {
    val argList = args.toMutableList()
    var dryRun = false
    var outputFile = ""
    val directories = mutableSetOf<String>()

    while (argList.isNotEmpty()) {
        when (val arg = argList.removeAt(0)) {
            "--dry-run" -> dryRun = true
            "--output" -> outputFile = argList.removeAt(0)
            "--include" -> directories += argList.removeAt(0)
            else -> {
                println("Unexpected argument: $arg")
                printUsage()
                return
            }
        }
    }

    if (directories.isEmpty()) {
        println("Error: no target directories specified. Use the --include argument to specify one or more.")
        printUsage()
        return
    }

    println("""
        ===== Asset Migration =====
        Dry run: $dryRun
        Output:  ${if (outputFile.isNotBlank()) "file -> $outputFile" else "console"}
        ${directories.joinToString(prefix = "Include: ", separator = "\n        Include: ")}
        
    """.trimIndent())

    print("Type 'go' to continue: ")
    if (readLine() != "go") {
        println("Bye")
        return
    }

    AssetMigrator.run(
            dryRun = dryRun,
            output = if (outputFile.isNotBlank()) FileOutput(outputFile) else ConsoleOutput(),
            directories = directories,
            mapping = TestAssetMapping()
    )
}

private fun printUsage() {
    println("""
        Available arguments:
        --include [directory] : Include a directory in the migration. Can provide multiple times. Must provide at least one.
        --dry-run             : Perform a dry run; don't actually modify any files.
        --output [file]       : Log to a file. Optional; logs to console if omitted.
    """.trimIndent())
}