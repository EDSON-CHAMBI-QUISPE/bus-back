// src/main/kotlin/com/programacionMovil/demo/entity/RutaCoordenadas.kt
package com.programacionMovil.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "ruta_coordenadas")
@IdClass(RutaCoordenadasId::class)
data class RutaCoordenadas(

    @Id
    @Column(name = "id_ruta")
    val idRuta: Int = 0,

    @Id
    @Column(name = "id_coordenada")
    val idCoordenada: Int = 0
)
