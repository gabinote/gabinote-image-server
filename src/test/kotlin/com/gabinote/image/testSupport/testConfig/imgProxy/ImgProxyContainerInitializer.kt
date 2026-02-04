package com.gabinote.image.testSupport.testConfig.imgProxy


import com.gabinote.image.testSupport.testConfig.container.ContainerNetworkHelper
import com.gabinote.image.testSupport.testConfig.storage.MinioContainerInitializer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

private val log = KotlinLogging.logger {}

class ImgProxyContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        val key = "77a0ef3a18b8970b8e91515880c61af5eb13eba42825a1fd68fd57dcf6483f1b"
        val salt = "efb10146e90458b916607a41fe2f6f7e93f9bdaded3ecf316102885647bd9960"
        @JvmStatic
        val imgProxy = GenericContainer(DockerImageName.parse("darthsim/imgproxy:latest")).apply {
            withNetwork(ContainerNetworkHelper.testNetwork)
            withNetworkAliases("imgproxy")
            withLabel("test-container", "imgProxy")
            withEnv("IMGPROXY_KEY", key)
            withEnv("IMGPROXY_SALT", salt)
            withEnv("IMGPROXY_USE_S3", "true")
            withEnv("IMGPROXY_S3_ENDPOINT", "http://minio:9000")
//            withEnv("IMGPROXY_S3_ACCESS_KEY_ID", MinioContainerInitializer.testAccessKey)
//            withEnv("IMGPROXY_S3_SECRET_ACCESS_KEY", MinioContainerInitializer.testSecretKey)
            withEnv("AWS_ACCESS_KEY_ID", MinioContainerInitializer.testAccessKey)
            withEnv("AWS_SECRET_ACCESS_KEY", MinioContainerInitializer.testSecretKey)
            withEnv("IMGPROXY_S3_SSL_DISABLE", "true")
            withEnv("IMGPROXY_AWS_REGION", "us-east-1")
            withEnv("AWS_EC2_METADATA_DISABLED", "true")
            withExposedPorts(8080)
            waitingFor(Wait.forHttp("/health").forStatusCode(200))
            withReuse(true)
        }


    }

    override fun initialize(context: ConfigurableApplicationContext) {

        imgProxy.start()
        val imgProxyUrl = "http://${imgProxy.host}:${imgProxy.getMappedPort(8080)}"
        log.debug { "run imgProxy container $imgProxyUrl" }

        TestPropertyValues.of(
            "imgproxy.base-url=$imgProxyUrl",
            "imgproxy.key=$key",
            "imgproxy.salt=$salt",
        ).applyTo(context.environment)

    }


}