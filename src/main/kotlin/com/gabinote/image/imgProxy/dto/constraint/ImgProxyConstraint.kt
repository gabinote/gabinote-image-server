package com.gabinote.image.imgProxy.dto.constraint

object ImgProxyConstraint {
    // 이미지 프록시 파일 이름 최대 길이
    // 기본적으로 uuid로 생성하기에 36자이지만, 확장자 등을 고려 여유를 둠
    const val MAX_FILE_NAME_LENGTH = 50

    // 이미지 요청 포맷 최대 길이
    // jpg, png, webp 등 일반적인 확장자 길이를 고려
    const val REQUEST_FORMAT_MAX_LENGTH = 10
}