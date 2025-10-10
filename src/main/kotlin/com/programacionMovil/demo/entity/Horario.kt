package com.programacionMovil.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.OffsetTime

@Entity
@Table(name = "horario")
data class Horario(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val idHorario: Int = 0,

        @Column(name = "hora_inicio")
        val horaInicio: OffsetTime? = null,

        @Column(name = "hora_fin")
        val horaFin: OffsetTime? = null,

        @Column(name = "dia")
        val dia: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_parada", nullable = false)
        @JsonBackReference(value = "parada-horarios")
        val parada: Parada
)
