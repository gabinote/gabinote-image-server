package com.gabinote.image.common.util.uuid

import java.util.*

object UuidUtil {
    fun String.toUuid(): UUID {
        return UUID.fromString(this)
    }
}