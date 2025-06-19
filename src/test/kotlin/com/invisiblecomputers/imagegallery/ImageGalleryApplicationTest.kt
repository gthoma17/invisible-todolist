package com.invisiblecomputers.imagegallery

import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.entity.OneTimeToken
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.repository.OneTimeTokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ImageGalleryApplicationTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var appInstallationRepository: AppInstallationRepository
    
    @Autowired
    private lateinit var oneTimeTokenRepository: OneTimeTokenRepository
    
    @Test
    fun `application context loads`() {
        // This test verifies that the Spring Boot application context loads successfully
    }
    
    @Test
    fun `settings page integration test`() {
        // Given
        val installationId = UUID.randomUUID()
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = false
        )
        appInstallationRepository.save(installation)
        
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        // When - Get settings page
        mockMvc.perform(
            get("/settings")
                .session(session)
        )
            .andExpect(status().isOk)
            .andExpect(view().name("settings"))
            .andExpect(model().attribute("isVerticallyOriented", false))
        
        // When - Update settings
        mockMvc.perform(
            post("/settings")
                .param("orientation", "vertical")
                .session(session)
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
        
        // Then - Verify the change was persisted
        val updatedInstallation = appInstallationRepository.findById(installationId).get()
        assertThat(updatedInstallation.isVerticallyOriented).isTrue()
    }
    
    @Test
    fun `login flow integration test`() {
        // Given
        val installationId = UUID.randomUUID()
        val loginToken = "test-login-token"
        val oneTimeToken = OneTimeToken(
            installationId = installationId,
            token = loginToken,
            expirationTime = LocalDateTime.now().plusMinutes(10)
        )
        oneTimeTokenRepository.save(oneTimeToken)
        
        // When - Login with valid token
        val result = mockMvc.perform(
            get("/api/login")
                .param("login-token", loginToken)
                .param("device-type", "BLACK_AND_WHITE_SCREEN_880X528")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
            .andReturn()
        
        // Then - Verify installation was created and session was set
        val session = result.request.session!!
        assertThat(session.getAttribute("installation-id")).isEqualTo(installationId.toString())
        
        val installation = appInstallationRepository.findById(installationId)
        assertThat(installation).isPresent
        
        // Verify token was deleted after use
        val tokenAfterLogin = oneTimeTokenRepository.findByToken(loginToken)
        assertThat(tokenAfterLogin).isNull()
    }
}