package com.gabinote.image.imgProxy.dto.service

data class ImgProxyUrlReqServiceDto(
    val fileName: String,
    val requestFormat: String,
    val width: Int,
    val height: Int,
    val enlarge: Boolean
) {
}