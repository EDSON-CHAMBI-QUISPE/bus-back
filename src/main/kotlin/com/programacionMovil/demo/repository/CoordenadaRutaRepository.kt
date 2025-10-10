package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.CoordenadaRuta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CoordenadaRutaRepository : JpaRepository<CoordenadaRuta, Int>
