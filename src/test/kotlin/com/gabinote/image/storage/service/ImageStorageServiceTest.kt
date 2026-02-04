package com.gabinote.image.storage.service

import com.gabinote.image.testSupport.testTemplate.IntegrationTestTemplate
import com.gabinote.image.testSupport.testUtil.img.TestImgLoader
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

@Import(TestImgLoader::class)
class ImageStorageServiceTest : IntegrationTestTemplate() {

    @Autowired
    private lateinit var imageStorageService: ImageStorageService

    @Autowired
    private lateinit var testImgLoader: TestImgLoader
    init{
        beforeSpec {
            minioTestHelper.initStorage()
        }
        beforeTest{
            minioTestHelper.clearAllData()
        }


        feature("[Storage] ImageStorageService Test") {

            feature("saveImageToStorage") {
                scenario("정상적인 이미지가 주어지면 실제로 저장된다."){
                    val testImgDir = "testset/img/test.png"
                    val imgStream = testImgLoader.loadAsStream(testImgDir)
                    val key = "test-storage-image.png"
                    imageStorageService.saveImageToStorage(key, imgStream)
                    minioTestHelper.verifyWithFile(key, testImgDir) shouldBe true
                }
            }


            feature("getSavePath") {
                scenario("저장 경로가 올바르게 생성된다."){
                    val key = "sample-image.png"
                    val expectedPath = "s3://test-bucket/$key"
                    val resultPath = imageStorageService.getSavePath(key)
                    resultPath shouldBe expectedPath
                }
            }

        }

    }
}