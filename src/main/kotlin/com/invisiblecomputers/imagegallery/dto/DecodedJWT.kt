package com.invisiblecomputers.imagegallery.dto

import java.util.*

data class DecodedJWT(
    val userId: UUID,
    val deviceId: UUID,
    val installationId: UUID
)