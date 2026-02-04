package com.gabinote.image.imgProxy.mapping

import com.gabinote.image.imgProxy.dto.controller.ImgProxyUrlReqControllerDto
import com.gabinote.image.imgProxy.dto.service.ImgProxyUrlReqServiceDto
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
)
interface ImgProxyMapper {
    fun toServiceDto(dto: ImgProxyUrlReqControllerDto) : ImgProxyUrlReqServiceDto
}