package com.android.assets

import java.io.File

/** A method of outputting logs. */
interface Output {
    fun append(text: String)
}

class ConsoleOutput : Output {

    override fun append(text: String) {
        println(text)
    }
}

class FileOutput(fileName: String) : Output {
    private val file = File(fileName).apply {
        createNewFile()
    }

    override fun append(text: String) {
        file.appendText(text)
    }
}