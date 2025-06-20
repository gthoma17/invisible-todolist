package com.invisiblecomputers.imagegallery.service

import com.invisiblecomputers.imagegallery.entity.OneTimeToken
import com.invisiblecomputers.imagegallery.repository.OneTimeTokenRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*

class AuthenticationServiceTest {
    
    private val oneTimeTokenRepository = mockk<OneTimeTokenRepository>()
    
    private lateinit var authenticationService: AuthenticationService
    
    @BeforeEach
    fun setUp() {
        // Using a dummy base64 public key and developer ID for testing
        authenticationService = AuthenticationService(
            oneTimeTokenRepository = oneTimeTokenRepository,
            base64PublicKey = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF0VGhOeGJJUVIrL2ZQdz09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ==",
            myDeveloperId = "test-developer-id"
        )
    }
    
    @Test
    fun `generateLoginToken should create and save new token`() {
        // Given
        val installationId = UUID.randomUUID()
        val savedToken = OneTimeToken(
            id = 1L,
            installationId = installationId,
            token = "generated-token",
            expirationTime = LocalDateTime.now().plusMinutes(10)
        )
        
        justRun { oneTimeTokenRepository.deleteByInstallationId(installationId) }
        every { oneTimeTokenRepository.save(any()) } returns savedToken
        
        // When
        val result = authenticationService.generateLoginToken(installationId)
        
        // Then
        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        verify { oneTimeTokenRepository.deleteByInstallationId(installationId) }
        verify { oneTimeTokenRepository.save(any()) }
    }
    
    @Test
    fun `authenticateLoginToken should return installation ID for valid token`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "valid-token"
        val oneTimeToken = OneTimeToken(
            id = 1L,
            installationId = installationId,
            token = loginToken,
            expirationTime = LocalDateTime.now().plusMinutes(5)
        )
        
        every { oneTimeTokenRepository.findByToken(loginToken) } returns oneTimeToken
        justRun { oneTimeTokenRepository.deleteByInstallationId(installationId) }
        
        // When
        val result = authenticationService.authenticateLoginToken(loginToken)
        
        // Then
        assertThat(result).isEqualTo(installationId)
        verify { oneTimeTokenRepository.deleteByInstallationId(installationId) }
    }
    
    @Test
    fun `authenticateLoginToken should throw exception for null token`() {
        // When & Then
        assertThatThrownBy {
            authenticationService.authenticateLoginToken(null)
        }
            .isInstanceOf(ResponseStatusException::class.java)
            .hasMessageContaining("No login token")
    }
    
    @Test
    fun `authenticateLoginToken should throw exception for non-existent token`() {
        // Given
        val loginToken = "non-existent-token"
        
        every { oneTimeTokenRepository.findByToken(loginToken) } returns null
        
        // When & Then
        assertThatThrownBy {
            authenticationService.authenticateLoginToken(loginToken)
        }
            .isInstanceOf(ResponseStatusException::class.java)
            .hasMessageContaining("Token does not exist")
    }
    
    @Test
    fun `authenticateLoginToken should throw exception for expired token`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "expired-token"
        val expiredToken = OneTimeToken(
            id = 1L,
            installationId = installationId,
            token = loginToken,
            expirationTime = LocalDateTime.now().minusMinutes(5)
        )
        
        every { oneTimeTokenRepository.findByToken(loginToken) } returns expiredToken
        
        // When & Then
        assertThatThrownBy {
            authenticationService.authenticateLoginToken(loginToken)
        }
            .isInstanceOf(ResponseStatusException::class.java)
            .hasMessageContaining("Token expired")
    }
    
    @Test
    fun `authenticateJWT should throw exception for null authorization header`() {
        // When & Then
        assertThatThrownBy {
            authenticationService.authenticateJWT(null)
        }
            .isInstanceOf(ResponseStatusException::class.java)
            .hasMessageContaining("No http auth header")
    }
}