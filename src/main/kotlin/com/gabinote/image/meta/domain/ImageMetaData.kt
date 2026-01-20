package com.gabinote.image.meta.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "image_meta_data")
data class ImageMetaData(
    @Id
    var id: ObjectId? = null,
    val originName: String,
    @CreatedDate
    var uploadDate: LocalDateTime? = null,

    @Indexed(unique=true)
    var convertedName: String,
    var format: String,
    var size: Long,
    var width: Int,
    var height: Int,
    var storagePath: String,
    var uploadBy:String,
)