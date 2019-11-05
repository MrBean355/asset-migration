package com.android.assets

fun main(args: Array<String>) {
    val argList = args.toMutableList()
    var dryRun = false
    var output = ""
    val directories = mutableSetOf<String>()

    while (argList.isNotEmpty()) {
        when (argList.removeAt(0)) {
            "--dry-run" -> dryRun = true
            "--output" -> output = argList.removeAt(0)
            "--include" -> directories += argList.removeAt(0)
        }
    }

    if (directories.isEmpty()) {
        println("Error: no target directories specified. Use the --include argument to specify one or more.")
        return
    }

    println("""
        ===== Asset Migration =====
        Dry run: $dryRun
        Output:  ${if (output.isNotBlank()) "file -> $output" else "console"}
        ${directories.joinToString(prefix = "Include: ", separator = "\n        Include: ")}
        
    """.trimIndent())

//    print("Type 'go' to continue: ")
//    if (readLine() != "go") {
//        println("Bye")
//        return
//    }

    val outputter = if (output.isBlank()) ConsoleOutput() else FileOutput(output)
    AssetMigrator.run(dryRun, outputter, directories, TestAssetMapping())
}
