package com.gabinote.image.common.util.img

/**
 * 이미지 이름과 포맷을 나타내는 데이터 클래스
 *
 * @property name 이미지 이름
 * @property format 이미지 포맷 (예: jpg, png)
 */
data class ImgName(
    // 이미지 이름이때 확장자 제외한 이름
    var name: String,
    var extension: String
){
    /**
     * 전체 이미지 이름을 받아서 ImgName 객체를 생성하는 보조 생성자
     * @param fullName 전체 이미지 이름 (예: image.jpg)
     */
    constructor(fullName: String) : this(
        name = fullName.substringBeforeLast('.'),
        extension = fullName.substringAfterLast('.')
    )
    companion object {
        /**
         * ImgExtension을 사용하여 ImgName을 생성하는 팩토리 메서드
         */
        fun from(name: String, ext: ImgExtension): ImgName {
            return ImgName(
                name = name,
                extension = ext.ext().lowercase()
            )
        }
    }

    /**
     * 전체 이미지 이름을 반환
     */
    val fullName: String
        get() = "$name.${extension.lowercase()}"

    /**
     * 이미지 확장자를 ImgExtension 객체로 반환
     */
    val ext: ImgExtension
        get() = ImgExtension(extension)

    val stem: ImgStem
        get() = ImgStem(name)

    val stemStr: String
        get() = name
}