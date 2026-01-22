package com.gabinote.image.imgProxy.dto.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.image.imgProxy.dto.constraint.ImgProxyConstraint.MAX_FILE_NAME_LENGTH
import com.gabinote.image.imgProxy.dto.constraint.ImgProxyConstraint.REQUEST_FORMAT_MAX_LENGTH
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class ImgProxyUrlReqControllerDto(

    @field:NotBlank("fileName must not be blank")
    @field:Length(max = MAX_FILE_NAME_LENGTH, message = "fileName must have length less than $MAX_FILE_NAME_LENGTH")
    val fileName: String,


    @field:NotBlank("requestFormat must not be blank")
    @field:Length(max = REQUEST_FORMAT_MAX_LENGTH, message = "requestFormat must have length less than $REQUEST_FORMAT_MAX_LENGTH")
    val requestFormat: String,

    @field:Min(0, message = "Width must be greater than or equal to 0")
    val width: Int,


    @field:Min(0, message = "Height must be greater than or equal to 0")
    val height: Int,

    val enlarge: Boolean
) {

}