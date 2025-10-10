package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime

@Entity
@Table(name = "linea")
data class Linea(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idLinea: Int = 0,

        @Column(name = "nombre_linea", nullable = false)
        val nombreLinea: String,

        @Column(name = "descripcion")
        val descripcion: String? = null,

        @OneToMany(mappedBy = "linea", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JsonManagedReference
        val rutas: MutableList<Ruta> = mutableListOf(),

        @OneToMany(mappedBy = "linea", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JsonManagedReference
        val paradas: MutableList<Parada> = mutableListOf()
)
