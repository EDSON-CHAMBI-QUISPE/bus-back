package com.programacionMovil.mongo

import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class CoordenadaDocument(
    @Field("idCoordenada") val idCoordenada: Int? = null,
    @Field("coordenada") val raw: String? = null,
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("point") val point: GeoJsonPoint? = null
)
