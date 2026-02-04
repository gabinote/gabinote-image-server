package com.gabinote.image.common.util.extensions

object ExtensionHelper {
    // Boolean을 0 또는 1로 변환하는 확장 함수
    // true는 1, false는 0으로 변환
    fun Boolean.toZeroOrOne(): Int = if (this) 1 else 0
}