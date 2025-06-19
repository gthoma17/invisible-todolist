package com.invisiblecomputers.imagegallery.service

import com.invisiblecomputers.imagegallery.dto.DecodedJWT
import com.invisiblecomputers.imagegallery.entity.OneTimeToken
import com.invisiblecomputers.imagegallery.repository.OneTimeTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Service
class AuthenticationService(
    private val oneTimeTokenRepository: OneTimeTokenRepository,
    @Value("\${app.jwt.public-key}") private val base64PublicKey: String,
    @Value("\${app.developer-id}") private val myDeveloperId: String
) {
    
    private val publicKey by lazy {
        val keyBytes = Base64.getDecoder().decode(base64PublicKey)
        val spec = X509EncodedKeySpec(keyBytes)
        KeyFactory.getInstance("RSA").generatePublic(spec)
    }
    
    fun authenticateJWT(authorizationHeader: String?): DecodedJWT {
        if (authorizationHeader == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No http auth header")
        }
        
        try {
            val claims: Claims = Jwts.parser()
                .verifyWith(publicKey as RSAPublicKey)
                .build()
                .parseSignedClaims(authorizationHeader)
                .payload
            
            val developerId = claims["developer_id"] as? String
            if (developerId != myDeveloperId) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid developer id")
            }
            
            return DecodedJWT(
                userId = UUID.fromString(claims["user_id"] as String),
                deviceId = UUID.fromString(claims["device_id"] as String),
                installationId = UUID.fromString(claims["installation_id"] as String)
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed: ${e.message}")
        }
    }
    
    fun generateLoginToken(installationId: UUID): String {
        val token = generateSecureToken()
        
        // Delete existing tokens for this installation
        oneTimeTokenRepository.deleteByInstallationId(installationId)
        
        // Create new token
        val oneTimeToken = OneTimeToken(
            installationId = installationId,
            token = token,
            expirationTime = LocalDateTime.now().plusMinutes(10)
        )
        
        oneTimeTokenRepository.save(oneTimeToken)
        return token
    }
    
    fun authenticateLoginToken(loginToken: String?): UUID {
        if (loginToken == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No login token")
        }
        
        val oneTimeToken = oneTimeTokenRepository.findByToken(loginToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token does not exist")
        
        if (oneTimeToken.expirationTime.isBefore(LocalDateTime.now())) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired")
        }
        
        val installationId = oneTimeToken.installationId
        
        // Delete all tokens for this installation after successful authentication
        oneTimeTokenRepository.deleteByInstallationId(installationId)
        
        return installationId
    }
    
    private fun generateSecureToken(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
        return (1..50)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}