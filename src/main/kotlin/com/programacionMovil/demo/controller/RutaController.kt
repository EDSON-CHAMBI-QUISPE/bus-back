// src/main/kotlin/com/programacionMovil/demo/controller/RutaController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.Ruta
import com.programacionMovil.demo.repository.RutaRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rutas")
class RutaController(private val rutaRepository: RutaRepository) {

    @GetMapping
    fun getAll(): List<Ruta> = rutaRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): Ruta? = rutaRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody ruta: Ruta): Ruta = rutaRepository.save(ruta)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody ruta: Ruta): Ruta {
        val existing = rutaRepository.findById(id).orElseThrow()
        val updated = existing.copy(
                nombreRuta = ruta.nombreRuta,
                estadoRuta = ruta.estadoRuta
        )
        return rutaRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = rutaRepository.deleteById(id)
}
