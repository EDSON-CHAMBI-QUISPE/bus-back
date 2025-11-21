package com.programacionMovil.config

import com.mongodb.MongoCommandException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MongoConfig(private val mongoTemplate: MongoTemplate) {

    private val log = LoggerFactory.getLogger(MongoConfig::class.java)

    /**
     * Al arrancar la app nos aseguramos de que exista un índice 2dsphere sobre
     * 'rutas.coordenadas.point'. Si ya existe algo parecido lo respetamos y no
     * lanzamos excepción que detenga Spring.
     */
    @EventListener(ApplicationReadyEvent::class)
    fun ensureGeoIndex() {
        val collectionName = "lineas"
        val coll: MongoCollection<Document> = mongoTemplate.getCollection(collectionName)

        // Recolectar índices existentes
        val existingIndexes = coll.listIndexes().into(mutableListOf<Document>())

        // Buscar si ya existe un índice que referencia rutas.coordenadas.point
        val found = existingIndexes.any { idx ->
            val key = idx.get("key")
            if (key is Document) {
                // chequea si la key contiene exactamente el path o alguna forma anidada
                val value = key.get("rutas.coordenadas.point")
                (value != null)
            } else false
        }

        if (found) {
            log.info("Índice geoespacial para '$collectionName.rutas.coordenadas.point' ya existe. No se creará uno nuevo.")
            return
        }

        // Si no existe ninguno, intentar crear. Atrapar conflicto para no romper arranque.
        val options = IndexOptions().name("rutas_coordenadas_point_2dsphere")
        try {
            log.info("No se encontró índice geoespacial en '$collectionName.rutas.coordenadas.point'. Se creará uno.")
            coll.createIndex(Indexes.geo2dsphere("rutas.coordenadas.point"), options)
            log.info("Índice 2dsphere creado: rutas_coordenadas_point_2dsphere")
        } catch (e: MongoCommandException) {
            // Código 85 = IndexOptionsConflict -> índice con distinto nombre / opciones ya existe
            log.warn("No se pudo crear índice geoespacial (posible conflicto): ${e.errorMessage}. Se continúa sin fallar el arranque.")
        } catch (t: Throwable) {
            log.error("Error inesperado al crear índice geoespacial: ${t.message}", t)
        }
    }
}
