package com.invisiblecomputers.imagegallery.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.io.InputStream

class ImageServiceTest {
    
    private val restTemplate = mockk<RestTemplate>()
    
    private lateinit var imageService: ImageService
    
    @BeforeEach
    fun setUp() {
        imageService = ImageService()
        // We need to use reflection to inject the mock RestTemplate
        val field = ImageService::class.java.getDeclaredField("restTemplate")
        field.isAccessible = true
        field.set(imageService, restTemplate)
    }
    
    @Test
    fun `getRandomImage should return input stream for valid dimensions`() {
        // Given
        val width = 800
        val height = 600
        val imageBytes = "fake-image-data".toByteArray()
        
        every {
            restTemplate.getForObject(
                "https://picsum.photos/$width/$height/",
                ByteArray::class.java
            )
        } returns imageBytes
        
        // When
        val result: InputStream = imageService.getRandomImage(width, height)
        
        // Then
        assertThat(result.readAllBytes()).isEqualTo(imageBytes)
    }
    
    @Test
    fun `getRandomImage should throw exception when API returns null`() {
        // Given
        val width = 800
        val height = 600
        
        every {
            restTemplate.getForObject(
                "https://picsum.photos/$width/$height/",
                ByteArray::class.java
            )
        } returns null
        
        // When & Then
        assertThatThrownBy {
            imageService.getRandomImage(width, height)
        }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Failed to fetch image")
    }
}