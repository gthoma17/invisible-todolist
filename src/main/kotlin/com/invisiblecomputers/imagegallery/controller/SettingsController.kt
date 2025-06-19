package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.view.RedirectView
import java.util.*

@Controller
class SettingsController(
    private val appInstallationRepository: AppInstallationRepository
) {
    
    @GetMapping("/settings")
    fun getSettings(request: HttpServletRequest, model: Model): String {
        val installationId = getInstallationIdFromSession(request)
        val installation = appInstallationRepository.findById(installationId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Installation not found") }
        
        model.addAttribute("isVerticallyOriented", installation.isVerticallyOriented)
        return "settings"
    }
    
    @PostMapping("/settings")
    fun updateSettings(
        @RequestParam("orientation") orientation: String,
        request: HttpServletRequest
    ): RedirectView {
        val installationId = getInstallationIdFromSession(request)
        val installation = appInstallationRepository.findById(installationId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Installation not found") }
        
        val isVerticallyOriented = orientation == "vertical"
        val updatedInstallation = installation.copy(isVerticallyOriented = isVerticallyOriented)
        appInstallationRepository.save(updatedInstallation)
        
        return RedirectView("/settings")
    }
    
    private fun getInstallationIdFromSession(request: HttpServletRequest): UUID {
        val installationIdStr = request.session.getAttribute("installation-id") as? String
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session authentication failed")
        
        return try {
            UUID.fromString(installationIdStr)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid installation ID in session")
        }
    }
}