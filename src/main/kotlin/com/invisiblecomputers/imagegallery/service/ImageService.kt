package com.invisiblecomputers.imagegallery.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class ImageService {
    
    private val restTemplate = RestTemplate()
    
    fun getRandomImage(width: Int, height: Int): InputStream {
        val imageBytes = restTemplate.getForObject(
            "https://picsum.photos/$width/$height/",
            ByteArray::class.java
        ) ?: throw RuntimeException("Failed to fetch image")
        
        return ByteArrayInputStream(imageBytes)
    }
}