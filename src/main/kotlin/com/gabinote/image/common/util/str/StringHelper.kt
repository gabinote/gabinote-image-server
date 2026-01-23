package com.gabinote.image.common.util.str

object StringHelper {
    fun String.isSimpleFileNameFormat(): Boolean {
        val lastDotIndex = this.lastIndexOf('.')

        return lastDotIndex > 0 && lastDotIndex < this.length - 1
    }
}