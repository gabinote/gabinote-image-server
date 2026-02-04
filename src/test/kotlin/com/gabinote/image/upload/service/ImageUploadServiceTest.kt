package com.gabinote.image.upload.service

import com.gabinote.image.common.config.properties.ImgProperties
import com.gabinote.image.common.util.exception.service.ResourceNotValid
import com.gabinote.image.common.util.uuid.UuidSource
import com.gabinote.image.meta.domain.ImageMetaData
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import com.gabinote.image.meta.service.ImageMetaDataService
import com.gabinote.image.storage.service.ImageStorageService
import com.gabinote.image.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.image.testSupport.testUtil.data.img.TestImageHelper
import com.gabinote.image.testSupport.testUtil.uuid.TestUuidSource
import com.sun.tools.javac.file.JavacFileManager.testName
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.verify
import org.apache.tika.Tika
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

class ImageUploadServiceTest : ServiceTestTemplate() {

    private lateinit var imageUploadService: ImageUploadService

    @MockK
    private lateinit var imageMetaDataService: ImageMetaDataService

    @MockK
    private lateinit var imageStorageService: ImageStorageService

    @MockK
    private lateinit var uuidSource: UuidSource

    @MockK
    private lateinit var imgProperties: ImgProperties

    @MockK
    private lateinit var tika: Tika

    private val defaultMaxFileSize = 10_000_000L
    private val defaultMaxWidth = 10000
    private val defaultMaxHeight = 10000
    private val defaultAllowedFormats = mutableSetOf("PNG", "JPG", "JPEG", "GIF", "WEBP")
    private val defaultMaxFileNameSize = 100_000_000L

    init {
        beforeTest {
            clearAllMocks()
            imageUploadService = ImageUploadService(
                imageMetaDataService,
                imageStorageService,
                uuidSource,
                imgProperties,
                tika
            )

            every { imgProperties.maxFileSize } returns defaultMaxFileSize
            every { imgProperties.maxWidth } returns defaultMaxWidth
            every { imgProperties.maxHeight } returns defaultMaxHeight
            every { imgProperties.allowedFormats } returns defaultAllowedFormats
            every { imgProperties.maxFileNameSize } returns defaultMaxFileNameSize
            every { imgProperties.allowedFormatSet } returns defaultAllowedFormats.map { it.uppercase() }.toSet()
        }

        describe("[ImageUpload] ImageUploadService Test") {

            describe("ImageUploadService.uploadImage") {
                describe("성공 케이스") {
                    context("올바른 이미지 파일과 업로더 정보가 주어지면,") {
                        // ~ 메타 정보 저장 전
                        val testUploader = "test-uploader"
                        val validImage = TestImageHelper.PNG_1X1
                        val fileName = "validimg"
                        val mockFile = validImage.toMockMultipartFile(fileName)

                        val convertName = TestUuidSource.UUID_STRING
                        beforeTest {
                            every { uuidSource.generateUuid() } returns convertName
                            every { tika.detect(any<ByteArray>()) } returns "image/${validImage.format.lowercase()}"
                        }


                        //saveMetaData
                        val testSavePath = "/images/$convertName.png"

                        val expectedMeta = ImageMetaDataCreateReqServiceDto(
                            originName = "$fileName.${validImage.format}",
                            convertedName = "$convertName.png",
                            format = validImage.format.lowercase(),
                            size = mockFile.size,
                            width = 1,
                            height = 1,
                            uploadBy = testUploader,
                            storagePath = testSavePath,
                        )

                        beforeTest {

                            every { imageStorageService.getSavePath("$convertName.png")} returns testSavePath

                            every { imageMetaDataService.createImageMetaData(expectedMeta) } returns Unit
                        }

                        // 이미지 저장
                        beforeTest {
                            every {
                                imageStorageService.saveImageToStorage(
                                    convertedName = "$convertName.png",
                                    imageStream = any()
                                )
                            } returns Unit
                        }

                        it("이미지를 저장소에 업로드하고 메타데이터를 생성한 후 변환된 이름을 반환한다.") {
                            val result = imageUploadService.uploadImage(mockFile, testUploader)

                            result shouldBe "$convertName.png"

                            verify(exactly = 1) { uuidSource.generateUuid() }
                            verify(exactly = 1) { imageStorageService.getSavePath("$convertName.png") }
                            verify(exactly = 1) { imageMetaDataService.createImageMetaData(expectedMeta) }
                            verify(exactly = 1) {
                                imageStorageService.saveImageToStorage(
                                    convertedName = "$convertName.png",
                                    imageStream = any()
                                )
                            }
                        }
                    }
                }
                describe("실패 케이스 - 파일명 관련") {
                    context("원본 파일 이름이 없는 경우,") {
                        val testUploader = "test-uploader"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(null)

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Original filename is missing")

                        }
                    }

                    context("파일 확장자가 없는 경우,") {
                        val testUploader = "test-uploader"
                        val invalidImage = TestImageHelper.PNG_1X1
                        val testName = "test_image"
                        val mockFile = invalidImage.toMockMultipartFileNoExt(testName)


                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Original filename format is invalid: $testName")
                        }
                    }

                    context("유효하지 않은 이미지 데이터인 경우,") {
                        val testUploader = "test-uploader"
                        val invalidImage = TestImageHelper.INVALID_IMG
                        val testName = "test_image"
                        val mockFile = invalidImage.toMockMultipartFile(testName)

                        beforeTest{
                            every { tika.detect(any<ByteArray>()) } returns "image/${invalidImage.format.lowercase()}"
                        }


                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Unable to read image data from file: $testName.${invalidImage.format}")

                        }
                    }
                }

