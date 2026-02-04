package com.gabinote.image.imgProxy.mapping

import com.gabinote.image.imgProxy.dto.controller.ImgProxyUrlReqControllerDto
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import com.gabinote.image.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe

class ImgProxyMapperTest : MockkTestTemplate() {
    private val imgProxyMapper = ImgProxyMapperImpl()

    init {
        describe("[ImgProxy] ImgProxyMapperTest") {
            describe("ImgProxyMapper.toServiceDto") {
                context("ImgProxyUrlReqControllerDto가 주어지면,") {
                    val dto = ImgProxyUrlReqControllerDto(
                        fileName = "test-image.png",
                        requestFormat = "webp",
                        width = 100,
                        height = 200,
                        enlarge = false
                    )

                    val expected = ImgProxyUrlReqServiceDto(
                        fileName = "test-image.png",
                        requestFormat = "webp",
                        width = 100,
                        height = 200,
                        enlarge = false
                    )

                    it("ImgProxyUrlReqServiceDto로 올바르게 매핑한다.") {
                        val result = imgProxyMapper.toServiceDto(dto)

                        result shouldBe expected
                    }
                }

                context("enlarge가 true인 ControllerDto가 주어지면,") {
                    val dto = ImgProxyUrlReqControllerDto(
                        fileName = "large-image.jpg",
                        requestFormat = "png",
                        width = 1920,
                        height = 1080,
                        enlarge = true
                    )

                    val expected = ImgProxyUrlReqServiceDto(
                        fileName = "large-image.jpg",
                        requestFormat = "png",
                        width = 1920,
                        height = 1080,
                        enlarge = true
                    )

                    it("ImgProxyUrlReqServiceDto로 올바르게 매핑한다.") {
                        val result = imgProxyMapper.toServiceDto(dto)

                        result shouldBe expected
                    }
                }
            }
        }
    }
}

