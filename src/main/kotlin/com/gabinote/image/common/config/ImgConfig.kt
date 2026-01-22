package com.gabinote.image.common.config

import com.gabinote.image.common.config.properties.ImgProperties
import com.gabinote.image.common.config.properties.ImgProxyProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ImgProperties::class)
class ImgConfig {

}