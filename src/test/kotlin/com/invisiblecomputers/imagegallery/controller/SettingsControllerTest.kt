package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(SettingsController::class)
class SettingsControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var appInstallationRepository: AppInstallationRepository
    
    @Test
    fun `getSettings should return settings page with installation data`() {
        // Given
        val installationId = UUID.randomUUID()
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = true
        )
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        
        // When & Then
        mockMvc.perform(
            get("/settings")
                .session(session)
        )
            .andExpect(status().isOk)
            .andExpect(view().name("settings"))
            .andExpect(model().attribute("isVerticallyOriented", true))
    }
    
    @Test
    fun `getSettings should return 401 when no session`() {
        // When & Then
        mockMvc.perform(get("/settings"))
            .andExpect(status().isUnauthorized)
    }
    
    @Test
    fun `getSettings should return 404 when installation not found`() {
        // Given
        val installationId = UUID.randomUUID()
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.empty())
        
        // When & Then
        mockMvc.perform(
            get("/settings")
                .session(session)
        )
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `updateSettings should update orientation to vertical`() {
        // Given
        val installationId = UUID.randomUUID()
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = false
        )
        val updatedInstallation = installation.copy(isVerticallyOriented = true)
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        whenever(appInstallationRepository.save(any<AppInstallation>()))
            .thenReturn(updatedInstallation)
        
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "vertical")
                .session(session)
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
        
        // Verify the installation was updated
        verify(appInstallationRepository).save(updatedInstallation)
    }
    
    @Test
    fun `updateSettings should update orientation to horizontal`() {
        // Given
        val installationId = UUID.randomUUID()
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = true
        )
        val updatedInstallation = installation.copy(isVerticallyOriented = false)
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        whenever(appInstallationRepository.findById(installationId))
            .thenReturn(Optional.of(installation))
        whenever(appInstallationRepository.save(any<AppInstallation>()))
            .thenReturn(updatedInstallation)
        
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "horizontal")
                .session(session)
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
        
        // Verify the installation was updated
        verify(appInstallationRepository).save(updatedInstallation)
    }
    
    @Test
    fun `updateSettings should return 401 when no session`() {
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "vertical")
        )
            .andExpect(status().isUnauthorized)
    }
}