// src/main/kotlin/com/programacionMovil/demo/controller/LineaController.kt
package com.programacionMovil.demo.controller

import com.programacionMovil.demo.entity.Linea
import com.programacionMovil.demo.repository.*
import com.programacionMovil.demo.util.GeoUtils
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/lineas")
class LineaController(
    private val lineaRepository: LineaRepository,
    private val rutaRepository: RutaRepository,
    private val coordenadaRutaRepository: CoordenadaRutaRepository,
    private val rutaCoordenadasRepository: RutaCoordenadasRepository
) {

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

    // ─────────────────────────────────────────────
    //   BUSCAR LÍNEAS POR COORDENADA (RADIO 1 KM)
    //   URL Formato: /api/lineas/rutas/{lat$lon}
    // ─────────────────────────────────────────────
    @GetMapping("/rutas/{coord}")
    @Transactional(readOnly = true)
    fun getLineasPorCoordenada(@PathVariable coord: String): List<Linea> {
        val decoded = URLDecoder.decode(coord, StandardCharsets.UTF_8.name())
        val partes = decoded.split("$")
        if (partes.size != 2) return emptyList()

        fun normalizeNumber(s: String): Double? =
            s.trim().replace(" ", "").replace(",", ".").toDoubleOrNull()

        val latUser = normalizeNumber(partes[0]) ?: return emptyList()
        val lonUser = normalizeNumber(partes[1]) ?: return emptyList()

        val rutas = rutaRepository.findAll()

        val lineasEncontradas = rutas.filter { ruta ->
            ruta.coordenadas.any { c ->
                val coordStr = c.coordenada ?: return@any false

                // limpiar parentesis
                val cleaned = coordStr.replace("(", "").replace(")", "")

                val parts = cleaned.split(",")

                if (parts.size != 2) return@any false

                // BD = (lon, lat)
                val lonDb = normalizeNumber(parts[0]) ?: return@any false
                val latDb = normalizeNumber(parts[1]) ?: return@any false

                val distKm = GeoUtils.distanceKm(
                    latUser, lonUser,
                    latDb, lonDb
                )

                distKm <= 1.0
            }


        }.mapNotNull { ruta ->
            val linea = ruta.linea
            // 🔥 FORZAMOS LA INICIALIZACIÓN LAZY
            linea.rutas.size
            linea.paradas.size
            linea
        }.distinctBy { it.idLinea }

        return lineasEncontradas
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) = lineaRepository.deleteById(id)

    @GetMapping("/rutas/dual/{coord1}/{coord2}")
    @Transactional(readOnly = true)
    fun getLineasPorDosCoordenadas(
        @PathVariable coord1: String,
        @PathVariable coord2: String
    ): List<Linea> {
        fun decodeAndSplit(s: String): Pair<Double, Double>? {
            val decoded = URLDecoder.decode(s, StandardCharsets.UTF_8.name())
            val parts = decoded.split("$")
            if (parts.size != 2) return null
            fun norm(u: String): Double? = u.trim().replace(" ", "").replace(",", ".").toDoubleOrNull()
            val lat = norm(parts[0]) ?: return null
            val lon = norm(parts[1]) ?: return null
            return Pair(lat, lon)
        }

        val p1 = decodeAndSplit(coord1) ?: return emptyList()
        val p2 = decodeAndSplit(coord2) ?: return emptyList()
        val lat1 = p1.first; val lon1 = p1.second
        val lat2 = p2.first; val lon2 = p2.second

        // carga todas las rutas (puedes optimizar con consultas específicas si la tabla es grande)
        val allRutas = rutaRepository.findAll()

        // Helper: obtener coordenadas de una ruta usando la tabla intermedia
        fun coordenadasDeRuta(rutaId: Int): List<com.programacionMovil.demo.entity.CoordenadaRuta> {
            val mappings = rutaCoordenadasRepository.findByIdRuta(rutaId)
            val ids = mappings.map { it.idCoordenada }
            if (ids.isEmpty()) return emptyList()
            // usa findAllById (JpaRepository) o findAllByIdCoordenadaIn si añadiste ese método
            return coordenadaRutaRepository.findAllById(ids)
        }

        // 1) Rutas que pasan el primer punto (a <= 1km)
        val rutasQuePasanP1 = allRutas.filter { ruta ->
            val coords = coordenadasDeRuta(ruta.idRuta)
            coords.any { c ->
                val coordStr = c.coordenada ?: return@any false
                val cleaned = coordStr.replace("(", "").replace(")", "")
                val parts = cleaned.split(",")
                if (parts.size != 2) return@any false
                val lonDb = parts[0].trim().replace(",", ".").toDoubleOrNull() ?: return@any false
                val latDb = parts[1].trim().replace(",", ".").toDoubleOrNull() ?: return@any false
                val distKm = GeoUtils.distanceKm(lat1, lon1, latDb, lonDb)
                distKm <= 1.0
            }
        }

        // 2) Sacamos las lineas de esas rutas (únicas)
        val lineasCandidatas = rutasQuePasanP1.map { it.linea }.distinctBy { it.idLinea }

        // 3) Para cada linea candidata, comprobamos sus rutas:
        // si alguna de las rutas de la linea tiene UNA coordenada a <= 1km del segundo punto -> pasa
        val resultado = lineasCandidatas.filter { linea ->
            // obtener rutas de esta linea (desde allRutas)
            val rutasDeLinea = allRutas.filter { r ->
                // r.linea puede ser proxy; comparamos por id (asegúrate que linea.idLinea no sea nullable)
                try {
                    r.linea.idLinea == linea.idLinea
                } catch (e: Exception) {
                    false
                }
            }
            rutasDeLinea.any { ruta ->
                val coords = coordenadasDeRuta(ruta.idRuta)
                coords.any { c ->
                    val coordStr = c.coordenada ?: return@any false
                    val cleaned = coordStr.replace("(", "").replace(")", "")
                    val parts = cleaned.split(",")
                    if (parts.size != 2) return@any false
                    val lonDb = parts[0].trim().replace(",", ".").toDoubleOrNull() ?: return@any false
                    val latDb = parts[1].trim().replace(",", ".").toDoubleOrNull() ?: return@any false
                    val distKm = GeoUtils.distanceKm(lat2, lon2, latDb, lonDb)
                    distKm <= 1.0
                }
            }
        }.map { linea ->
            // FORZAR inicialización lazy para evitar proxies en la serialización
            linea.rutas.size
            linea.paradas.size
            linea
        }

        return resultado.distinctBy { it.idLinea }
    }

}
