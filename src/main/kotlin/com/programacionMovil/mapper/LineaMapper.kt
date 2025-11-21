package com.programacionMovil.mapper

import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import kotlin.reflect.full.memberProperties

/**
 * Mapper robusto que:
 * - No asume propiedades concretas de las clases de documento.
 * - Usa reflexión para buscar propiedades por nombre.
 * - Devuelve exactamente los campos que espera el frontend:
 *   "idLinea","nombreLinea","descripcion","rutas","paradas"...
 * - Para coordenadas/ubicaciones prioriza devolver el "raw" string
 *   (ej. "(-66.151185,-17.397801)"). Si no existe, lo construye desde
 *   un GeoJsonPoint (point).
 */
object LineaMapper {

    fun toApi(lineaDoc: Any?): Map<String, Any?> {
        if (lineaDoc == null) return emptyMap()

        fun prop(obj: Any?, name: String): Any? {
            if (obj == null) return null
            // si es Map, toma por clave
            if (obj is Map<*, *>) return obj[name]
            // reflexión dinámica
            return obj::class.memberProperties.firstOrNull { it.name == name }?.getter?.call(obj)
        }

        fun listProp(obj: Any?, name: String): List<Any?> {
            val v = prop(obj, name)
            return when (v) {
                is List<*> -> v
                is Array<*> -> v.toList()
                null -> emptyList()
                else -> listOf(v)
            }
        }

        // obtiene listas de rutas/paradas sin asumir tipo
        val rutasRaw = listProp(lineaDoc, "rutas")
        val paradasRaw = listProp(lineaDoc, "paradas")

        val rutas = rutasRaw.map { r -> mapRutaToApi(r, ::prop) }
        val paradas = paradasRaw.map { p -> mapParadaToApi(p, ::prop) }

        return mapOf(
            "idLinea" to (prop(lineaDoc, "idLinea") ?: prop(lineaDoc, "id_linea") ?: prop(lineaDoc, "id") ),
            "nombreLinea" to (prop(lineaDoc, "nombreLinea") ?: prop(lineaDoc, "nombre_linea") ?: prop(lineaDoc, "nombre")),
            "descripcion" to prop(lineaDoc, "descripcion"),
            "rutas" to rutas,
            "paradas" to paradas
        )
    }

    private fun mapRutaToApi(rutaObj: Any?, prop: (Any?, String) -> Any?): Map<String, Any?> {
        val coordenadasRaw = when (val lr = prop(rutaObj, "coordenadas") ?: prop(rutaObj, "coordenadasList")) {
            is List<*> -> lr
            is Array<*> -> lr.toList()
            null -> emptyList<Any?>()
            else -> listOf(lr)
        }

        val coordenadas = coordenadasRaw.map { c ->
            mapOf(
                "idCoordenada" to (prop(c, "idCoordenada") ?: prop(c, "id_coordenada") ?: prop(c, "id") ?: prop(c, "idCoordenadaSql")),
                "coordenada" to coordinateToRaw(c, prop)
            )
        }

        return mapOf(
            "idRuta" to (prop(rutaObj, "idRuta") ?: prop(rutaObj, "id_ruta") ?: prop(rutaObj, "id")),
            "nombreRuta" to (prop(rutaObj, "nombreRuta") ?: prop(rutaObj, "nombre_ruta") ?: prop(rutaObj, "nombre")),
            "estadoRuta" to (prop(rutaObj, "estadoRuta") ?: prop(rutaObj, "estado_ruta") ?: prop(rutaObj, "estado")),
            "coordenadas" to coordenadas
        )
    }

