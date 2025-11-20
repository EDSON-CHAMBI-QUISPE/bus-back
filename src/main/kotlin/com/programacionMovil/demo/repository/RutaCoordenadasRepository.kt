// src/main/kotlin/com/programacionMovil/demo/repository/RutaCoordenadasRepository.kt
package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.RutaCoordenadas
import com.programacionMovil.demo.entity.RutaCoordenadasId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RutaCoordenadasRepository :
    JpaRepository<RutaCoordenadas, RutaCoordenadasId> {

    fun findByIdRuta(idRuta: Int): List<RutaCoordenadas>
    fun findByIdCoordenada(idCoordenada: Int): List<RutaCoordenadas>
}
