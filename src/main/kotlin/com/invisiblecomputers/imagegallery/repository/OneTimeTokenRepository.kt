package com.invisiblecomputers.imagegallery.repository

import com.invisiblecomputers.imagegallery.entity.OneTimeToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface OneTimeTokenRepository : JpaRepository<OneTimeToken, Long> {
    fun findByToken(token: String): OneTimeToken?
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OneTimeToken o WHERE o.installationId = :installationId")
    fun deleteByInstallationId(installationId: UUID)
}