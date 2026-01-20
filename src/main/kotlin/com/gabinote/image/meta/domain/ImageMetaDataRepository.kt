package com.gabinote.image.meta.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ImageMetaDataRepository: MongoRepository<ImageMetaData, ObjectId> {
    fun deleteAllByUploadBy(userId: String): Long
    fun countAllByUploadBy(userId: String): Long
    fun findByConvertedName(convertedName: String): ImageMetaData?
}