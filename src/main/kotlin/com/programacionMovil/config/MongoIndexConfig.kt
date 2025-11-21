package com.programacionMovil.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.CommandLineRunner
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType

@Configuration
class MongoIndexConfig {

    @Bean
    fun ensureIndexes(mongoTemplate: MongoTemplate) = CommandLineRunner {
        val ops = mongoTemplate.indexOps("lineas")
        ops.ensureIndex(GeospatialIndex("rutas.coordenadas.point").typed(GeoSpatialIndexType.GEO_2DSPHERE))
        ops.ensureIndex(GeospatialIndex("paradas.ubicacionPoint").typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }
}
