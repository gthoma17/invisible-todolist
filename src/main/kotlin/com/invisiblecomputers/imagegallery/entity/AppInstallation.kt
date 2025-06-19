package com.invisiblecomputers.imagegallery.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "app_installation")
data class AppInstallation(
    @Id
    @Column(name = "installation_id")
    val installationId: UUID,
    
    @Column(name = "is_vertically_oriented", nullable = false)
    val isVerticallyOriented: Boolean = false
)