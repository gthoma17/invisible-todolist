package com.invisiblecomputers.imagegallery.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "one_time_token")
data class OneTimeToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(name = "installation_id", unique = true, nullable = false)
    val installationId: UUID,
    
    @Column(name = "expiration_time", nullable = false)
    val expirationTime: LocalDateTime,
    
    @Column(name = "token", length = 100, nullable = false)
    val token: String
)