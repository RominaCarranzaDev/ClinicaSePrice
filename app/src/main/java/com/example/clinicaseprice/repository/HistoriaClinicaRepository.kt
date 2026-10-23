package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.AtencionMedica
import com.example.clinicaseprice.model.FactorRiesgo
import com.example.clinicaseprice.model.GrupoSanguineo
import com.example.clinicaseprice.model.HistoriaClinica
import com.example.clinicaseprice.model.OrdenMedicaEstudio
import com.example.clinicaseprice.model.PrescripcionMedica
import com.example.clinicaseprice.model.SignosVitales
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HistoriaClinicaRepository {
    private val historias = mutableListOf<HistoriaClinica>()

    // historias clínicas iniciales
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

                    observacionesGenerales =
                        "Historia clínica de ejemplo",

                    atenciones = mutableListOf(
                        AtencionMedica(
                            id = 1,
                            fecha = fechaActual(),
                            medicoId = 1,
                            anamnesis = "Anamnesis de ejemplo $numero",
                            diagnostico = "Diagnóstico de ejemplo $numero"
                        )
                    )
                )
            )
        }
    }

    // Buscar historia clínica por ID
    fun buscarPorId(id: Int): HistoriaClinica? {
        return historias.find { it.id == id }
    }

    // Buscar historia clínica por número
    fun buscarPorNroHC(numero: String): HistoriaClinica? {
        return historias.find {
            it.numeroHistoriaClinica.equals(numero.trim(), ignoreCase = true)
        }
    }

    // Buscar historia clínica por paciente
    fun buscarPorPaciente(pacienteId: Int?): HistoriaClinica? {
        return historias.find { it.pacienteId == pacienteId }
    }

    // Crear una historia clínica nueva, sin agregarla al repositorio
    fun crear(pacienteId: Int?): HistoriaClinica {
        val nuevoId = (historias.maxOfOrNull { it.id } ?: 0) + 1

        return HistoriaClinica(
            id = nuevoId,
            numeroHistoriaClinica =
                "HC-${nuevoId.toString().padStart(4, '0')}",
            pacienteId = pacienteId,
            fechaCreacion = fechaActual()
        )
    }

    // Agregar una historia clínica existente
    fun agregar(historia: HistoriaClinica): Boolean {
        val idExiste = historias.any { it.id == historia.id }

        val numeroExiste = historias.any {
            it.numeroHistoriaClinica.equals(
                historia.numeroHistoriaClinica,
                ignoreCase = true
            )
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

    // Crear una historia solamente si el paciente no tiene una
    fun crearYAgregar(pacienteId: Int?): HistoriaClinica? {
        val historiaExistente = buscarPorPaciente(pacienteId)

        if (historiaExistente != null) {
            return historiaExistente
        }

        val nuevaHistoria = crear(pacienteId)

        return if (agregar(nuevaHistoria)) {
            nuevaHistoria
        } else {
            buscarPorPaciente(pacienteId)
        }
    }

    // Registrar una atención en la historia clínica del paciente
    // Si no tiene historia, la crea automáticamente
    fun registrarAtencion(
        pacienteId: Int?,
        medicoId: Int?,
        anamnesis: String,
        diagnostico: String,
        observaciones: String = "",
        signosVitales: SignosVitales? = null,
        prescripciones: List<PrescripcionMedica> = emptyList(),
        estudios: List<OrdenMedicaEstudio> = emptyList()
    ): AtencionMedica {

        val historia = buscarPorPaciente(pacienteId)
            ?: crearYAgregar(pacienteId)
            ?: throw IllegalStateException(
                "No se pudo crear la historia clínica del paciente"
            )

        val nuevoId = (historia.atenciones.maxOfOrNull { it.id } ?: 0) + 1

        val atencion = AtencionMedica(
            id = nuevoId,
            fecha = fechaActual(),
            medicoId = medicoId,
            anamnesis = anamnesis,
            diagnostico = diagnostico,
            observaciones = observaciones,
            signosVitales = signosVitales,
            factoresRiesgo = historia.factoresRiesgo.toSet(),
            prescripciones = prescripciones.toList(),
            estudios = estudios.toList()
        )
        historia.atenciones.add(atencion)

        return atencion
    }

    // Obtener todas las atenciones de la HC de un paciente
    fun obtenerAtencionesPorPaciente(
        pacienteId: Int
    ): List<AtencionMedica> {
        return buscarPorPaciente(pacienteId)
            ?.atenciones
            ?.toList()
            ?: emptyList()
    }

    private fun fechaActual(): String {
        return SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())
    }
}
