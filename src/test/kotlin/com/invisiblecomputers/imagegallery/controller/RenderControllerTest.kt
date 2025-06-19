package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.dto.DecodedJWT
import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.service.AuthenticationService
import com.invisiblecomputers.imagegallery.service.ImageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.io.ByteArrayInputStream
import java.util.*

@WebMvcTest(RenderController::class)
class RenderControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var authenticationService: AuthenticationService
    
    @MockBean
    private lateinit var appInstallationRepository: AppInstallationRepository
    
    @MockBean
    private lateinit var imageService: ImageService
    
    @Test
    fun `getRender should return image for horizontal 880x528 screen`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = false
        )
        val imageBytes = "fake-image-data".toByteArray()
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        whenever(imageService.getRandomImage(880, 528))
            .thenReturn(ByteArrayInputStream(imageBytes))
        
        // When & Then
        val result = mockMvc.perform(
            get("/api/render")
                .header("Authorization", "Bearer valid-jwt")
                .param("device-type", "BLACK_AND_WHITE_SCREEN_880X528")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andReturn()
        
        assertThat(result.response.contentAsByteArray).isEqualTo(imageBytes)
    }
    
    @Test
    fun `getRender should return image for vertical 880x528 screen`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = true
        )
        val imageBytes = "fake-image-data".toByteArray()
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        whenever(imageService.getRandomImage(528, 880))
            .thenReturn(ByteArrayInputStream(imageBytes))
        
        // When & Then
        mockMvc.perform(
            get("/api/render")
                .header("Authorization", "Bearer valid-jwt")
                .param("device-type", "BLACK_AND_WHITE_SCREEN_880X528")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
    }
    
    @Test
    fun `getRender should return image for horizontal 800x480 screen`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = false
        )
        val imageBytes = "fake-image-data".toByteArray()
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        whenever(imageService.getRandomImage(800, 480))
            .thenReturn(ByteArrayInputStream(imageBytes))
        
        // When & Then
        mockMvc.perform(
            get("/api/render")
                .header("Authorization", "Bearer valid-jwt")
                .param("device-type", "BLACK_AND_WHITE_SCREEN_800X480")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
    }
    
    @Test
    fun `getRender should create new installation if not exists`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        val newInstallation = AppInstallation(installationId = installationId)
        val imageBytes = "fake-image-data".toByteArray()
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.empty())
        whenever(appInstallationRepository.save(any<AppInstallation>()))
            .thenReturn(newInstallation)
        whenever(imageService.getRandomImage(880, 528))
            .thenReturn(ByteArrayInputStream(imageBytes))
        
        // When & Then
        mockMvc.perform(
            get("/api/render")
                .header("Authorization", "Bearer valid-jwt")
                .param("device-type", "BLACK_AND_WHITE_SCREEN_880X528")
        )
            .andExpect(status().isOk)
    }
    
    @Test
    fun `getRender should return 400 for invalid device type`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(AppInstallation(installationId = installationId)))
        
        // When & Then
        mockMvc.perform(
            get("/api/render")
                .header("Authorization", "Bearer valid-jwt")
                .param("device-type", "INVALID_DEVICE_TYPE")
        )
            .andExpect(status().isInternalServerError)
    }
}