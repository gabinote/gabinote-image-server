package com.gabinote.image.imgProxy.service

import com.gabinote.image.common.config.properties.ImgProperties
import com.gabinote.image.common.config.properties.ImgProxyProperties
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import com.gabinote.image.storage.service.ImageStorageService
import com.gabinote.image.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.image.common.util.exception.service.ResourceNotValid
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.string.shouldStartWith
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class ImgProxyServiceTest : ServiceTestTemplate() {

    private lateinit var imgProxyService: ImgProxyService

    @MockK
    private lateinit var imgProxyProperties: ImgProxyProperties

    @MockK
    private lateinit var imgProperties: ImgProperties

    @MockK
    private lateinit var imgStorageService: ImageStorageService

    @MockK
    private lateinit var pathSignService: PathSignService

    init {
        beforeTest {
            clearAllMocks()
            imgProxyService = ImgProxyService(
                imgProxyProperties,
                imgProperties,
                imgStorageService,
                pathSignService
            )
        }

        describe("[ImgProxy] ImgProxyService Test") {
            describe("ImgProxyService.generateUrl") {
                describe("성공 케이스") {

                    context("올바른 요청 정보가 주어지면,") {
                        val fileName = "test-image.png"
                        val requestFormat = "webp"
                        val width = 100
                        val height = 100
                        val enlarge = false

                        val dto = ImgProxyUrlReqServiceDto(
                            fileName = fileName,
                            requestFormat = requestFormat,
                            width = width,
                            height = height,
                            enlarge = enlarge
                        )

                        val imagePath = "s3://test-bucket/$fileName"
                        val encodedImagePath = "czM6Ly90ZXN0LWJ1Y2tldC90ZXN0LWltYWdlLnBuZw"
                        val expectedProxyPath = "/rs:fit:100:100:0/$encodedImagePath.$requestFormat"
                        val signature = "test-signature-abc123"
                        val baseUrl = "https://imgproxy.example.com"

                        beforeTest {
                            // validation mock
                            every { imgProperties.maxWidth } returns 2000
                            every { imgProperties.maxHeight } returns 2000
                            every { imgProperties.allowedFormats } returns mutableSetOf("WEBP", "PNG", "JPG")

                            every { imgStorageService.getSavePath(fileName) } returns imagePath
                            every { pathSignService.encode(imagePath) } returns encodedImagePath
                            every { pathSignService.sign(expectedProxyPath) } returns signature
                            every { imgProxyProperties.baseUrl } returns baseUrl
                        }

                        it("서명된 이미지 프록시 URL을 생성하여 반환한다.") {
                            val result = imgProxyService.generateUrl(dto)

                            result.shouldNotBeEmpty()
                            result shouldStartWith baseUrl
                            result shouldContain signature
                            result shouldContain encodedImagePath
                            result shouldContain requestFormat

                            result shouldBe "$baseUrl/$signature$expectedProxyPath"
                            verify(exactly = 1) { imgStorageService.getSavePath(fileName) }
                            verify(exactly = 1) { pathSignService.encode(imagePath) }
                            verify(exactly = 1) { pathSignService.sign(expectedProxyPath) }
                        }
                    }
                }

                describe("실패 케이스") {
                    context("width가 최대 사이즈를 초과하면,") {
                        val maxWidth = 2000
                        val maxHeight = 2000
                        val dto = ImgProxyUrlReqServiceDto(
                            fileName = "test.png",
                            requestFormat = "webp",
                            width = 2001,
                            height = 100,
                            enlarge = false
                        )

                        beforeTest {
                            every { imgProperties.maxWidth } returns maxWidth
                            every { imgProperties.maxHeight } returns maxHeight
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imgProxyService.generateUrl(dto)
                            }

                            ex.name shouldBe "height,width"
                        }
                    }

                    context("height가 최대 사이즈를 초과하면,") {
                        val maxWidth = 2000
                        val maxHeight = 2000
                        val dto = ImgProxyUrlReqServiceDto(
                            fileName = "test.png",
                            requestFormat = "webp",
                            width = 100,
                            height = 2001,
                            enlarge = false
                        )

                        beforeTest {
                            every { imgProperties.maxWidth } returns maxWidth
                            every { imgProperties.maxHeight } returns maxHeight
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imgProxyService.generateUrl(dto)
                            }

                            ex.name shouldBe "height,width"
                        }
                    }



                    context("지원하지 않는 이미지 포맷이 주어지면,") {
                        val maxWidth = 2000
                        val maxHeight = 2000
                        val allowedFormats = mutableSetOf("webp", "png", "jpg")
                        val dto = ImgProxyUrlReqServiceDto(
                            fileName = "test.png",
                            requestFormat = "gif",
                            width = 100,
                            height = 100,
                            enlarge = false
                        )

                        beforeTest {
                            every { imgProperties.maxWidth } returns maxWidth
                            every { imgProperties.maxHeight } returns maxHeight
                            every { imgProperties.allowedFormats } returns allowedFormats
                        }

                        it("ResourceNotValid 예외를 던진다.") {
                            val ex = assertThrows<ResourceNotValid> {
                                imgProxyService.generateUrl(dto)
                            }

                            ex.name shouldBe "requestFormat"
                        }
                    }
                }
            }
        }
    }
}

