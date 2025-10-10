package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.Ruta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RutaRepository : JpaRepository<Ruta, Int>
