package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.Parada
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParadaRepository : JpaRepository<Parada, Int>
