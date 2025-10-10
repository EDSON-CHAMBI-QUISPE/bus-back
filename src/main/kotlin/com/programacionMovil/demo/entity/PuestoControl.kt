package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime
import java.time.Duration

@Entity
@Table(name = "puesto_control")
data class PuestoControl(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idPc: Int = 0,

        @Column(name = "nombre_puesto")
        val nombrePuesto: String? = null,

        @Column(name = "descripcion_pc")
        val descripcionPc: String? = null,

        @Column(name = "tiempo_salida")
        val tiempoSalida: String? = null, // Guardar en segundos

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_parada", nullable = false)
        @JsonBackReference(value = "parada-puestos")
        val parada: Parada
)
