// src/main/kotlin/com/programacionMovil/demo/controller/ParadaController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.Parada
import com.programacionMovil.demo.repository.ParadaRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/paradas")
class ParadaController(private val paradaRepository: ParadaRepository) {

    @GetMapping
    fun getAll(): List<Parada> = paradaRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): Parada? = paradaRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody parada: Parada): Parada = paradaRepository.save(parada)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody parada: Parada): Parada {
        val existing = paradaRepository.findById(id).orElseThrow()
        val updated = existing.copy(
                nombreParada = parada.nombreParada,
                tipoParada = parada.tipoParada,
                estadoParada = parada.estadoParada,
                ubicacion = parada.ubicacion
        )
        return paradaRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = paradaRepository.deleteById(id)
}
