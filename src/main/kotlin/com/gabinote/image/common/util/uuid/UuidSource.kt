package com.gabinote.image.common.util.uuid

import java.util.*


interface UuidSource {
    fun generateUuid(): UUID
}