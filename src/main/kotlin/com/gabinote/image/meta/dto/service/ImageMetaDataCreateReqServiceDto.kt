package com.gabinote.image.meta.dto.service


data class ImageMetaDataCreateReqServiceDto(
    /**
     * 원본 파일 이름 (확장자 포함)
     */
    val originName: String,
    /**
     * 변환된 파일 이름 (확장자 포함)
     */
    val convertedName: String,
    val format: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val storagePath: String,
    val uploadBy:String,
)