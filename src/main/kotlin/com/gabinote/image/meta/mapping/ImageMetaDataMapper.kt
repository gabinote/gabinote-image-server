package com.gabinote.image.meta.mapping

import com.gabinote.image.meta.domain.ImageMetaData
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ImageMetaDataMapper {
    fun toEntity(dto: ImageMetaDataCreateReqServiceDto): ImageMetaData
}