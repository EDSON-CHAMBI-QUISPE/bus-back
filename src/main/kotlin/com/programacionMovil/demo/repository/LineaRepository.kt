package com.programacionMovil.demo.repository

import com.programacionMovil.demo.entity.Linea
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LineaRepository : JpaRepository<Linea, Int> {

    @EntityGraph(
            attributePaths = [
                "rutas",
                "rutas.coordenadas",
                "paradas",
                "paradas.horarios",
                "paradas.puestosControl"
            ]
    )
    @Query("SELECT l FROM Linea l WHERE l.id = :id")
    fun findByIdWithRelations(@Param("id") id: Int): Linea?
}
