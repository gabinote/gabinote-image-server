package com.gabinote.image.storage.service

import com.gabinote.image.common.util.exception.service.ResourceNotValid
import com.gabinote.image.meta.domain.ImageMetaData
import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * S3 Storage 기능을 담당하는 서비스 클래스
 * (실제로는 aws s3를 사용하지는 않고, minio를 사용함.)
 */
private val logger = KotlinLogging.logger {}

@Service
class ImageStorageService(
    private val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String
) {

    /**
     * 이미지를 S3 스토리지에 저장
     * @param convertedName 변환된 이미지 이름
     * @param imageStream 이미지 입력 스트림
     */
    fun saveImageToStorage(convertedName: String, imageStream: InputStream){
        try {
            s3Template.upload(bucketName, convertedName, imageStream)

        } catch (e: Exception) {
            logger.error(e) { "Failed to upload image" }
            throw RuntimeException("Failed to upload image to S3", e)
        }
    }

    /**
     * 이미지 저장 경로 생성
     * @param convertedName 변환된 이미지 이름
     * @return 이미지 저장 경로
     */
    fun getSavePath(convertedName: String): String {
        return "s3://$bucketName/$convertedName"
    }


}