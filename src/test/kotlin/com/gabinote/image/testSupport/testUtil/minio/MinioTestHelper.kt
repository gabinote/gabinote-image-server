package com.gabinote.image.testSupport.testUtil.minio

import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import org.springframework.core.io.ClassPathResource
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import java.security.MessageDigest


private val logger = KotlinLogging.logger {}

@TestComponent
class MinioTestHelper(
    private val s3Template: S3Template,
    private val s3Client: S3Client,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String
) {

    /**
     * 스토리지 초기화 (버킷 생성 등)
     */
    fun initStorage() {
        logger.debug { "Initializing storage for bucket: $bucketName" }

        // 버킷이 존재하지 않으면 생성
        val buckets = s3Client.listBuckets().buckets().map { it.name() }
        if (!buckets.contains(bucketName)) {
            s3Client.createBucket { it.bucket(bucketName) }

            logger.debug { "Created bucket: $bucketName" }
        } else {
            logger.debug { "Bucket already exists: $bucketName" }
        }
    }

    /**
     * 버킷 내 모든 데이터를 삭제하여 초기화
     */
    fun clearAllData() {
        logger.debug { "Clearing all data from bucket: $bucketName" }
        val buckets = s3Client.listBuckets().buckets().map { it.name() }

        if (!buckets.contains(bucketName)) {
            logger.debug { "Bucket does not exist, pass delete: $bucketName" }
        }
        val listObjects = s3Client.listObjectsV2 { it.bucket(bucketName) }

        if (listObjects.hasContents()) {
            val objectIds = listObjects.contents().map {
                ObjectIdentifier.builder().key(it.key()).build()
            }

            s3Client.deleteObjects(
                DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objectIds).build())
                    .build()
            )
        }

    }

    /**
     * Minio에 해당 이름을 가진 이미지가 존재하는지 확인
     * @param objectKey 확인할 객체의 키(이름)
     * @return 존재 여부
     */
    fun existsObject(objectKey: String): Boolean {
        return try {
            s3Template.download(bucketName, objectKey)
            true
        } catch (e: Exception) {
            logger.debug { "Object not found: $objectKey" }
            false
        }
    }

    fun insertFile(objectKey: String, classpathFilePath: String) {
        val resource = ClassPathResource(classpathFilePath)
        s3Template.upload(bucketName, objectKey, resource.inputStream)
    }


    /**
     * Minio에 저장된 파일과 classpath에 있는 파일의 해시를 비교하여 동일한지 확인
     * @param objectKey Minio에 저장된 객체의 키(이름)
     * @param expectPath classpath 내 비교할 파일의 경로
     * @return 동일하면 true, 다르면 false
     */
    fun verifyWithFile(objectKey: String, expectPath: String): Boolean {
        // Minio에서 파일 다운로드
        val minioResource = try {
            s3Template.download(bucketName, objectKey)
        } catch (e: Exception) {
            logger.error(e) { "Failed to download object from Minio: $objectKey" }
            return false
        }

        // Classpath에서 파일 로드
        val expectResource = ClassPathResource(expectPath)
        if (!expectResource.exists()) {
            logger.error { "Classpath resource not found: $expectResource" }
            return false
        }

        // 두 파일의 해시 비교
        val minioHash = minioResource.inputStream.use { calculateMd5Hash(it.readAllBytes()) }
        val expect = expectResource.inputStream.use { calculateMd5Hash(it.readAllBytes()) }

        logger.debug { "Expect Hash = $expect , Actual Hash = $minioHash res=${expect == minioHash}" }
        return minioHash == expect
    }

    /**
     * MD5 해시 계산
     */
    private fun calculateMd5Hash(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}