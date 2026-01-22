package com.gabinote.image.testSupport.testUtil.data.img

import org.springframework.mock.web.MockMultipartFile
import java.util.Base64

data class TestImg(
    val width: Long,
    val height: Long,
    val format: String,
    val base64: String,
){
    fun toMockMultipartFile(
        fileName: String?,
    ): MockMultipartFile{


        val content = Base64.getDecoder().decode(base64)

        return MockMultipartFile(
            "file",
            fileName?.let{"$fileName.$format"},
            "image/$format",
            content
        )
    }

    fun toMockMultipartFileNoExt(
        fileName: String,
    ): MockMultipartFile{
        val pureBase64 = if (base64.contains(",")) base64.split(",")[1] else base64

        val content = Base64.getDecoder().decode(pureBase64)

        return MockMultipartFile(
            "file",
            fileName,
            "image/$format",
            content
        )
    }


}