package com.gabinote.image.common.util.file

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.tika.Tika
import org.apache.tika.config.TikaConfig
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class FileHelper(
    private val tika: Tika,
) {

    /**
     * 파일 바이트 배열에서 MIME 타입을 추출합니다.
     * @param fileBytes 파일의 바이트 배열
     * @return 추출된 MIME 타입 문자열
     * @throws IllegalArgumentException MIME 타입을 추출할 수 없는 경우
     */
    fun extractMimeType(fileBytes: ByteArray): String {
        return tika.detect(fileBytes)
    }

}