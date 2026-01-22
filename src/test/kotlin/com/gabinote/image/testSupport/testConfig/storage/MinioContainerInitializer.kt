package com.gabinote.image.testSupport.testConfig.storage


import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

private val log = KotlinLogging.logger {}

class MinioContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        val testAccessKey = "test-access-key"
        val testSecretKey = "test-secret-key"
        @JvmStatic
        val minio = MinIOContainer("minio/minio:latest").apply {
            withLabel("test-container", "mongodb")
            withUserName(testAccessKey)
            withPassword(testSecretKey)
        }


    }

    override fun initialize(context: ConfigurableApplicationContext) {
        // 테스트 컨테이너 시작
        minio.start()

        log.debug { "run minio container ${minio.s3URL}" }

        TestPropertyValues.of(
            "spring.cloud.aws.s3.endpoint=${minio.s3URL}",
            "spring.cloud.aws.s3.bucket=test-bucket",
            "spring.cloud.aws.credentials.access-key=$testAccessKey",
            "spring.cloud.aws.credentials.secret-key=$testSecretKey",
            "spring.cloud.aws.region.static=us-east-1"
        ).applyTo(context.environment)

    }


}