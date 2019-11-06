package com.android.assets

import java.io.File

/** A method of outputting logs. */
interface Output {
    /** Append some text to the output. */
    operator fun plusAssign(text: String)
}

/** Write output to the console. */
class ConsoleOutput : Output {

    override operator fun plusAssign(text: String) {
        println(text)
    }
}

/** Write output to a file with the name. Overwrites the file if it already exists. */
class FileOutput(fileName: String) : Output {
    private val file = File(fileName)

    init {
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
    }

    override operator fun plusAssign(text: String) {
        file.appendText("$text\n")
    }
}