package com.gabinote.image.testSupport.testUtil.img

import org.springframework.boot.test.context.TestComponent
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException
import java.io.InputStream

@TestComponent
class TestImgLoader {
    /**
     * classpath에 있는 파일을 읽어 InputStream으로 반환
     */
    fun loadAsStream(path: String): InputStream {
        val resource = ClassPathResource(path)
        if (!resource.exists()) {
            throw FileNotFoundException("Resource not found in classpath: $path")
        }
        return resource.inputStream
    }

    /**
     * classpath에 있는 파일을 읽어 ByteArray로 반환
     */
    fun loadAsBytes(path: String): ByteArray {
        val resource = ClassPathResource(path)
        if (!resource.exists()) {
            throw FileNotFoundException("Resource not found in classpath: $path")
        }
        return resource.contentAsByteArray
    }
}