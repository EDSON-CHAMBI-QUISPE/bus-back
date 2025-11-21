package com.programacionMovil.controller

import com.programacionMovil.service.LineaGeoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CatalogController(private val lineaGeoService: LineaGeoService) {

    @GetMapping("/rutas")
    fun getAllRutas(): ResponseEntity<List<Map<String, Any?>>> {
        val rutas = lineaGeoService.findAllRutas()
        return ResponseEntity.ok(rutas)
    }

    @GetMapping("/paradas")
    fun getAllParadas(): ResponseEntity<List<Map<String, Any?>>> {
        val paradas = lineaGeoService.findAllParadas()
        return ResponseEntity.ok(paradas)
    }

    @GetMapping("/coordenadas")
    fun getAllCoordenadas(): ResponseEntity<List<Map<String, Any?>>> {
        val coords = lineaGeoService.findAllCoordenadas()
        return ResponseEntity.ok(coords)
    }

    @GetMapping("/horarios")
    fun getAllHorarios(): ResponseEntity<List<Map<String, Any?>>> {
        val horarios = lineaGeoService.findAllHorarios()
        return ResponseEntity.ok(horarios)
    }
}
