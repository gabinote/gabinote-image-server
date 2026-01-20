package com.gabinote.image.meta.mapping

import com.gabinote.image.meta.domain.ImageMetaData
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import com.gabinote.image.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe

class ImageMetaDataMapperTest : MockkTestTemplate() {
    private val imageMetaDataMapper = ImageMetaDataMapperImpl()
    
    init {
        describe("[ImageMetaData] ImageMetaDataMapperTest") {
            describe("ImageMetaDataMapperTest.toEntity") {
                context("ImageMetaDataCreateReqServiceDto 가 주어지면") {
                   val dto = ImageMetaDataCreateReqServiceDto(
                       originName = "sample_image.jpg",
                       convertedName = "123e4567-e89b-12d3-a456-426614174000.jpg",
                       format = "jpg",
                       size = 204800L,
                       width = 1920,
                       height = 1080,
                       storagePath = "/images/2024/06/01/123e4567-e89b-12d3-a456-426614174000.jpg",
                       uploadBy = "user_1234"
                   )

                    val expected = ImageMetaData(
                        id = null,
                        uploadDate = null,
                        originName = "sample_image.jpg",
                        convertedName = "123e4567-e89b-12d3-a456-426614174000.jpg",
                        format = "jpg",
                        size = 204800L,
                        width = 1920,
                        height = 1080,
                        storagePath = "/images/2024/06/01/123e4567-e89b-12d3-a456-426614174000.jpg",
                        uploadBy = "user_1234"
                    )

                    it("ImageMetaData 엔티티로 올바르게 매핑한다") {
                        val result = imageMetaDataMapper.toEntity(dto)

                        result shouldBe expected
                    }
                }
            }
        }
    }

}