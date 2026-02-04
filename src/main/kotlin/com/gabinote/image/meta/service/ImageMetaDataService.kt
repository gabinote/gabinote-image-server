package com.gabinote.image.meta.service

import com.gabinote.image.common.util.exception.service.ResourceNotFound
import com.gabinote.image.meta.domain.ImageMetaData
import com.gabinote.image.meta.domain.ImageMetaDataRepository
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import com.gabinote.image.meta.mapping.ImageMetaDataMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ImageMetaDataService(
    private val imageMetaDataRepository: ImageMetaDataRepository,
    private val imageMetaDataMapper: ImageMetaDataMapper,
) {

    fun createImageMetaData(
        dto: ImageMetaDataCreateReqServiceDto
    ) {
        val imageMetaData = imageMetaDataMapper.toEntity(dto)
        imageMetaDataRepository.save(imageMetaData)
    }

    fun fetchByConvertedName(
        convertedName: UUID
    ) : ImageMetaData {
        return imageMetaDataRepository.findByConvertedName(convertedName.toString())?: throw ResourceNotFound(
            name = "ImageMetaData",
            identifier = convertedName.toString(),
            identifierType = "convertedName"
        )
    }

}