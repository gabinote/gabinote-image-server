package com.gabinote.image.common.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gabinote.img")
data class ImgProperties(
    val allowedFormats: MutableSet<String> = mutableSetOf(),
    val maxWidth: Int,
    val maxHeight: Int,
    val maxFileSize: Long,
    val maxFileNameSize: Long
)