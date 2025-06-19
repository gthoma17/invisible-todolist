package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.dto.LoginTokenResponse
import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.service.AuthenticationService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.util.*

@RestController
@RequestMapping("/api")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val appInstallationRepository: AppInstallationRepository
) {
    
    @GetMapping("/get-login-token")
    fun getLoginToken(@RequestHeader("Authorization") authorizationHeader: String): ResponseEntity<LoginTokenResponse> {
        val decodedJWT = authenticationService.authenticateJWT(authorizationHeader)
        val loginToken = authenticationService.generateLoginToken(decodedJWT.installationId)
        
        return ResponseEntity.ok(LoginTokenResponse(loginToken))
    }
    
    @GetMapping("/login")
    fun login(
        @RequestParam("login-token") loginToken: String,
        @RequestParam("device-type") deviceType: String,
        request: HttpServletRequest
    ): RedirectView {
        val installationId = authenticationService.authenticateLoginToken(loginToken)
        
        // Create or get installation
        if (!appInstallationRepository.existsById(installationId)) {
            appInstallationRepository.save(AppInstallation(installationId = installationId))
        }
        
        // Store installation ID in session
        request.session.setAttribute("installation-id", installationId.toString())
        
        return RedirectView("/settings")
    }
}