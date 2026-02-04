package com.gabinote.image

import com.gabinote.image.testSupport.testConfig.db.DatabaseContainerInitializer
import com.gabinote.image.testSupport.testConfig.storage.MinioContainerInitializer
import com.gabinote.image.testSupport.testTemplate.IntegrationTestTemplate
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration


class ImageApplicationTests: IntegrationTestTemplate() {

    @Test
    fun contextLoads() {
    }

}
