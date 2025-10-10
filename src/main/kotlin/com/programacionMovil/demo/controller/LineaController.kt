// src/main/kotlin/com/programacionMovil/demo/controller/LineaController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.Linea
import com.programacionMovil.demo.repository.LineaRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lineas")
class LineaController(private val lineaRepository: LineaRepository) {

    @GetMapping
    fun getAll(): List<Linea> = lineaRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): Linea? = lineaRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody linea: Linea): Linea = lineaRepository.save(linea)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody linea: Linea): Linea {
        val existing = lineaRepository.findById(id).orElseThrow()
        val updated = existing.copy(
                nombreLinea = linea.nombreLinea,
                descripcion = linea.descripcion
        )
        return lineaRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = lineaRepository.deleteById(id)
}
