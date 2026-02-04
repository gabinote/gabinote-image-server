package com.gabinote.image.upload.web.controller

import com.gabinote.image.common.config.aop.auth.NeedAuth
import com.gabinote.image.common.util.context.UserContext
import com.gabinote.image.upload.dto.controller.ImgUploadResControllerDto
import com.gabinote.image.upload.service.ImageUploadService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Validated
@RestController
@RequestMapping("/api/v1/image")
class ImgUploadApiController(
    private val imgUploadService: ImageUploadService,
    private val userContext: UserContext
) {

    /**
     * 이미지 업로드 엔드포인트
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 새로운 이름을 포함한 응답 DTO
     */
    @NeedAuth
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<ImgUploadResControllerDto> {
        val uploader = userContext.uid
        val savedImgName = imgUploadService.uploadImage(file, uploader)
        val resDto = ImgUploadResControllerDto(
            newName = savedImgName,
        )

        return ResponseEntity.ok(resDto)
    }

}