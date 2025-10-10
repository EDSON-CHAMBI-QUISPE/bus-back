package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime
@Entity
@Table(name = "coordenadas_ruta")
data class CoordenadaRuta(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idCoordenada: Int = 0,

        @Column(name = "coordenada")
        val coordenada: String? = null, // "x,y"

        @ManyToMany(mappedBy = "coordenadas", fetch = FetchType.LAZY)
        @JsonBackReference
        val rutas: MutableList<Ruta> = mutableListOf()
)
