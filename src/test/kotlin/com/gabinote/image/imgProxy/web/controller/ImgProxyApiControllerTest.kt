package com.gabinote.image.imgProxy.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.image.imgProxy.dto.controller.ImgProxyUrlReqControllerDto
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import com.gabinote.image.imgProxy.mapping.ImgProxyMapper
import com.gabinote.image.imgProxy.service.ImgProxyService
import com.gabinote.image.testSupport.testTemplate.WebMvcTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [ImgProxyApiController::class])
class ImgProxyApiControllerTest : WebMvcTestTemplate() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var imgProxyService: ImgProxyService

    @MockkBean
    private lateinit var imgProxyMapper: ImgProxyMapper

    private val apiPrefix = "/api/v1/image"

    init {
        beforeTest {
            clearAllMocks()
        }

        describe("[ImgProxy] ImgProxyApiController Test") {
            describe("ImgProxyApiController.getImgProxyUrl") {
                context("올바른 요청이 주어지면,") {
                    val fileName = "test-image.png"
                    val requestFormat = "webp"
                    val width = 100
                    val height = 100
                    val enlarge = false

                    val serviceDto = mockk<ImgProxyUrlReqServiceDto>()
                    val generatedUrl = "https://imgproxy.example.com/signed/rs:fit:100:100:0/encoded.webp"

                    beforeTest {
                        every { imgProxyMapper.toServiceDto(any<ImgProxyUrlReqControllerDto>()) } returns serviceDto
                        every { imgProxyService.generateUrl(serviceDto) } returns generatedUrl
                    }

                    it("302 Redirect와 Location 헤더를 응답한다.") {
                        mockMvc.perform(
                            get(apiPrefix)
                                .param("fileName", fileName)
                                .param("requestFormat", requestFormat)
                                .param("width", width.toString())
                                .param("height", height.toString())
                                .param("enlarge", enlarge.toString())
                        )
                            .andDo(print())
                            .andExpect(status().isFound)
                            .andExpect(header().string("Location", generatedUrl))
                            .andExpect(header().exists("Cache-Control"))
                            .andDo(
                                document(
                                    "imgProxy/getImgProxyUrl",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("ImgProxy")
                                            .description("이미지 프록시 URL 생성")
                                            .queryParameters(
                                                parameterWithName("fileName").description("이미지 파일 이름"),
                                                parameterWithName("requestFormat").description("요청 이미지 포맷 (예: webp, png, jpg)"),
                                                parameterWithName("width").description("이미지 너비"),
                                                parameterWithName("height").description("이미지 높이"),
                                                parameterWithName("enlarge").description("이미지 확대 여부")
                                            )
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            imgProxyMapper.toServiceDto(any<ImgProxyUrlReqControllerDto>())
                            imgProxyService.generateUrl(serviceDto)
                        }
                    }
                }


                describe("실패 케이스") {
                    context("fileName이 비어있으면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                get(apiPrefix)
                                    .param("ileNam", "")
                                    .param("requestFormat", "webp")
                                    .param("width", "100")
                                    .param("height", "100")
                                    .param("enlarge", "false")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("requestFormat이 비어있으면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                get(apiPrefix)
                                    .param("fileName", "test.png")
                                    .param("requestFormat", "")
                                    .param("width", "100")
                                    .param("height", "100")
                                    .param("enlarge", "false")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("width가 음수이면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                get(apiPrefix)
                                    .param("fileName", "test.png")
                                    .param("requestFormat", "webp")
                                    .param("width", "-1")
                                    .param("height", "100")
                                    .param("enlarge", "false")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("height가 음수이면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                get(apiPrefix)
                                    .param("fileName", "test.png")
                                    .param("requestFormat", "webp")
                                    .param("width", "100")
                                    .param("height", "-1")
                                    .param("enlarge", "false")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("필수 파라미터가 누락되면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                get(apiPrefix)
                                    .param("fileName", "test.png")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }
            }
        }
    }
}

