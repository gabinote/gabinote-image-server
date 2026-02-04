package com.gabinote.image.meta.domain

import com.gabinote.image.testSupport.testTemplate.RepositoryTestTemplate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

class ImageMetaDataRepositoryTest : RepositoryTestTemplate() {

    override val baseData = "base.json"
    override val baseDataDir = "/testset/domain"

    @Autowired
    private lateinit var imageMetaDataRepository: ImageMetaDataRepository

    init {
        describe("[ImageMetaData] ImageMetaDataRepositoryTest") {

            describe("ImageMetaDataRepositoryTest.findByConvertedName") {
                context("유효한 convertedName이 주어지면") {
                    useBaseData()
                    val validConvertedName = "a1b2c3d4-e5f6-4789-0123-4567890abcde.png"
                    it("해당 convertedName을 가진 ImageMetaData를 반환한다") {
                        val res = imageMetaDataRepository.findByConvertedName(validConvertedName)

                        res shouldNotBe null
                        res!!.convertedName shouldBe validConvertedName
                        res.originName shouldBe "profile_image.png"
                    }
                }

                context("존재하지 않는 convertedName이 주어지면") {
                    useBaseData()
                    val invalidConvertedName = "00000000-0000-0000-0000-000000000000.png"
                    it("null을 반환한다") {
                        val res = imageMetaDataRepository.findByConvertedName(invalidConvertedName)

                        res shouldBe null
                    }
                }
            }

            describe("ImageMetaDataRepositoryTest.countAllByUploadBy") {
                context("이미지가 있는 userId가 주어지면") {
                    useBaseData()
                    val validUserId = "user_alpha_1234"
                    it("해당 userId가 업로드한 이미지 개수를 반환한다") {
                        val res = imageMetaDataRepository.countAllByUploadBy(validUserId)

                        res shouldBe 2L
                    }
                }

                context("이미지가 없는 userId가 주어지면") {
                    useBaseData()
                    val userIdWithNoImages = "user-0000"
                    it("0을 반환한다") {
                        val res = imageMetaDataRepository.countAllByUploadBy(userIdWithNoImages)

                        res shouldBe 0L
                    }
                }
            }

            describe("ImageMetaDataRepositoryTest.deleteAllByUploadBy") {
                context("유효한 userId가 주어지면") {
                    testDataHelper.setData("$baseDataDir/delete-before.json")
                    val targetUserId = "target_user"
                    it("해당 userId가 업로드한 모든 이미지를 삭제한다") {
                        val deletedCount = imageMetaDataRepository.deleteAllByUploadBy(targetUserId)

                        deletedCount shouldBe 2L
                        testDataHelper.assertData("$baseDataDir/delete-after.json")
                    }
                }

                context("이미지가 없는 userId가 주어지면") {
                    testDataHelper.setData("$baseDataDir/delete-before.json")
                    val userIdWithNoImages = "user-0000"
                    it("아무것도 삭제되지 않는다") {
                        val deletedCount = imageMetaDataRepository.deleteAllByUploadBy(userIdWithNoImages)

                        deletedCount shouldBe 0L
                    }
                }
            }

            describe("ImageMetaDataRepositoryTest.save(신규)") {
                context("신규 ImageMetaData가 주어지면") {
                    testDataHelper.setData("$baseDataDir/save-before.json")
                    val newImageMetaData = ImageMetaData(
                        originName = "new_image.png",
                        convertedName = "d4e5f6a7-b8c9-4012-3456-789012cdefgh.png",
                        format = "png",
                        size = 153600,
                        width = 1024,
                        height = 768,
                        storagePath = "/images/2024/08/",
                        uploadBy = "user_gamma_9012"
                    )
                    it("ImageMetaData를 저장한다") {

                        imageMetaDataRepository.save(newImageMetaData)
                        testDataHelper.assertData("$baseDataDir/save-after.json")
                    }
                }
            }
        }
    }
}