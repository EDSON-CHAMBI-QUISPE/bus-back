package com.programacionMovil.service

import com.programacionMovil.mongo.LineaDocument
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import com.programacionMovil.mongo.RutaDocument
import com.programacionMovil.mongo.ParadaDocument
import com.programacionMovil.mongo.CoordenadaDocument


@Service
class LineaGeoService(private val mongoTemplate: MongoTemplate) {

    fun findAllLineas(): List<LineaDocument> =
        mongoTemplate.findAll(LineaDocument::class.java)
    // Archivo: /mnt/data/LineaGeoService.kt
    fun findLineaById(idLinea: Int): LineaDocument? {
        val query = Query(Criteria.where("idLinea").`is`(idLinea))
        return try {
            mongoTemplate.findOne(query, LineaDocument::class.java)
        } catch (e: Exception) {
            println("⚠️ findLineaById error: ${e.message}")
            null
        }
    }

    fun findLineasNearPoint(lat: Double, lon: Double, maxMeters: Double = 1000.0): List<LineaDocument> {

        // Construcción manual de $geoNear — 100% compatible
        val geoNearDoc = Document(
            "\$geoNear",
            Document()
                .append("near", Document("type", "Point")
                    .append("coordinates", listOf(lon, lat)))
                .append("distanceField", "dist")
                .append("spherical", true)
                .append("maxDistance", maxMeters)
                .append("key", "rutas.coordenadas.point")
        )

        val geoOp = AggregationOperation {
                _: AggregationOperationContext -> geoNearDoc
        }

        val agg = Aggregation.newAggregation(geoOp)

        return try {
            mongoTemplate.aggregate(agg, "lineas", LineaDocument::class.java)
                .mappedResults
                .distinctBy { it.idLinea }
        } catch (e: Exception) {
            println("⚠️ GeoNear error: ${e.message}")
            emptyList()
        }
    }

    fun findLineasNearBoth(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        maxMeters: Double = 1000.0
    ): List<LineaDocument> {

        val ids1 = idsNear(lat1, lon1, maxMeters)
        val ids2 = idsNear(lat2, lon2, maxMeters)
        val ids = ids1.intersect(ids2)

        if (ids.isEmpty()) return emptyList()

        val query = Query(Criteria.where("idLinea").`in`(ids.toList()))
        return mongoTemplate.find(query, LineaDocument::class.java)
    }

    private fun idsNear(lat: Double, lon: Double, maxMeters: Double): Set<Int> {

        val geoNearDoc = Document(
            "\$geoNear",
            Document()
                .append("near", Document("type", "Point")
                    .append("coordinates", listOf(lon, lat)))
                .append("distanceField", "dist")
                .append("spherical", true)
                .append("maxDistance", maxMeters)
                .append("key", "rutas.coordenadas.point")
        )

        val geoOp = AggregationOperation {
                _: AggregationOperationContext -> geoNearDoc
        }

        val proj = Aggregation.project("idLinea")
        val agg = Aggregation.newAggregation(geoOp, proj)

        return try {
            mongoTemplate.aggregate(agg, "lineas", Document::class.java)
                .mappedResults
                .mapNotNull {
                    val v = it["idLinea"]
                    when (v) {
                        is Number -> v.toInt()
                        is String -> v.toIntOrNull()
                        else -> null
                    }
                }.toSet()
        } catch (e: Exception) {
            println("⚠️ idsNear error: ${e.message}")
            emptySet()
        }
    }

    // Devuelve todas las rutas encontradas en la colección 'lineas'
    fun findAllRutas(): List<Map<String, Any?>> {
        val lineas = try {
            mongoTemplate.findAll(LineaDocument::class.java)
        } catch (e: Exception) {
            println("⚠️ findAllRutas error: ${e.message}")
            return emptyList()
        }

        val rutas: List<RutaDocument> = lineas.flatMap { it.rutas ?: emptyList() }

        return rutas.map { r ->
            mapOf(
                "idRuta" to r.idRuta,
                "nombreRuta" to r.nombreRuta,
                "estadoRuta" to r.estadoRuta,
                "coordenadas" to (r.coordenadas?.map { c ->
                    mapOf(
                        "idCoordenada" to c.idCoordenada,
                        "coordenada" to (c.raw ?: "(${c.point?.x},${c.point?.y})")
                    )
                } ?: emptyList<Map<String, Any?>>())
            )
        }
    }

    // Devuelve todas las paradas (sin duplicados por idParada)
    fun findAllParadas(): List<Map<String, Any?>> {
        val lineas = try {
            mongoTemplate.findAll(LineaDocument::class.java)
        } catch (e: Exception) {
            println("⚠️ findAllParadas error: ${e.message}")
            return emptyList()
        }

        val paradas: List<ParadaDocument> = lineas
            .flatMap { it.paradas ?: emptyList() }
            .distinctBy { it.idParada }

        return paradas.map { p ->
            mapOf(
                "idParada" to p.idParada,
                "nombreParada" to p.nombreParada,
                "tipoParada" to p.tipoParada,
                "estadoParada" to p.estadoParada,
                "ubicacion" to p.ubicacionRaw,
                "horarios" to (p.horarios ?: emptyList<Map<String, Any>>()),
                "puestosControl" to (p.puestosControl ?: emptyList<Map<String, Any>>())
            )
        }
    }

    // Devuelve todas las coordenadas (lista plana)
    fun findAllCoordenadas(): List<Map<String, Any?>> {
        val lineas = try {
            mongoTemplate.findAll(LineaDocument::class.java)
        } catch (e: Exception) {
            println("⚠️ findAllCoordenadas error: ${e.message}")
            return emptyList()
        }

        val coords: List<CoordenadaDocument> = lineas
            .flatMap { it.rutas ?: emptyList() }
            .flatMap { it.coordenadas ?: emptyList() }

        return coords.map { c ->
            mapOf(
                "idCoordenada" to c.idCoordenada,
                "coordenada" to (c.raw ?: "(${c.point?.x},${c.point?.y})")
            )
        }
    }

    // Devuelve todos los horarios (lista plana)
    fun findAllHorarios(): List<Map<String, Any?>> {
        val lineas = try {
            mongoTemplate.findAll(LineaDocument::class.java)
        } catch (e: Exception) {
            println("⚠️ findAllHorarios error: ${e.message}")
            return emptyList()
        }

        // p.horarios ya es List<Map<String, Any>>
        val horarios: List<Map<String, Any>> = lineas
            .flatMap { it.paradas ?: emptyList() }
            .flatMap { it.horarios ?: emptyList() }

        // lo levantamos a Any? para coincidir con List<Map<String, Any?>>
        return horarios.map { h -> h as Map<String, Any?> }
    }

}
