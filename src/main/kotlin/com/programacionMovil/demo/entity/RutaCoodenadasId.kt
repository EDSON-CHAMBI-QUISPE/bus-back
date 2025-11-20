// src/main/kotlin/com/programacionMovil/demo/entity/RutaCoordenadasId.kt
package com.programacionMovil.demo.entity

import java.io.Serializable

data class RutaCoordenadasId(
    val idRuta: Int = 0,
    val idCoordenada: Int = 0
) : Serializable
