package com.programacionMovil.mongo

import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Field

data class ParadaDocument(
    @Field("idParada") val idParada: Int? = null,
    @Field("nombreParada") val nombreParada: String? = null,
    @Field("tipoParada") val tipoParada: String? = null,
    @Field("estadoParada") val estadoParada: String? = null,
    @Field("ubicacion") val ubicacionRaw: String? = null,
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("ubicacionPoint") val ubicacionPoint: GeoJsonPoint? = null,
    @Field("horarios") val horarios: List<Map<String, Any>>? = null,
    @Field("puestosControl") val puestosControl: List<Map<String, Any>>? = null
)
