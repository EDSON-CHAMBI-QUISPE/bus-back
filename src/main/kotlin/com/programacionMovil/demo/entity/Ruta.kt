package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime
@Entity
@Table(name = "ruta")
data class Ruta(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idRuta: Int = 0,

        @Column(name = "nombre_ruta")
        val nombreRuta: String? = null,

        @Column(name = "estado_ruta")
        val estadoRuta: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_linea", nullable = false)
        @JsonBackReference
        val linea: Linea,

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
                name = "ruta_coordenadas",
                joinColumns = [JoinColumn(name = "id_ruta")],
                inverseJoinColumns = [JoinColumn(name = "id_coordenada")]
        )
        @OrderBy("idCoordenada ASC")
        val coordenadas: MutableList<CoordenadaRuta> = mutableListOf()
)
