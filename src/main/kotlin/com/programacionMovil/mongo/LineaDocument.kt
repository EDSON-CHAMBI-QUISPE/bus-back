package com.programacionMovil.mongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("lineas")
data class LineaDocument(
    @Id val id: String? = null,
    @Field("idLinea") val idLinea: Int? = null,
    @Field("nombreLinea") val nombreLinea: String? = null,
    @Field("descripcion") val descripcion: String? = null,
    @Field("rutas") val rutas: List<RutaDocument>? = null,
    @Field("paradas") val paradas: List<ParadaDocument>? = null
)

data class RutaDocument(
    @Field("idRuta") val idRuta: Int? = null,
    @Field("nombreRuta") val nombreRuta: String? = null,
    @Field("estadoRuta") val estadoRuta: String? = null,
    @Field("coordenadas") val coordenadas: List<CoordenadaDocument>? = null
)

data class CoordenadaDoc(
    val idCoordenadaSql: Int?,
    val raw: String?,             // ex: "(-66.147717,-17.397324)"
    val point: GeoPoint? = null   // { type: "Point", coordinates: [lon, lat] } usado internamente
)

data class ParadaDoc(
    val idParada: Int?,
    val nombreParada: String?,
    val tipoParada: String?,
    val estadoParada: String?,
    val ubicacion: UbicacionDoc? = null,
    val horarios: List<HorarioDoc> = emptyList(),
    val puestosControl: List<PuestoControlDoc> = emptyList()
)

data class UbicacionDoc(
    val raw: String?,
    val point: GeoPoint? = null
)

data class HorarioDoc(
    val idHorario: Int?,
    val horaInicio: String?,  // conserva formato "HH:mm:ss"
    val horaFin: String?,
    val dia: String?
)

data class PuestoControlDoc(
    val idPc: Int?,
    val nombrePuesto: String?,
    val descripcionPc: String?,
    val tiempoSalida: String?
)

data class GeoPoint(
    val type: String = "Point",
    val coordinates: List<Double> = emptyList() // [lon, lat]
)
