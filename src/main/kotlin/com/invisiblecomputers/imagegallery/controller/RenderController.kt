package com.invisiblecomputers.imagegallery.controller

import com.invisiblecomputers.imagegallery.entity.AppInstallation
import com.invisiblecomputers.imagegallery.repository.AppInstallationRepository
import com.invisiblecomputers.imagegallery.service.AuthenticationService
import com.invisiblecomputers.imagegallery.service.ImageService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class RenderController(
    private val authenticationService: AuthenticationService,
    private val appInstallationRepository: AppInstallationRepository,
    private val imageService: ImageService
) {
    
    @GetMapping("/render")
    fun getRender(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestParam("device-type") deviceType: String
    ): ResponseEntity<ByteArray> {
        val decodedJWT = authenticationService.authenticateJWT(authorizationHeader)
        
        // Get or create installation
        val installation = appInstallationRepository.findById(decodedJWT.installationId)
            .orElseGet {
                val newInstallation = AppInstallation(installationId = decodedJWT.installationId)
                appInstallationRepository.save(newInstallation)
            }
        
        val (width, height) = when (deviceType) {
            "BLACK_AND_WHITE_SCREEN_880X528" -> {
                if (installation.isVerticallyOriented) 528 to 880 else 880 to 528
            }
            "BLACK_AND_WHITE_SCREEN_800X480" -> {
                if (installation.isVerticallyOriented) 480 to 800 else 800 to 480
            }
            else -> throw IllegalArgumentException("Invalid device type: $deviceType")
        }
        
        val imageStream = imageService.getRandomImage(width, height)
        val imageBytes = imageStream.readAllBytes()
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(imageBytes)
    }
}