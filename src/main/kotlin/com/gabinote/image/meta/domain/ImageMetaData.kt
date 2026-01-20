package com.gabinote.image.meta.domain

import org.bson.types.ObjectId
import org.hibernate.validator.constraints.UniqueElements
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.Date

@Document(collection = "image_meta_data")
data class ImageMetaData(
    @Id
    val id: ObjectId? = null,
    val originName: String,

    @Indexed(unique=true)
    val convertedName: String,
    val format: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val storagePath: String,
    val uploadBy:String,
    @CreatedDate
    val uploadDate: LocalDateTime
)