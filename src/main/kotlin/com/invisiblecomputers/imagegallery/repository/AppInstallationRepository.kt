package com.invisiblecomputers.imagegallery.repository

import com.invisiblecomputers.imagegallery.entity.AppInstallation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppInstallationRepository : JpaRepository<AppInstallation, UUID>