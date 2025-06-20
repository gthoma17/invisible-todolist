package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.dto.DecodedJWT
import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.service.AuthenticationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(controllers = [AuthController::class], excludeAutoConfiguration = [SecurityAutoConfiguration::class])
class AuthControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var authenticationService: AuthenticationService
    
    @MockkBean
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
        
        every { authenticationService.authenticateJWT("Bearer valid-jwt") } returns decodedJWT
        every { authenticationService.generateLoginToken(installationId) } returns loginToken
        
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
        every { authenticationService.authenticateJWT("Bearer invalid-jwt") } throws ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT")
        
        // When & Then
        mockMvc.perform(
            get("/api/get-login-token")
                .header("Authorization", "Bearer invalid-jwt")
        )
            .andExpect(status().isUnauthorized)
    }
    
    @Test
    fun `login should redirect to settings when valid login token provided`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "valid-login-token"
        val deviceType = "BLACK_AND_WHITE_SCREEN_880X528"
        
        every { authenticationService.authenticateLoginToken(loginToken) } returns installationId
        every { appInstallationRepository.existsById(installationId) } returns false
        every { appInstallationRepository.save(any()) } returns AppInstallation(installationId = installationId)
        
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
        
        every { authenticationService.authenticateLoginToken(loginToken) } returns installationId
        every { appInstallationRepository.existsById(installationId) } returns true
        
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