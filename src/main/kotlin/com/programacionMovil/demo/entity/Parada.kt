package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime
@Entity
@Table(name = "parada")
data class Parada(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idParada: Int = 0,

        @Column(name = "nombre_parada")
        val nombreParada: String? = null,

        @Column(name = "tipo_parada")
        val tipoParada: String? = null,

        @Column(name = "estado_parada")
        val estadoParada: String? = null,

        @Column(name = "ubicacion")
        val ubicacion: String? = null, // "x,y"

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_linea", nullable = false)
        @JsonBackReference
        val linea: Linea,

        @OneToMany(mappedBy = "parada", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JsonManagedReference(value = "parada-horarios")
        val horarios: List<Horario> = mutableListOf(),

        @OneToMany(mappedBy = "parada", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JsonManagedReference(value = "parada-puestos")
        val puestosControl: List<PuestoControl> = mutableListOf()
)
