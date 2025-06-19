package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.dto.DecodedJWT
import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.service.AuthenticationService
import com.fasterxml.jackson.databind.ObjectMapper
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
import java.util.*

@WebMvcTest(AuthController::class)
class AuthControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var authenticationService: AuthenticationService
    
    @MockBean
    private lateinit var appInstallationRepository: AppInstallationRepository
    
    @Test
    fun `getLoginToken should return login token when valid JWT provided`() {
        // Given
        val installationId = UUID.randomUUID()
        val decodedJWT = DecodedJWT(
            userId = UUID.randomUUID(),
            deviceId = UUID.randomUUID(),
            installationId = installationId
        )
        val loginToken = "test-login-token"
        
        whenever(authenticationService.authenticateJWT("Bearer valid-jwt"))
            .thenReturn(decodedJWT)
        whenever(authenticationService.generateLoginToken(installationId))
            .thenReturn(loginToken)
        
        // When & Then
        mockMvc.perform(
            get("/api/get-login-token")
                .header("Authorization", "Bearer valid-jwt")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.loginToken").value(loginToken))
    }
    
    @Test
    fun `getLoginToken should return 401 when invalid JWT provided`() {
        // Given
        whenever(authenticationService.authenticateJWT("Bearer invalid-jwt"))
            .thenThrow(RuntimeException("Invalid JWT"))
        
        // When & Then
        mockMvc.perform(
            get("/api/get-login-token")
                .header("Authorization", "Bearer invalid-jwt")
        )
            .andExpect(status().isInternalServerError)
    }
    
    @Test
    fun `login should redirect to settings when valid login token provided`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "valid-login-token"
        val deviceType = "BLACK_AND_WHITE_SCREEN_880X528"
        
        whenever(authenticationService.authenticateLoginToken(loginToken))
            .thenReturn(installationId)
        whenever(appInstallationRepository.existsById(installationId))
            .thenReturn(false)
        whenever(appInstallationRepository.save(any<AppInstallation>()))
            .thenReturn(AppInstallation(installationId = installationId))
        
        // When & Then
        val result = mockMvc.perform(
            get("/api/login")
                .param("login-token", loginToken)
                .param("device-type", deviceType)
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
            .andReturn()
        
        // Verify session contains installation ID
        val session = result.request.session!!
        assertThat(session.getAttribute("installation-id")).isEqualTo(installationId.toString())
    }
    
    @Test
    fun `login should handle existing installation`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "valid-login-token"
        val deviceType = "BLACK_AND_WHITE_SCREEN_880X528"
        
        whenever(authenticationService.authenticateLoginToken(loginToken))
            .thenReturn(installationId)
        whenever(appInstallationRepository.existsById(installationId))
            .thenReturn(true)
        
        // When & Then
        mockMvc.perform(
            get("/api/login")
                .param("login-token", loginToken)
                .param("device-type", deviceType)
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
    }
}