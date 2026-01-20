package com.gabinote.image.common.util.strategy

interface Strategy<T : Enum<T>> {
    val type: T
}