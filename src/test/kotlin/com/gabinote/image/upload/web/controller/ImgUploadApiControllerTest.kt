package com.gabinote.image.upload.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.image.common.util.context.UserContext
import com.gabinote.image.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.image.testSupport.testUtil.data.img.TestImageHelper
import com.gabinote.image.upload.dto.controller.ImgUploadResControllerDto
import com.gabinote.image.upload.service.ImageUploadService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [ImgUploadApiController::class])
class ImgUploadApiControllerTest : WebMvcTestTemplate() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var imgUploadService: ImageUploadService

    @MockkBean
    private lateinit var userContext: UserContext

    private val apiPrefix = "/api/v1/image"

    init {
        beforeTest {
            clearAllMocks()
        }

        describe("[ImgUpload] ImgUploadApiController Test") {
            describe("ImgUploadApiController.uploadImage") {
                context("올바른 이미지 파일이 주어지면,") {
                    val testUploader = "test-uploader-uid"
                    val validImage = TestImageHelper.PNG_1X1
                    val mockFile = validImage.toMockMultipartFile("test-image")
                    val savedImgName = "converted-uuid-name"

                    beforeTest {
                        every { userContext.uid } returns testUploader
                        every { userContext.isAuthorized } returns true
                        every { imgUploadService.uploadImage(any(), testUploader) } returns savedImgName
                    }

                    val expectedResponse = ImgUploadResControllerDto(newName = savedImgName)

                    it("이미지를 업로드하고 200 OK를 응답한다.") {
                        mockMvc.perform(
                            multipart(apiPrefix)
                                .file(mockFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                            .andDo(
                                document(
                                    "imgUpload/uploadImage",
                                    
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParts(
                                        partWithName("file").description("업로드할 이미지 파일")
                                    ),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("ImgUpload")
                                            .description("이미지 업로드")
                                            .responseFields(
                                                fieldWithPath("new_name").description("저장된 이미지 파일 이름")
                                            )
                                            .responseSchema(Schema("ImgUploadResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            imgUploadService.uploadImage(any(), testUploader)
                        }
                    }
                }

                describe("실패 케이스") {
                    context("파일이 없으면,") {
                        it("400 Bad Request를 응답한다.") {
                            mockMvc.perform(
                                multipart(apiPrefix)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
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

