package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.HistoriaClinica
import com.example.clinicaseprice.model.GrupoSanguineo
import com.example.clinicaseprice.model.FactorRiesgo
import com.example.clinicaseprice.model.SignoVital
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HistoriaClinicaRepository {
    private val historias = mutableListOf<HistoriaClinica>()

    init {
        repeat(10) { indice ->
            val numero = indice + 1

            historias.add(
                HistoriaClinica(
                    id = numero,
                    numeroHistoriaClinica =
                        "HC-${numero.toString().padStart(4, '0')}",
                    pacienteId = numero,
                    fechaCreacion = fechaActual(),
                    grupoSanguineo =
                        GrupoSanguineo.values()[
                            indice % GrupoSanguineo.values().size
                        ],
                    factoresRiesgo = mutableSetOf(
                        FactorRiesgo.values()[
                            indice % FactorRiesgo.values().size
                        ]
                    ),
                    signosVitales = mutableListOf(
                        SignoVital.PRESION_ARTERIAL,
                        SignoVital.FRECUENCIA_CARDIACA
                    ),
                    observacionesGenerales =
                        "Historia clínica de ejemplo",
                    anamnesis = "Anamnesis de ejemplo $numero",
                    diagnostico = "Diagnóstico de ejemplo $numero"
                )
            )
        }
    }

    // Buscar por ID
    fun buscarPorId(id: Int): HistoriaClinica? {
        return historias.find { it.id == id }
    }

    // Buscar por número de historia clínica
    fun buscarPorNumero(numero: String): HistoriaClinica? {
        return historias.find {
            it.numeroHistoriaClinica.equals(numero, ignoreCase = true)
        }
    }

    // Buscar por paciente
    fun buscarPorPaciente(pacienteId: Int): HistoriaClinica? {
        return historias.find { it.pacienteId == pacienteId }
    }

    // Crear una historia nueva
    fun crear(pacienteId: Int): HistoriaClinica {
        val nuevoId = (historias.maxOfOrNull { it.id } ?: 0) + 1

        return HistoriaClinica(
            id = nuevoId,
            numeroHistoriaClinica =
                "HC-${nuevoId.toString().padStart(4, '0')}",
            pacienteId = pacienteId,
            fechaCreacion = fechaActual()
        )
    }

    // Agregar una historia existente
    fun agregar(historia: HistoriaClinica): Boolean {
        val idExiste = historias.any { it.id == historia.id }
        val numeroExiste = historias.any {
            it.numeroHistoriaClinica == historia.numeroHistoriaClinica
        }
        val pacienteExiste = historias.any {
            it.pacienteId == historia.pacienteId
        }

        if (idExiste || numeroExiste || pacienteExiste) {
            return false
        }

        historias.add(historia)
        return true
    }

    // Crear y agregar en una sola operación
    fun crearYAgregar(pacienteId: Int): HistoriaClinica? {
        if (buscarPorPaciente(pacienteId) != null) {
            return null
        }

        val historia = crear(pacienteId)

        return if (agregar(historia)) historia else null
    }

    private fun fechaActual(): String {
        return SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())
    }
}