package com.gabinote.image.meta.service

import com.gabinote.image.common.util.exception.service.ResourceNotFound
import com.gabinote.image.meta.domain.ImageMetaData
import com.gabinote.image.meta.domain.ImageMetaDataRepository
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import com.gabinote.image.meta.mapping.ImageMetaDataMapper
import com.gabinote.image.testSupport.testTemplate.ServiceTestTemplate
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class ImageMetaDataServiceTest : ServiceTestTemplate() {

    private lateinit var imageMetaDataService: ImageMetaDataService

    @MockK
    private lateinit var imageMetaDataRepository: ImageMetaDataRepository

    @MockK
    private lateinit var imageMetaDataMapper: ImageMetaDataMapper

    init {
        beforeTest {
            clearAllMocks()
            imageMetaDataService = ImageMetaDataService(
                imageMetaDataRepository,
                imageMetaDataMapper
            )
        }

        describe("[ImageMetaData] ImageMetaDataService Test") {

            describe("ImageMetaDataService.createImageMetaData") {
                context("올바른 정보가 주어지면,") {
                    val createReqDto = ImageMetaDataCreateReqServiceDto(
                        originName = "test_image.png",
                        convertedName = "a1b2c3d4-e5f6-4789-0123-4567890abcde.png",
                        format = "png",
                        size = 102400L,
                        width = 800,
                        height = 600,
                        storagePath = "/images/2024/07/",
                        uploadBy = "test-user"
                    )

                    val imageMetaDataEntity = mockk<ImageMetaData>()
                    val savedEntity = mockk<ImageMetaData>()

                    beforeTest {
                        every {
                            imageMetaDataMapper.toEntity(createReqDto)
                        } returns imageMetaDataEntity

                        every {
                            imageMetaDataRepository.save(imageMetaDataEntity)
                        } returns savedEntity
                    }

                    it("이미지 메타데이터를 생성한다.") {
                        imageMetaDataService.createImageMetaData(createReqDto)

                        verify(exactly = 1) {
                            imageMetaDataMapper.toEntity(createReqDto)
                        }

                        verify(exactly = 1) {
                            imageMetaDataRepository.save(imageMetaDataEntity)
                        }
                    }
                }
            }

            describe("ImageMetaDataService.fetchByConvertedName") {
                context("존재하는 convertedName이 주어지면,") {
                    val existingConvertedName = UUID.fromString("a1b2c3d4-e5f6-4789-0123-4567890abcde")
                    val existingImageMetaData = ImageMetaData(
                        originName = "test_image.png",
                        convertedName = existingConvertedName.toString(),
                        format = "png",
                        size = 102400L,
                        width = 800,
                        height = 600,
                        storagePath = "/images/2024/07/",
                        uploadBy = "test-user",
                        uploadDate = LocalDateTime.now()
                    )

                    beforeTest {
                        every {
                            imageMetaDataRepository.findByConvertedName(existingConvertedName.toString())
                        } returns existingImageMetaData
                    }

                    it("해당 convertedName에 맞는 ImageMetaData를 반환한다.") {
                        val result = imageMetaDataService.fetchByConvertedName(existingConvertedName)

                        result.convertedName shouldBe existingConvertedName.toString()

                        verify(exactly = 1) {
                            imageMetaDataRepository.findByConvertedName(existingConvertedName.toString())
                        }
                    }
                }

                context("존재하지 않는 convertedName이 주어지면,") {
                    val nonExistingConvertedName = UUID.randomUUID()

                    beforeTest {
                        every {
                            imageMetaDataRepository.findByConvertedName(nonExistingConvertedName.toString())
                        } returns null
                    }

                    it("ResourceNotFound 예외를 던진다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            imageMetaDataService.fetchByConvertedName(nonExistingConvertedName)
                        }

                        ex.name shouldBe "ImageMetaData"
                        ex.identifier shouldBe nonExistingConvertedName.toString()
                        ex.identifierType shouldBe "convertedName"

                        verify(exactly = 1) {
                            imageMetaDataRepository.findByConvertedName(nonExistingConvertedName.toString())
                        }
                    }
                }
            }
        }
    }
}