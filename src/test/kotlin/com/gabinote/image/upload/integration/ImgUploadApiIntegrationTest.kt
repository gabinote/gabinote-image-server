package com.gabinote.image.upload.integration

import com.gabinote.image.testSupport.testTemplate.IntegrationTestTemplate
import com.gabinote.image.testSupport.testUtil.img.TestImgLoader
import com.gabinote.image.testSupport.testUtil.uuid.TestUuidSource
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType

@Import(TestImgLoader::class)
class ImgUploadApiIntegrationTest : IntegrationTestTemplate() {
    override val apiPrefix: String = "/api/v1/image"
    @Autowired
    private lateinit var testImgLoader: TestImgLoader

    init{
        beforeSpec {
            minioTestHelper.initStorage()
        }
        beforeTest{
            minioTestHelper.clearAllData()
        }

        feature("[ImgUpload] ImgUploadApi Integration Test") {

            feature("이미지 업로드 API") {
                scenario("이미지 업로드가 정상적으로 처리된다.") {
                    val uploader = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

                    Given{
                        testDataHelper.setData("/testset/integration/save-before.json")
                        val testImg = testImgLoader.loadAsMockMultipartFile(
                            path = "testset/img/test.png",
                            originalFilename = "test.png",
                            contentType = MediaType.IMAGE_PNG
                        )
                        basePath(apiPrefix)
                        multiPart("file", testImg.originalFilename, testImg.bytes, MediaType.IMAGE_PNG_VALUE)
                        header("X-Token-Sub", uploader)
                        header("X-Token-Roles", "admin")
                    }.When {
                        post()
                    }.Then {
                        statusCode(200)
                        val expectedImgName = "${TestUuidSource.UUID_STRING}.png"
                        minioTestHelper.verifyWithFile(expectedImgName, "testset/img/test.png")
                        testDataHelper.assertData("/testset/integration/save-after.json")
                    }
                }
            }
        }
    }

}