    private fun mapParadaToApi(paradaObj: Any?, prop: (Any?, String) -> Any?): Map<String, Any?> {
        val horariosRaw = when (val hr = prop(paradaObj, "horarios")) {
            is List<*> -> hr
            is Array<*> -> hr.toList()
            null -> emptyList<Any?>()
            else -> listOf(hr)
        }

        val horarios = horariosRaw.map { h ->
            mapOf(
                "idHorario" to (prop(h, "idHorario") ?: prop(h, "id_horario") ?: prop(h, "id")),
                "horaInicio" to (prop(h, "horaInicio") ?: prop(h, "hora_inicio") ?: prop(h, "horaInicioStr") ?: prop(h, "horaInicioStr")),
                "horaFin" to (prop(h, "horaFin") ?: prop(h, "hora_fin") ?: prop(h, "horaFinStr")),
                "dia" to (prop(h, "dia") ?: prop(h, "dias") ?: prop(h, "day"))
            )
        }

        val puestosRaw = when (val pr = prop(paradaObj, "puestosControl") ?: prop(paradaObj, "puestos_control")) {
            is List<*> -> pr
            is Array<*> -> pr.toList()
            null -> emptyList<Any?>()
            else -> listOf(pr)
        }

        val puestosControl = puestosRaw.map { pc ->
            mapOf(
                "idPc" to (prop(pc, "idPc") ?: prop(pc, "id_pc") ?: prop(pc, "id")),
                "nombrePuesto" to (prop(pc, "nombrePuesto") ?: prop(pc, "nombre_puesto") ?: prop(pc, "nombre")),
                "descripcionPc" to (prop(pc, "descripcionPc") ?: prop(pc, "descripcion") ?: prop(pc, "descripcion_pc")),
                "tiempoSalida" to (prop(pc, "tiempoSalida") ?: prop(pc, "tiempo_salida") ?: prop(pc, "tiempo"))
            )
        }

        return mapOf(
            "idParada" to (prop(paradaObj, "idParada") ?: prop(paradaObj, "id_parada") ?: prop(paradaObj, "id")),
            "nombreParada" to (prop(paradaObj, "nombreParada") ?: prop(paradaObj, "nombre_parada") ?: prop(paradaObj, "nombre")),
            "tipoParada" to (prop(paradaObj, "tipoParada") ?: prop(paradaObj, "tipo_parada") ?: prop(paradaObj, "tipo")),
            "estadoParada" to (prop(paradaObj, "estadoParada") ?: prop(paradaObj, "estado_parada") ?: prop(paradaObj, "estado")),
            "ubicacion" to paradaUbicacionRaw(paradaObj, prop),
            "horarios" to horarios,
            "puestosControl" to puestosControl
        )
    }

    private fun coordinateToRaw(coordObj: Any?, prop: (Any?, String) -> Any?): String {
        // intenta raw string en varios nombres
        val candidates = listOfNotNull(
            prop(coordObj, "raw") as? String,
            prop(coordObj, "coordenada") as? String,
            prop(coordObj, "coordenadaRaw") as? String,
            prop(coordObj, "coordenada_str") as? String,
            prop(coordObj, "ubicacion") as? String
        )
        if (candidates.isNotEmpty() && candidates.first().isNotBlank()) return candidates.first()

        // intenta point (GeoJsonPoint u objeto con x,y)
        val point = prop(coordObj, "point") ?: prop(coordObj, "location") ?: prop(coordObj, "geo")
        when (point) {
            is GeoJsonPoint -> {
                val lon = point.x
                val lat = point.y
                return "($lon,$lat)"
            }
            is Map<*, *> -> {
                val lon = (point["x"] ?: point["lon"] ?: point["lng"] ?: point["longitude"])
                val lat = (point["y"] ?: point["lat"] ?: point["latitude"])
                if (lon != null && lat != null) return "($lon,$lat)"
            }
        }

        // intenta campos separados
        val lon = prop(coordObj, "lon") ?: prop(coordObj, "lng") ?: prop(coordObj, "longitude")
        val lat = prop(coordObj, "lat") ?: prop(coordObj, "latitude")
        if (lon != null && lat != null) return "($lon,$lat)"

        return ""
    }

    private fun paradaUbicacionRaw(paradaObj: Any?, prop: (Any?, String) -> Any?): String {
        // prioriza string raw
        val rawCandidates = listOfNotNull(
            prop(paradaObj, "ubicacion") as? String,
            prop(paradaObj, "ubicacionRaw") as? String,
            prop(paradaObj, "ubicacion_str") as? String,
            prop(paradaObj, "location") as? String
        )
        if (rawCandidates.isNotEmpty() && rawCandidates.first().isNotBlank()) return rawCandidates.first()

        // si ubicacion es objeto, intenta coord -> construir
        val ubicObj = prop(paradaObj, "ubicacion") ?: prop(paradaObj, "location") ?: prop(paradaObj, "point")
        val fromPoint = coordinateToRaw(ubicObj, prop)
        if (fromPoint.isNotBlank()) return fromPoint

        return ""
    }
}
