package com.gabinote.image.meta.dto.service


data class ImageMetaDataCreateReqServiceDto(

    val originName: String,
    val convertedName: String,
    val format: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val storagePath: String,
    val uploadBy:String,
)