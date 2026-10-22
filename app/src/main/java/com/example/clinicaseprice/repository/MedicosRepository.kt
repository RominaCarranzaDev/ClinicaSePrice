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

    private val dias = listOf(
        lunes to "Lunes",
        martes to "Martes",
        miercoles to "Miércoles",
        jueves to "Jueves",
        viernes to "Viernes"
    )

    private fun franja(
        inicio: String,
        fin: String
    ) = FranjaHoraria(
        horaInicio = inicio,
        horaFin = fin
    )

    // Lista inicial de seis médicos.
    val medicos = listOf(

        // CLÍNICA MÉDICA

        Medico(
            id = 1,
            nombre = "Juana",
            apellido = "Gómez",
            matricula = "MN-12345",
            especialidad = "Clínica Médica",
            consultorioAsignado = Consultorio.CONSULTORIO_1,
            horarios = mapOf(
                lunes to listOf(franja("08:00", "12:00")),
                miercoles to listOf(franja("08:00", "12:00")),
                viernes to listOf(franja("08:00", "12:00"))
            ),
            usuarioId = 2
        ),

        Medico(
            id = 2,
            nombre = "Laura",
            apellido = "López",
            matricula = "MN-23456",
            especialidad = "Clínica Médica",
            consultorioAsignado = Consultorio.CONSULTORIO_2,
            horarios = mapOf(
                martes to listOf(franja("14:00", "18:00")),
                jueves to listOf(franja("14:00", "18:00")),
                viernes to listOf(franja("14:00", "18:00"))
            ),
            usuarioId = 3
        ),

        // TRAUMATOLOGÍA

        Medico(
            id = 3,
            nombre = "Carlos",
            apellido = "Rodríguez",
            matricula = "MN-34567",
            especialidad = "Traumatología",
            consultorioAsignado = Consultorio.CONSULTORIO_3,
            horarios = mapOf(
                lunes to listOf(franja("14:00", "18:00")),
                miercoles to listOf(franja("14:00", "18:00")),
                viernes to listOf(franja("08:00", "12:00"))
            ),
            usuarioId = 4
        ),

        Medico(
            id = 4,
            nombre = "María",
            apellido = "Fernández",
            matricula = "MN-45678",
            especialidad = "Traumatología",
            consultorioAsignado = Consultorio.CONSULTORIO_1,
            horarios = mapOf(
                martes to listOf(franja("08:00", "12:00")),
                jueves to listOf(franja("08:00", "12:00"))
            ),
            usuarioId = 5
        ),

        // CARDIOLOGÍA

        Medico(
            id = 5,
            nombre = "Pedro",
            apellido = "Martínez",
            matricula = "MN-56789",
            especialidad = "Cardiología",
            consultorioAsignado = Consultorio.CONSULTORIO_2,
            horarios = mapOf(
                lunes to listOf(franja("08:00", "12:00")),
                jueves to listOf(franja("08:00", "12:00")),
                viernes to listOf(franja("14:00", "18:00"))
            ),
            usuarioId = 6
        ),

        Medico(
            id = 6,
            nombre = "Ana",
            apellido = "Torres",
            matricula = "MN-67890",
            especialidad = "Cardiología",
            consultorioAsignado = Consultorio.CONSULTORIO_3,
            horarios = mapOf(
                martes to listOf(franja("14:00", "18:00")),
                miercoles to listOf(franja("08:00", "12:00")),
                jueves to listOf(franja("14:00", "18:00"))
            ),
            usuarioId = 7
        )
    )

    // Obtener todos los médicos.
    fun obtenerTodos(): List<Medico> {
        return medicos.toList()
    }

    // Buscar un médico por ID.
    fun buscarPorId(id: Int): Medico? {
        return medicos.find { it.id == id }
    }

    // Buscar un médico por ID de usuario.
    fun buscarPorIdUsuario(id: Int): Medico? {
        return medicos.find { it.usuarioId == id }
    }

    // Buscar médicos por especialidad.
    fun buscarPorEspecialidad(
        especialidad: String
    ): List<Medico> {
        return medicos.filter {
            it.especialidad.equals(
                especialidad.trim(),
                ignoreCase = true
            )
        }
    }

    // Describir los días y horarios de atención.
    fun descripcionHorarios(medico: Medico): String {
        return dias.mapNotNull { (dia, nombreDia) ->

            val franjas = medico.horarios[dia]
                ?: return@mapNotNull null

            val detalle = franjas.joinToString(" y ") {
                "${it.horaInicio} a ${it.horaFin}"
            }

            "$nombreDia: $detalle"

        }.joinToString("\n")
    }

    // Validar que la fecha y el horario sean correctos.
    fun estaDisponible(
        medico: Medico,
        fecha: String,
        horario: String
    ): Boolean {

        val formato = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.ROOT
        ).apply {
            isLenient = false
        }

        val fechaParseada = try {
            formato.parse(fecha)
        } catch (_: Exception) {
            null
        } ?: return false

        // Se aceptan horarios HH:mm.
        if (!Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
                .matches(horario)
        ) {
            return false
        }

        val calendario = Calendar.getInstance()
        calendario.time = fechaParseada

        val diaSemana = calendario.get(Calendar.DAY_OF_WEEK)
        val franjas = medico.horarios[diaSemana]
            ?: return false

        // El inicio está incluido y el final está excluido.
        return franjas.any { franja ->
            horario >= franja.horaInicio &&
                    horario < franja.horaFin
        }
    }
}
