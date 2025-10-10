// src/main/kotlin/com/programacionMovil/demo/controller/PuestoControlController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.PuestoControl
import com.programacionMovil.demo.repository.PuestoControlRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/puestos")
class PuestoControlController(private val puestoControlRepository: PuestoControlRepository) {

    @GetMapping
    fun getAll(): List<PuestoControl> = puestoControlRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): PuestoControl? = puestoControlRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody puesto: PuestoControl): PuestoControl = puestoControlRepository.save(puesto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody puesto: PuestoControl): PuestoControl {
        val existing = puestoControlRepository.findById(id).orElseThrow()
        val updated = existing.copy(
                nombrePuesto = puesto.nombrePuesto,
                descripcionPc = puesto.descripcionPc,
                tiempoSalida = puesto.tiempoSalida
        )
        return puestoControlRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = puestoControlRepository.deleteById(id)
}
