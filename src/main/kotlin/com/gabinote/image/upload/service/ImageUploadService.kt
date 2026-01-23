package com.gabinote.image.upload.service

import com.gabinote.image.common.config.properties.ImgProperties
import com.gabinote.image.common.util.exception.service.ResourceNotValid
import com.gabinote.image.common.util.img.ImgExtension
import com.gabinote.image.common.util.img.ImgMimeType
import com.gabinote.image.common.util.img.ImgName
import com.gabinote.image.common.util.img.ImgStem
import com.gabinote.image.common.util.str.StringHelper.isSimpleFileNameFormat
import com.gabinote.image.common.util.uuid.UuidSource
import com.gabinote.image.meta.dto.service.ImageMetaDataCreateReqServiceDto
import com.gabinote.image.meta.service.ImageMetaDataService
import com.gabinote.image.storage.service.ImageStorageService
import org.apache.tika.Tika
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.UUID
import javax.imageio.ImageIO

/**
 * 이미지 업로드를 처리하는 서비스 클래스
 */
// TODO: 비 직관적이고 지저분한 이미지 이름 및 포맷 정보 리펙토링
@Service
class ImageUploadService(
    private val imageMetaDataService: ImageMetaDataService,
    private val imageStorageService: ImageStorageService,
    private val uuidSource: UuidSource,
    private val imgProperties: ImgProperties,
    private val tika: Tika

) {

    /**
     * 해당 이미지를 저장소에 업로드하고, 메타데이터를 생성
     * @param file 업로드할 이미지 파일
     * @param uploader 업로더 정보
     * @return 저장된 이미지의 변환된 이름 (확장자 포함)
     */
    @Transactional
    fun uploadImage(file: MultipartFile,uploader: String): String{
        val fileBytes = file.bytes
        val imgName = extractImgName(file)
        val imageInfo = extractImageInfo(fileBytes, imgName)
        validationImgInfo(imageInfo)

        val convertedName = ImgName.from(uuidSource.generateUuid().toString(), imgName.ext)

        saveMetaData(imageInfo, uploader, convertedName)


        imageStorageService.saveImageToStorage(
            convertedName = convertedName.fullName,
            imageStream = ByteArrayInputStream(fileBytes)
        )

        return convertedName.fullName
    }

    /**
     * 이미지의 메타정보 추출
     * @param bytes 이미지 바이트 배열
     * @param filename 원본 파일 이름
     * @return 추출된 이미지 정보
     */
    private fun extractImageInfo(bytes: ByteArray, imgName: ImgName): ImageInfo{
        val mimeType = extractMimeType(bytes)
        val bufferedImage = runCatching {
            ImageIO.read(ByteArrayInputStream(bytes))
                ?: throw RuntimeException("Unsupported image format or corrupted data")

        }.getOrElse { e ->
            throw ResourceNotValid(
                "Image",
                listOf("Unable to read image data from file: ${imgName.fullName}")
            )
        }

        return ImageInfo(
            width = bufferedImage.width,
            height = bufferedImage.height,
            size = bytes.size.toLong(),
            format = imgName.ext,
            originalName = imgName,
            mimeType = mimeType
        )
    }

    /**
     * 이미지 메타데이터 저장
     * @param imageInfo 이미지 정보
     * @param uploader 업로더 정보
     * @param convertedName 변환된 이미지 이름
     */
    private fun saveMetaData(imageInfo: ImageInfo, uploader: String,convertedName: ImgName){

        val savePath = imageStorageService.getSavePath(convertedName.fullName)

        val metaData = ImageMetaDataCreateReqServiceDto(
            originName = imageInfo.originalName.fullName,
            convertedName = convertedName.fullName,
            format = imageInfo.format.ext().lowercase(),
            size = imageInfo.size,
            uploadBy = uploader,
            storagePath = savePath,
            width = imageInfo.width,
            height = imageInfo.height
        )
        imageMetaDataService.createImageMetaData(metaData)
    }

    /**
     * 이미지 정보 유효성 검사
     * @param imageInfo 이미지 정보
     * @throws ResourceNotValid 유효성 검사 실패 시 예외 발생
     */
    private fun validationImgInfo(imageInfo: ImageInfo) {
        // 1. 파일 크기 제한
        if (imageInfo.size > imgProperties.maxFileSize) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("File size exceeds the maximum limit of ${imgProperties.maxFileSize} bytes")
            )
        }

        // 2. 이미지 해상도 제한
        if (imageInfo.width > imgProperties.maxWidth || imageInfo.height > imgProperties.maxHeight) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Image resolution exceeds the maximum limit of ${imgProperties.maxWidth}x${imgProperties.maxHeight} pixels")
            )
        }

        //3. 확장자 제한
        if(imageInfo.format.ext() !in imgProperties.allowedFormatSet) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Unsupported image format: ${imageInfo.format.ext()}. Supported formats are: ${imgProperties.allowedFormatSet.joinToString(", ")}")
            )
        }

        //4. 원본 파일 이름 사이즈 제한
        if(imageInfo.originalName.stemStr.length > imgProperties.maxFileNameSize) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Original filename exceeds the maximum length of ${imgProperties.maxFileNameSize} characters")
            )
        }

        //5. MIME 타입 검사
        //5-1. image/ 로 시작하는지 검사
        if(!imageInfo.mimeType.mimeType().startsWith("image/")) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Invalid MIME type: ${imageInfo.mimeType.mimeType()}. Expected an image MIME type.")
            )
        }

        //5-2. 확장자와 MIME 타입 매칭 검사
        if(imageInfo.format.ext() != imageInfo.mimeType.ext()) {
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("File extension ${imageInfo.format.ext()} does not match MIME type ${imageInfo.mimeType.mimeType()}.")
            )
        }

    }

    /**
     * 원본 파일 이름 추출
     * @param file 업로드된 파일
     * @return 원본 파일 이름
     * @throws ResourceNotValid 원본 파일 이름이 없는 경우
     */
    private fun extractImgName(file:MultipartFile): ImgName {
        //1. d이름이 없는 경우 예외 처리
        val name = file.originalFilename ?: throw ResourceNotValid(
            name = "Image",
            reasons = listOf("Original filename is missing")
        )

        //2. 이름이 비어있는 경우 예외 처리
        if(name.isBlank()){
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Original filename is missing")
            )
        }


        //3. 파일 이름 포맷이 아닌 경우 예외 처리
        if(!name.isSimpleFileNameFormat()){
            throw ResourceNotValid(
                name = "Image",
                reasons = listOf("Original filename format is invalid: $name")
            )
        }

        //4. 특수하게 jpg 확장자라면, jpeg로 변환
        val ext = name.substringAfterLast('.')
        val correctedName = if(ext.equals("jpg", ignoreCase = true)){
            name.substringBeforeLast('.') + ".jpeg"
        } else {
            name
        }

        return ImgName(correctedName)

    }


    /**
     * 파일 확장자 추출 (MIME 타입 기반)
     * @param byte 파일 바이트 배열
     * @return 파일 확장자
     * @throws ResourceNotValid 확장자 추출 실패 시 예외 발생
     */
    private fun extractMimeType(byte: ByteArray): ImgMimeType {
        return runCatching {
            val res = tika.detect(byte)
            ImgMimeType(res)
        }.getOrElse { e ->
            throw ResourceNotValid(
                "Image",
                listOf("Unable to detect file mimeType")
            )
        }

    }

    private data class ImageInfo (
        val originalName: ImgName,
        val width: Int,
        val height: Int,
        val size: Long,
        val format: ImgExtension,
        val mimeType: ImgMimeType,
    )

}