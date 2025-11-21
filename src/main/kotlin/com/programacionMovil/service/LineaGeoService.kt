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

    fun findLineasNearPoint(lat: Double, lon: Double, maxMeters: Double = 200.0): List<LineaDocument> {

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
        maxMeters: Double = 200.0
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
}
