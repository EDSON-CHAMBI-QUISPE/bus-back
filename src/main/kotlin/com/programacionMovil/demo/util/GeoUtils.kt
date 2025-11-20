// src/main/kotlin/com/programacionMovil/demo/util/GeoUtils.kt
package com.programacionMovil.demo.util

import kotlin.math.*

object GeoUtils {
    private const val R = 6371.0 // Radio de la Tierra en km

    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * asin(sqrt(a))

        return R * c
    }
}
