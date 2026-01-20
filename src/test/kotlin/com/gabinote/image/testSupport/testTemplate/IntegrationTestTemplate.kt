package com.gabinote.image.testSupport.testTemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.testSupport.testUtil.database.TestDataHelper
import com.gabinote.image.testSupport.testUtil.time.TestTimeProvider

import com.gabinote.image.testSupport.testConfig.db.UseTestDatabase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCaseOrder
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers


@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Import(
    TestDataHelper::class,
    TestTimeProvider::class,
)
@Testcontainers
@UseTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestTemplate : FeatureSpec() {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var testDataHelper: TestDataHelper



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