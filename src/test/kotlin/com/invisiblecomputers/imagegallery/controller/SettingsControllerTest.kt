package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import java.util.*
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.context.support.WithMockUser

@WebMvcTest(SettingsController::class)
class SettingsControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockkBean
    private lateinit var appInstallationRepository: AppInstallationRepository
    
    @Test
    @WithMockUser
    fun `getSettings should return settings page with installation data`() {
        // Given
        val installationId = UUID.randomUUID()
        val installation = AppInstallation(
            installationId = installationId,
            isVerticallyOriented = true
        )
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        every { appInstallationRepository.findById(installationId) } returns Optional.of(installation)
        
        // When & Then
        val result = mockMvc.perform(
            get("/settings")
                .session(session)
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andReturn()
        
        // Verify the model and view name without rendering
        val modelAndView = result.modelAndView
        assertThat(modelAndView?.viewName).isEqualTo("settings")
        assertThat(modelAndView?.model?.get("isVerticallyOriented")).isEqualTo(true)
    }
    
    @Test
    fun `getSettings should return 401 when no session`() {
        // When & Then
        mockMvc.perform(get("/settings"))
            .andExpect(status().isUnauthorized)
    }
    
    @Test
    @WithMockUser
    fun `getSettings should return 404 when installation not found`() {
        // Given
        val installationId = UUID.randomUUID()
        val session = MockHttpSession()
        session.setAttribute("installation-id", installationId.toString())
        
        every { appInstallationRepository.findById(installationId) } returns Optional.empty()
        
        // When & Then
        mockMvc.perform(
            get("/settings")
                .session(session)
                .with(csrf())
        )
            .andExpect(status().isNotFound)
    }
    
    @Test
    @WithMockUser
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
        
        every { appInstallationRepository.findById(installationId) } returns Optional.of(installation)
        every { appInstallationRepository.save(any()) } returns updatedInstallation
        
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "vertical")
                .session(session)
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
        
        // Verify the installation was updated
        verify { appInstallationRepository.save(updatedInstallation) }
    }
    
    @Test
    @WithMockUser
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
        
        every { appInstallationRepository.findById(installationId) } returns Optional.of(installation)
        every { appInstallationRepository.save(any()) } returns updatedInstallation
        
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "horizontal")
                .session(session)
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/settings"))
        
        // Verify the installation was updated
        verify { appInstallationRepository.save(updatedInstallation) }
    }
    
    @Test
    @WithMockUser
    fun `updateSettings should return 401 when no session`() {
        // When & Then
        mockMvc.perform(
            post("/settings")
                .param("orientation", "vertical")
                .with(csrf())
        )
            .andExpect(status().isUnauthorized)
    }
}