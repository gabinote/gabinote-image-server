package com.gabinote.image.testSupport.testUtil.data.img

object TestImageHelper {

    val PNG_1X1 = TestImg(
        width = 1,
        height = 1,
        format = "png",
        base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
    )

    val PNG_2X1 = TestImg(
        width = 2,
        height = 1,
        format = "png",
        base64 = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAIAAAD91JpzAAAADUlEQVR42mP8/5+hHgAHggJ/Pp6ZVQAAAABJRU5ErkJggg==",

    )

    val PNG_2X2 = TestImg(
        width = 2,
        height = 2,
        format = "png",
        base64 = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAADUlEQVR42mP8z8BQDwAFMQGAxbK09wAAAABJRU5ErkJggg==",

    )

    val PNG_1X2 = TestImg(
        width = 1,
        height = 2,
        format = "png",
        base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAADUlEQVR42mP8z8BQDwAFMQGAxbK09wAAAABJRU5ErkJggg==",

    )

    val JPG_1X1 = TestImg(
        width = 1,
        height = 1,
        format = "jpg",
        base64 = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////2wBDAf//////////////////////////////////////////////////////////////////////////////////////wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAX/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAL/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCfAAH/2Q=="
    )

    val INVALID_IMG = TestImg(
        width = 0,
        height = 0,
        format = "jpeg",
        base64 = "VGhpcyBpcyBmYWtlIGltYWdl"
    )

}