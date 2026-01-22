package com.gabinote.image.testSupport.testTemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.testSupport.testUtil.database.TestDataHelper
import com.gabinote.image.testSupport.testConfig.db.DatabaseContainerInitializer
import com.gabinote.image.testSupport.testUtil.time.TestTimeProvider

import com.gabinote.image.testSupport.testConfig.db.UseTestDatabase
import com.gabinote.image.testSupport.testConfig.storage.MinioContainerInitializer
import com.gabinote.image.testSupport.testUtil.minio.MinioTestHelper
import com.gabinote.image.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCaseOrder
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers


@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Import(
    TestDataHelper::class,
    TestTimeProvider::class,
    MinioTestHelper::class,
    TestUuidSource::class,
)
@Testcontainers
@ContextConfiguration(initializers = [MinioContainerInitializer::class, DatabaseContainerInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestTemplate : FeatureSpec() {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var testDataHelper: TestDataHelper

    @Autowired
    lateinit var minioTestHelper: MinioTestHelper


    val apiPrefix: String = "/api/v1"


    fun beforeSpec() {
        RestAssured.basePath = apiPrefix
        RestAssured.port = port
    }

    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Random

    init {
        beforeSpec {
            beforeSpec()
        }

    }
}