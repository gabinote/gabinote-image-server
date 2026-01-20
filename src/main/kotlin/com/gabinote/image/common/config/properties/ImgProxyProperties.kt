package com.gabinote.image.common.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "imgproxy")
data class ImgProxyProperties(
    val baseUrl: String,
    val key: String,
    val salt: String
)