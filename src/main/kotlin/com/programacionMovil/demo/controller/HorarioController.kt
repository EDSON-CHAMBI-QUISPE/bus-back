// src/main/kotlin/com/programacionMovil/demo/controller/HorarioController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.Horario
import com.programacionMovil.demo.repository.HorarioRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/horarios")
class HorarioController(private val horarioRepository: HorarioRepository) {

    @GetMapping
    fun getAll(): List<Horario> = horarioRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): Horario? = horarioRepository.findById(id).orElse(null)

    @PostMapping
    fun create(@RequestBody horario: Horario): Horario = horarioRepository.save(horario)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody horario: Horario): Horario {
        val existing = horarioRepository.findById(id).orElseThrow()
        val updated = existing.copy(
                horaInicio = horario.horaInicio,
                horaFin = horario.horaFin,
                dia = horario.dia
        )
        return horarioRepository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = horarioRepository.deleteById(id)
}
