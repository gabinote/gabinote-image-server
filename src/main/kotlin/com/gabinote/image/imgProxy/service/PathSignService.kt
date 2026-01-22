package com.gabinote.image.imgProxy.service

import com.gabinote.image.common.config.properties.ImgProxyProperties
import org.springframework.stereotype.Service
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class PathSignService(
    private val imgProxyProperties: ImgProxyProperties,
) {

    private val urlEncoder = Base64.getUrlEncoder().withoutPadding()

    private val signAlgorithm = "HmacSHA256"
    private val saltBytes: ByteArray
    private val secretKeySpec: SecretKeySpec

    init {
        val keyBytes = decodeHexOrString(imgProxyProperties.key)
        this.saltBytes = decodeHexOrString(imgProxyProperties.salt)

        this.secretKeySpec = SecretKeySpec(keyBytes, signAlgorithm)
    }

    /**
     * 주어진 경로에 대한 서명을 생성
     * @param path 서명할 경로 문자열
     * @return 생성된 서명 문자열 (URL-safe Base64 인코딩)
     */
    fun sign(path: String): String {
        val mac = Mac.getInstance(signAlgorithm)
        mac.init(secretKeySpec)
        mac.update(saltBytes)
        val hash = mac.doFinal(path.toByteArray())
        return urlEncoder.encodeToString(hash)
    }

    /**
     * 주어진 URI를 URL-safe Base64로 인코딩
     * @param uri 인코딩할 URI 문자열
     * @return 인코딩된 URI 문자열
     */
    fun encode(uri: String): String {
        return urlEncoder.encodeToString(uri.toByteArray())
    }

    private fun decodeHexOrString(input: String): ByteArray {
        return try {
            java.util.HexFormat.of().parseHex(input)
        } catch (e: Exception) {
            input.toByteArray()
        }
    }
}