                describe("실패 케이스 - Validation 관련") {
                    context("파일 크기가 최대 제한을 초과하는 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "large_image"
                        val tooLargeImg = TestImageHelper.PNG_1X1

                        val mockFile = tooLargeImg.toMockMultipartFile(testOriginalFilename)
                        val maxFileSize = 10L

                        beforeTest {
                            // 파일 크기 제한을 매우 작게 설정
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { imgProperties.maxFileSize } returns maxFileSize
                            every { tika.detect(any<ByteArray>()) } returns "image/${tooLargeImg.format.lowercase()}"
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("File size exceeds the maximum limit of $maxFileSize bytes")
                        }
                    }

                    context("이미지 해상도(너비)가 최대 제한을 초과하는 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "wide_image"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile =  invalidImage.toMockMultipartFile(testOriginalFilename)
                        val maxWidth = invalidImage.width - 1 // 테스트용으로 1 작게 설정하여 항상 초과하도록 함


                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { imgProperties.maxWidth } returns maxWidth.toInt()
                            every { tika.detect(any<ByteArray>()) } returns "image/${invalidImage.format.lowercase()}"
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Image resolution exceeds the maximum limit of ${maxWidth}x${defaultMaxHeight} pixels")

                        }
                    }

                    context("이미지 해상도(높이)가 최대 제한을 초과하는 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "tall_image"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(testOriginalFilename)
                        val maxHeight = invalidImage.height - 1 // 테스트용으로 1 작게 설정하여 항상 초과하도록 함

                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { tika.detect(any<ByteArray>()) } returns "image/${invalidImage.format.lowercase()}"
                            every { imgProperties.maxHeight } returns maxHeight.toInt()
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Image resolution exceeds the maximum limit of ${defaultMaxWidth}x${maxHeight} pixels")
                        }
                    }

                    context("지원하지 않는 이미지 포맷인 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "image"
                        val invalidImage = TestImageHelper.JPG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(testOriginalFilename)
                        val allowedFormats = mutableSetOf("PNG")

                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { tika.detect(any<ByteArray>()) } returns "image/${invalidImage.format.lowercase()}"
                            every { imgProperties.allowedFormatSet } returns allowedFormats
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Unsupported image format: JPEG. Supported formats are: ${allowedFormats.joinToString(", ")}")

                        }
                    }

                    context("너무 긴 원본 파일 이름인 경우,") {
                        val testUploader = "test-uploader"
                        val tooLong = "tooooooooooooolongfilename"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(tooLong)
                        val maxFileName = tooLong.length - 1
                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { tika.detect(any<ByteArray>()) } returns "image/${invalidImage.format.lowercase()}"
                            every { imgProperties.maxFileNameSize } returns maxFileName.toLong()
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Original filename exceeds the maximum length of $maxFileName characters")
                        }
                    }

                    context("MIME 타입이 image/ 로 시작하지 않는 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "image"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(testOriginalFilename)

                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { tika.detect(any<ByteArray>()) } returns "application/${invalidImage.format.lowercase()}"
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("Invalid MIME type: application/${invalidImage.format.lowercase()}. Expected an image MIME type.")
                        }
                    }

                    context("파일 확장자와 MIME 타입이 매칭되지 않는 경우,") {
                        val testUploader = "test-uploader"
                        val testOriginalFilename = "image"
                        val invalidImage = TestImageHelper.PNG_1X1

                        val mockFile = invalidImage.toMockMultipartFile(testOriginalFilename)

                        beforeTest {
                            every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                            every { tika.detect(any<ByteArray>()) } returns "image/jpeg"
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imageUploadService.uploadImage(mockFile, testUploader)
                            }

                            ex.name shouldBe "Image"
                            ex.reasons shouldBe listOf("File extension PNG does not match MIME type image/jpeg.")
                        }
                    }
                }
            }
        }
    }
}

