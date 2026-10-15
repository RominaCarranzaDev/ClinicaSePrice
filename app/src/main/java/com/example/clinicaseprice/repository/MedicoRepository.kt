package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Consultorio
import com.example.clinicaseprice.model.FranjaHoraria
import com.example.clinicaseprice.model.Medico
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MedicosRepository {

    private val lunes = Calendar.MONDAY
    private val martes = Calendar.TUESDAY
    private val miercoles = Calendar.WEDNESDAY
    private val jueves = Calendar.THURSDAY
    private val viernes = Calendar.FRIDAY

    val medicos = listOf(
        Medico(
            id = 1,
            nombre = "Dr. Gómez",
            consultorioAsignado = Consultorio.CONSULTORIO_1,
            horarios = mapOf(
                lunes to listOf(FranjaHoraria(8, 12)),
                miercoles to listOf(FranjaHoraria(8, 12))
            )
        ),

        Medico(
            id = 2,
            nombre = "Dra. López",
            consultorioAsignado = Consultorio.CONSULTORIO_2,
            horarios = mapOf(
                martes to listOf(FranjaHoraria(14, 18)),
                jueves to listOf(FranjaHoraria(14, 18))
            )
        ),

        Medico(
            id = 3,
            nombre = "Dr. Rodríguez",
            consultorioAsignado = Consultorio.CONSULTORIO_3,
            horarios = mapOf(
                lunes to listOf(FranjaHoraria(14, 18)),
                martes to listOf(FranjaHoraria(8, 12)),
                miercoles to listOf(FranjaHoraria(14, 18)),
                jueves to listOf(FranjaHoraria(8, 12)),
                viernes to listOf(FranjaHoraria(8, 18))
            )
        )
    )

    fun buscarPorId(id: Int): Medico? {
        return medicos.find { it.id == id }
    }

    fun descripcionHorarios(medico: Medico): String {
        val dias = listOf(
            lunes to "Lunes",
            martes to "Martes",
            miercoles to "Miércoles",
            jueves to "Jueves",
            viernes to "Viernes"
        )

        return dias.mapNotNull { (dia, nombreDia) ->
            val franjas = medico.horarios[dia]
                ?: return@mapNotNull null

            val detalle = franjas.joinToString(" y ") {
                "${"%02d".format(it.horaInicio)}:00 a " +
                        "${"%02d".format(it.horaFin)}:00"
            }

            "$nombreDia: $detalle"
        }.joinToString("\n")
    }

    fun estaDisponible(
        medico: Medico,
        fecha: String,
        horario: String
    ): Boolean {

        val formato = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {
            isLenient = false
        }

        val fechaParseada = try {
            formato.parse(fecha)
        } catch (_: Exception) {
            null
        } ?: return false

        val horaValida = Regex("^([01][0-9]|2[0-3]):00$")
        if (!horaValida.matches(horario)) return false

        val hora = horario.substringBefore(":").toInt()

        val calendario = Calendar.getInstance()
        calendario.time = fechaParseada

        val diaSemana = calendario.get(Calendar.DAY_OF_WEEK)
        val franjas = medico.horarios[diaSemana] ?: return false

        return franjas.any { franja ->
            hora >= franja.horaInicio &&
                    hora < franja.horaFin
        }
    }
}
