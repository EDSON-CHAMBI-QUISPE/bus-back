// src/main/kotlin/com/programacionMovil/demo/controller/CoordenadasRutaController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.CoordenadaRuta
import com.programacionMovil.demo.repository.CoordenadaRutaRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coordenadas")
class CoordenadaRutaController(private val coordenadasRutaRepository: CoordenadaRutaRepository) {

    @GetMapping
    fun getAll(): List<CoordenadaRuta> = coordenadasRutaRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): CoordenadaRuta? = coordenadasRutaRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody coordenada: CoordenadaRuta): CoordenadaRuta = coordenadasRutaRepository.save(coordenada)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody coordenada: CoordenadaRuta): CoordenadaRuta {
        val existing = coordenadasRutaRepository.findById(id).orElseThrow()
        val updated = existing.copy(coordenada = coordenada.coordenada)
        return coordenadasRutaRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = coordenadasRutaRepository.deleteById(id)
}
