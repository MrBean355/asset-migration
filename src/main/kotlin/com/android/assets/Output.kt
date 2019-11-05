package com.android.assets

import java.io.File

/** A method of outputting logs. */
interface Output {
    operator fun plusAssign(text: String)
}

class ConsoleOutput : Output {

    override operator fun plusAssign(text: String) {
        println(text)
    }
}

class FileOutput(fileName: String) : Output {
    private val file = File(fileName)

    init {
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
    }

    override operator fun plusAssign(text: String) {
        file.appendText(text)
    }
}