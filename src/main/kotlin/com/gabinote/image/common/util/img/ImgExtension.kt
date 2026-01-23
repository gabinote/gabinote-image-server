package com.gabinote.image.common.util.img

/**
 * 이미지 파일의 확장자를 나타내는 값 클래스
 */
@JvmInline
value class ImgExtension(val value: String) {
    /**
     * 이미지 확장자를 대문자로 반환하여 리턴
     */
    fun ext(): String = value.uppercase()
}