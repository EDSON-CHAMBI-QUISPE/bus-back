package com.programacionMovil.controller

import com.programacionMovil.mapper.LineaMapper
import com.programacionMovil.service.LineaGeoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lineas")
class LineaController(private val lineaGeoService: LineaGeoService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Map<String, Any?>>> {
        val docs = lineaGeoService.findAllLineas()
        val out = docs.map { LineaMapper.toApi(it) }
        return ResponseEntity.ok(out)
    }

    @GetMapping("/rutas/{latlon}")
    fun getNear(@PathVariable latlon: String, @RequestParam(required = false) maxMeters: Int?): ResponseEntity<List<Map<String, Any?>>> {
        val parts = latlon.split("$")
        val lat = parts[0].toDouble()
        val lon = parts[1].toDouble()
        val docs = lineaGeoService.findLineasNearPoint(lat, lon,  1000.0)
        return ResponseEntity.ok(docs.map { LineaMapper.toApi(it) })
    }

    @GetMapping("/rutas/dual/{p1}/{p2}")
    fun getDual(@PathVariable p1: String, @PathVariable p2: String, @RequestParam(required = false) maxMeters: Int?): ResponseEntity<List<Map<String, Any?>>> {
        val pa = p1.split("$"); val pb = p2.split("$")
        val lat1 = pa[0].toDouble(); val lon1 = pa[1].toDouble()
        val lat2 = pb[0].toDouble(); val lon2 = pb[1].toDouble()
        val docs = lineaGeoService.findLineasNearBoth(lat1, lon1, lat2, lon2, 1000.0)
        return ResponseEntity.ok(docs.map { LineaMapper.toApi(it) })
    }
    @GetMapping("/{idLinea}")
    fun getLineaPorId(@PathVariable idLinea: Int): ResponseEntity<Any> {
        val linea = lineaGeoService.findLineaById(idLinea)

        return if (linea != null) {
            ResponseEntity.ok(LineaMapper.toApi(linea))
        } else {
            ResponseEntity.notFound().build()
        }
    }

}