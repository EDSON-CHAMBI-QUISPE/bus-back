package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.PuestoControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PuestoControlRepository : JpaRepository<PuestoControl, Int>
