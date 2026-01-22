package com.gabinote.image.imgProxy.web.controller

import com.gabinote.image.common.util.context.UserContext
import com.gabinote.image.imgProxy.dto.controller.ImgProxyUrlReqControllerDto
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import com.gabinote.image.imgProxy.mapping.ImgProxyMapper
import com.gabinote.image.imgProxy.service.ImgProxyService
import com.gabinote.image.upload.dto.controller.ImgUploadResControllerDto
import com.gabinote.image.upload.service.ImageUploadService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Duration

@Validated
@RestController
@RequestMapping("/api/v1/image")
class ImgProxyApiController(
    private val imgProxyService: ImgProxyService,
    private val imgProxyMapper: ImgProxyMapper,

    @Value("\${imgproxy.cache.cache-ttl}")
    private val cacheTtl: Long,
) {

    /**
     * 이미지 프록시 URL 생성 엔드포인트
     * @param dto 이미지 프록시 URL 요청 DTO
     * @return 생성된 이미지 프록시 URL로 리다이렉트하는 응
     */
    @GetMapping
    fun getImgProxyUrl(
        @Valid dto: ImgProxyUrlReqControllerDto
    ): ResponseEntity<Void> {
        val reqDto = imgProxyMapper.toServiceDto(dto)
        val res = imgProxyService.generateUrl(reqDto)

        return ResponseEntity.status(302)
            .location(URI.create(res))
            .cacheControl(
                CacheControl.maxAge(Duration.ofSeconds(cacheTtl)).cachePrivate()
            )
            .build()
    }

}