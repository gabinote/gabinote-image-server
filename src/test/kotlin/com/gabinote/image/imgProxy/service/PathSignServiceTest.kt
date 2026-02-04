package com.gabinote.image.imgProxy.service

import com.gabinote.image.common.config.properties.ImgProxyProperties
import com.gabinote.image.testSupport.testTemplate.ServiceTestTemplate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK

class PathSignServiceTest : ServiceTestTemplate() {

    private lateinit var pathSignService: PathSignService

    @MockK
    private lateinit var imgProxyProperties: ImgProxyProperties

    init {
        beforeTest {
            clearAllMocks()
        }

        describe("[ImgProxy] PathSignService Test") {
            describe("PathSignService.sign") {
                context("유효한 Hex 형식의 key와 salt가 주어지면,") {
                    val hexKey = "736563726574" // "secret" in hex
                    val hexSalt = "73616c74" // "salt" in hex

                    beforeTest {
                        every { imgProxyProperties.key } returns hexKey
                        every { imgProxyProperties.salt } returns hexSalt
                        pathSignService = PathSignService(imgProxyProperties)
                    }

                    it("경로에 대한 서명을 생성하여 반환한다.") {
                        val path = "/rs:fit:100:100:0/encoded_path.png"
                        
                        val result = pathSignService.sign(path)

                        result.shouldNotBeEmpty()
                        // URL-safe Base64는 +, / 문자를 포함하지 않음
                        result.contains("+") shouldBe false
                        result.contains("/") shouldBe false
                        result.contains("=") shouldBe false
                    }

                    it("동일한 경로에 대해 항상 동일한 서명을 생성한다.") {
                        val path = "/rs:fit:200:200:1/test_path.webp"

                        val result1 = pathSignService.sign(path)
                        val result2 = pathSignService.sign(path)

                        result1 shouldBe result2
                    }

                    it("다른 경로에 대해 다른 서명을 생성한다.") {
                        val path1 = "/rs:fit:100:100:0/path1.png"
                        val path2 = "/rs:fit:100:100:0/path2.png"

                        val result1 = pathSignService.sign(path1)
                        val result2 = pathSignService.sign(path2)

                        result1 shouldNotBe result2
                    }
                }

                context("일반 문자열 형식의 key와 salt가 주어지면,") {
                    val stringKey = "mySecretKey"
                    val stringSalt = "mySalt"

                    beforeTest {
                        every { imgProxyProperties.key } returns stringKey
                        every { imgProxyProperties.salt } returns stringSalt
                        pathSignService = PathSignService(imgProxyProperties)
                    }

                    it("경로에 대한 서명을 생성하여 반환한다.") {
                        val path = "/rs:fit:100:100:0/encoded_path.png"

                        val result = pathSignService.sign(path)

                        result.shouldNotBeEmpty()
                    }
                }
            }

            describe("PathSignService.encode") {
                context("유효한 URI가 주어지면,") {
                    val hexKey = "736563726574"
                    val hexSalt = "73616c74"

                    beforeTest {
                        every { imgProxyProperties.key } returns hexKey
                        every { imgProxyProperties.salt } returns hexSalt
                        pathSignService = PathSignService(imgProxyProperties)
                    }

                    it("URL-safe Base64로 인코딩된 문자열을 반환한다.") {
                        val uri = "s3://test-bucket/test-image.png"

                        val result = pathSignService.encode(uri)

                        result.shouldNotBeEmpty()
                        // URL-safe Base64는 패딩(=)이 없고, +와 /를 포함하지 않음
                        result.contains("+") shouldBe false
                        result.contains("/") shouldBe false
                        result.contains("=") shouldBe false
                    }

                    it("동일한 URI에 대해 항상 동일한 인코딩 결과를 반환한다.") {
                        val uri = "s3://bucket/image.webp"

                        val result1 = pathSignService.encode(uri)
                        val result2 = pathSignService.encode(uri)

                        result1 shouldBe result2
                    }

                    it("다른 URI에 대해 다른 인코딩 결과를 반환한다.") {
                        val uri1 = "s3://bucket/image1.png"
                        val uri2 = "s3://bucket/image2.png"

                        val result1 = pathSignService.encode(uri1)
                        val result2 = pathSignService.encode(uri2)

                        result1 shouldNotBe result2
                    }
                }

                context("특수 문자가 포함된 URI가 주어지면,") {
                    val hexKey = "736563726574"
                    val hexSalt = "73616c74"

                    beforeTest {
                        every { imgProxyProperties.key } returns hexKey
                        every { imgProxyProperties.salt } returns hexSalt
                        pathSignService = PathSignService(imgProxyProperties)
                    }

                    it("정상적으로 URL-safe Base64로 인코딩된 문자열을 반환한다.") {
                        val uri = "s3://bucket/path/to/image with spaces & special=chars.png"

                        val result = pathSignService.encode(uri)

                        result.shouldNotBeEmpty()
                        result.contains("+") shouldBe false
                        result.contains("/") shouldBe false
                        result.contains("=") shouldBe false
                    }
                }
            }
        }
    }
}

