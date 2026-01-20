package com.gabinote.image.testSupport.testTemplate


import com.gabinote.image.testSupport.testConfig.db.UseTestDatabase
import com.gabinote.coffeenote.testSupport.testUtil.database.TestDataHelper
import com.gabinote.image.testSupport.testUtil.time.TestTimeConfig
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import


@ExtendWith(MockKExtension::class)
@Import(
    TestDataHelper::class,
    TestTimeConfig::class,
)
@DataMongoTest
@UseTestDatabase
abstract class RepositoryTestTemplate : DescribeSpec() {

    @Autowired
    lateinit var testDataHelper: TestDataHelper

    val baseDataDir = "/testsets/note/domain"
    val baseData = "base.json"
    fun useBaseData() {
        testDataHelper.setData("$baseDataDir/$baseData")
    }
}