package com.gabinote.image.imgProxy.integration

import com.gabinote.image.testSupport.testTemplate.IntegrationTestTemplate
import com.gabinote.image.testSupport.testUtil.img.TestImgLoader
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.config.RedirectConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import javax.imageio.ImageIO

class ImgProxyIntegrationTest : IntegrationTestTemplate() {

    override val apiPrefix: String = "/api/v1/image"


    init {
        beforeSpec {
            minioTestHelper.initStorage()
        }
        beforeTest {
            minioTestHelper.clearAllData()
        }

        feature("[ImgProxy] ImgProxy Integration Test") {

            feature("[GET] /api/v1/image - 이미지 프록시 URL 생성") {

                scenario("올바른 요청이 주어지면, 리다이렉트를 따라가 변환된 이미지를 반환한다.") {
                    Given {
                        minioTestHelper.insertFile("test.png", "testset/img/test.png")
                        basePath(apiPrefix)
                        param("fileName", "test.png")
                        param("requestFormat", "png")
                        param("width", 0)
                        param("height", 0)
                        param("enlarge", false)
                    }.When {
                        get()
                    }.Then {
                        statusCode(200)
                        contentType("image/png")
                        val responseBytes = extract().asByteArray()
                        val img = ImageIO.read(ByteArrayInputStream(responseBytes))
                        img shouldNotBe null
                        img.width shouldBe 10
                        img.height shouldBe 10
                    }
                }


            }
        }
    }
}