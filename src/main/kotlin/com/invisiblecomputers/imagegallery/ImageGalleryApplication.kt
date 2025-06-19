package com.invisiblecomputers.imagegallery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ImageGalleryApplication

fun main(args: Array<String>) {
    runApplication<ImageGalleryApplication>(*args)
}