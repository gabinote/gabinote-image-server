package com.gabinote.image.common.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gabinote.img")
data class ImgProperties(
    /**
     * 허용되는 이미지 포맷 목록
     *
     * 비교시 해당 값을 사용하지 말고 [allowedFormatSet] 을 사용해야 함
     * @see allowedFormatSet
     */
    val allowedFormats: MutableSet<String> = mutableSetOf(),
    val maxWidth: Int,
    val maxHeight: Int,
    val maxFileSize: Long,
    val maxFileNameSize: Long
){
    /**
     * 허용되는 이미지 포맷의 대문자 집합
     *
     */
    val allowedFormatSet: Set<String>
        get() = allowedFormats.map { it.uppercase() }.toSet()
}