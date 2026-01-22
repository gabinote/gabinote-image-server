package com.gabinote.image.imgProxy.service

import com.gabinote.image.common.config.properties.ImgProperties
import com.gabinote.image.common.config.properties.ImgProxyProperties
import com.gabinote.image.common.util.exception.service.ResourceNotValid
import com.gabinote.image.common.util.extensions.ExtensionHelper.toZeroOrOne
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import com.gabinote.image.storage.service.ImageStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 이미지 프록시 서비스를 제공하는 클래스
 */
@Service
class ImgProxyService (
    private val imgProxyProperties: ImgProxyProperties,
    private val imgProperties: ImgProperties,
    private val imgStorageService: ImageStorageService,
    private val pathSignService: PathSignService
){


    /**
     * 이미지 프록시 URL을 생성 (Signed URL)
     * @param dto 이미지 프록시 URL 요청 데이터 전송 객체
     * @return 생성된 이미지 프록시 URL 문자열
     * @throws ResourceNotValid 요청 데이터가 유효하지 않은 경우
     */
    fun generateUrl(dto: ImgProxyUrlReqServiceDto): String {
        validationDto(dto)
        val imagePath = imgStorageService.getSavePath(dto.fileName)
        val encodeImagePath = pathSignService.encode(imagePath)
        val proxyPath = "/rs:fit:${dto.width}:${dto.height}:${dto.enlarge.toZeroOrOne()}/$encodeImagePath.${dto.requestFormat}"
        val signedProxyPath = pathSignService.sign(proxyPath)

        return UriComponentsBuilder.fromUriString(imgProxyProperties.baseUrl)
            .pathSegment(signedProxyPath)
            .path(proxyPath)
            .build()
            .toUriString()
    }

    /**
     * 요청 데이터 유효성 검사
     * @param dto 이미지 프록시 URL 요청 데이터 전송 객체
     * @throws ResourceNotValid 요청 데이터가 유효하지 않은 경우
     */
    private fun validationDto(dto: ImgProxyUrlReqServiceDto) {
        //1. 사이즈 체크
        //1.1 최대 사이즈 체크
        if(dto.height > imgProperties.maxHeight || dto.width > imgProperties.maxWidth) {
            throw ResourceNotValid(
                "height,width",
                listOf("Height or Width Exceeds the maximum size. maxHeight: ${imgProperties.maxHeight}, maxWidth: ${imgProperties.maxWidth}")
            )
        }

        //2. 확장자 체크
        val allowedFormats = imgProperties.allowedFormats
        if(!allowedFormats.contains(dto.requestFormat.uppercase())) {
            throw ResourceNotValid(
                "requestFormat",
                listOf("Unsupported image format: ${dto.requestFormat}. Allowed formats: ${allowedFormats.joinToString(", ")}")
            )
        }
    }

}