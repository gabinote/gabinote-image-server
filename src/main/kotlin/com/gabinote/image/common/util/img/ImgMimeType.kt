package com.gabinote.image.common.util.img

@JvmInline
value class ImgMimeType(val value: String) {
    fun mimeType(): String = value.lowercase()



    /**
     * 이미지 확장자를 대문자로 반환하여 리턴
     *
     * 이때 jpg는 JPEG로 변환하여 리턴
     */
    fun ext():String {
        val mineType = mimeType()
        return mineType.substringAfterLast('/').uppercase()
    }


}