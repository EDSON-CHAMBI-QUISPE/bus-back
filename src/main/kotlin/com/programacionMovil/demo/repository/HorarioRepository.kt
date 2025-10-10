package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.Horario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HorarioRepository : JpaRepository<Horario, Int>
