package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Turno
import java.text.SimpleDateFormat
import java.util.Locale

object TurnosRepository {

    val turnos = mutableListOf<Turno>()

    private var siguienteId = 1

    private fun normalizarDni(dni: String): String {
        return dni.filter { it.isDigit() }
    }

    // Sincroniza los datos visibles con los repositorios centrales.
    private fun sincronizarDatos() {
        turnos.forEach { turno ->

            // Recupera al paciente por ID.
            var paciente = if (turno.pacienteId > 0) {
                PacientesRepository.buscarPorId(turno.pacienteId)
            } else {
                null
            }

            // Compatibilidad con turnos antiguos sin pacienteId.
            if (paciente == null) {
                paciente = PacientesRepository.buscarPorDni(
                    turno.dniPaciente
                )

                if (paciente != null) {
                    turno.pacienteId = paciente.id
                }
            }

            if (paciente != null) {
                turno.nombrePaciente = paciente.nombreCompleto
                turno.dniPaciente = paciente.dni
            }

            // Actualiza los datos del profesional.
            val medico = MedicosRepository.buscarPorId(turno.medicoId)

            if (medico != null) {
                turno.nombreMedico = medico.nombre
                turno.consultorio = medico.consultorioAsignado.numero.toString()
            }
        }
    }

    fun obtenerTodos(): List<Turno> {
        sincronizarDatos()
        return turnos.toList()
    }

    fun buscarPorId(id: Int): Turno? {
        sincronizarDatos()
        return turnos.find { it.id == id }
    }

    private fun validarTurno(
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean,
        turnoActualId: Int? = null
    ) {
        val dniNormalizado = normalizarDni(dniPaciente)

        require(dniNormalizado.isNotEmpty()) {
            "El DNI del paciente es obligatorio."
        }

        require(
            PacientesRepository.buscarPorDni(dniNormalizado) != null
        ) {
            "No existe un paciente registrado con ese DNI."
        }

        val formatoFecha = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {
            isLenient = false
        }

        val fechaValida = try {
            val fechaParseada = formatoFecha.parse(fecha)
            fechaParseada != null &&
                    formatoFecha.format(fechaParseada) == fecha
        } catch (_: Exception) {
            false
        }

        require(fechaValida) {
            "Ingresá una fecha válida con formato dd/MM/aaaa."
        }

        require(
            Regex("^([01][0-9]|2[0-3]):00$").matches(horario)
        ) {
            "El horario debe ser una hora en punto, por ejemplo 09:00."
        }

        val medico = MedicosRepository.buscarPorId(medicoId)
            ?: throw IllegalArgumentException(
                "Seleccioná un médico válido."
            )

        require(
            MedicosRepository.estaDisponible(
                medico = medico,
                fecha = fecha,
                horario = horario
            )
        ) {
            "El médico seleccionado no atiende en ese día u horario."
        }

        val otrosTurnos = turnos.filter {
            it.id != turnoActualId &&
                    it.fecha == fecha &&
                    it.horario == horario &&
                    !it.estado.equals("Cancelado", ignoreCase = true)
        }

        val mismoMedico = otrosTurnos.filter {
            it.medicoId == medicoId
        }

        if (esSobreturno) {
            require(mismoMedico.any { !it.esSobreturno }) {
                "Para asignar un sobreturno debe existir un turno normal para ese médico y horario."
            }

            require(mismoMedico.none { it.esSobreturno }) {
                "Ya existe un sobreturno para ese médico y horario."
            }
        } else {
            require(mismoMedico.isEmpty()) {
                "El médico ya tiene un turno en ese horario. Podés revisar si corresponde un sobreturno."
            }
        }

        val consultorioOcupado = otrosTurnos.any { turno ->
            val otroMedico = MedicosRepository.buscarPorId(turno.medicoId)

            otroMedico != null &&
                    otroMedico.consultorioAsignado == medico.consultorioAsignado
        }

        require(!consultorioOcupado) {
            "El consultorio ${medico.consultorioAsignado.numero} ya está ocupado en ese horario."
        }
    }

    fun agregarTurno(
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean = false
    ): Turno {

        val medico = MedicosRepository.buscarPorId(medicoId)
            ?: throw IllegalArgumentException(
                "Seleccioná un médico válido."
            )

        validarTurno(
            dniPaciente = dniPaciente,
            fecha = fecha,
            horario = horario,
            medicoId = medicoId,
            esSobreturno = esSobreturno
        )

        val paciente = PacientesRepository.buscarPorDni(
            normalizarDni(dniPaciente)
        ) ?: throw IllegalArgumentException(
            "No existe un paciente registrado con ese DNI."
        )

        val nuevoTurno = Turno(
            id = siguienteId++,
            nombrePaciente = paciente.nombreCompleto,
            dniPaciente = paciente.dni,
            fecha = fecha,
            horario = horario,
            consultorio = medico.consultorioAsignado.numero.toString(),
            estado = "Pendiente",
            medicoId = medico.id,
            nombreMedico = medico.nombre,
            esSobreturno = esSobreturno,
            pacienteId = paciente.id
        )

        turnos.add(nuevoTurno)

        return nuevoTurno
    }

    fun modificarTurno(
        turno: Turno,
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean
    ) {
        val medico = MedicosRepository.buscarPorId(medicoId)
            ?: throw IllegalArgumentException(
                "Seleccioná un médico válido."
            )

        validarTurno(
            dniPaciente = dniPaciente,
            fecha = fecha,
            horario = horario,
            medicoId = medicoId,
            esSobreturno = esSobreturno,
            turnoActualId = turno.id
        )

        val paciente = PacientesRepository.buscarPorDni(
            normalizarDni(dniPaciente)
        ) ?: throw IllegalArgumentException(
            "No existe un paciente registrado con ese DNI."
        )

        turno.pacienteId = paciente.id
        turno.nombrePaciente = paciente.nombreCompleto
        turno.dniPaciente = paciente.dni
        turno.fecha = fecha
        turno.horario = horario
        turno.consultorio = medico.consultorioAsignado.numero.toString()
        turno.medicoId = medico.id
        turno.nombreMedico = medico.nombre
        turno.esSobreturno = esSobreturno
    }

    fun actualizarEstado(id: Int, nuevoEstado: String): Boolean {
        val turno = buscarPorId(id) ?: return false

        turno.estado = nuevoEstado

        return true
    }

    fun cancelarTurno(id: Int): Boolean {
        val turno = buscarPorId(id) ?: return false

        return turnos.remove(turno)
    }
}
