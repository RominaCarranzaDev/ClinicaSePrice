package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Medico
import com.example.clinicaseprice.model.Turno
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TurnosRepository {
    private val listaTurnos = mutableListOf<Turno>()

    // Permite consultar los turnos sin exponer la lista modificable.
    val turnos: List<Turno>
        get() = listaTurnos.toList()

    private var siguienteId = 1
    private const val DURACION_TURNO_MINUTOS = 30
    private const val ESTADO_PENDIENTE = "PENDIENTE"
    private const val ESTADO_CANCELADO = "CANCELADO"

    init {
        cargarTurnosIniciales()
    }

    // Turnos iniciales de los pacientes del Repo
    private fun cargarTurnosIniciales() {
        val formatoFecha = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        fun fechaConDiasDeDiferencia(dias: Int): String {
            val calendario = Calendar.getInstance()
            calendario.add(Calendar.DAY_OF_YEAR, dias)
            return formatoFecha.format(calendario.time)
        }

        val hoy = fechaConDiasDeDiferencia(0)
        val ayer = fechaConDiasDeDiferencia(-1)
        val semanaPasada = fechaConDiasDeDiferencia(-7)
        val proximaSemana = fechaConDiasDeDiferencia(7)

        val turnosIniciales = listOf(
            Turno(
                id = 1,
                fecha = hoy,
                horario = "08:00",
                consultorio = "1",
                pacienteId = 1,
                medicoId = 1
            ),
            Turno(
                id = 2,
                fecha = hoy,
                horario = "08:30",
                consultorio = "1",
                pacienteId = 2,
                medicoId = 1
            ),
            Turno(
                id = 3,
                fecha = semanaPasada,
                horario = "10:00",
                consultorio = "2",
                pacienteId = 2,
                medicoId = 1
            ),
            Turno(
                id = 4,
                fecha = hoy,
                horario = "09:00",
                consultorio = "2",
                pacienteId = 3,
                medicoId = 2
            ),
            Turno(
                id = 5,
                fecha = ayer,
                horario = "11:00",
                consultorio = "2",
                pacienteId = 3,
                medicoId = 2
            ),
            Turno(
                id = 6,
                fecha = proximaSemana,
                horario = "09:30",
                consultorio = "2",
                pacienteId = 3,
                medicoId = 2
            ),
            Turno(
                id = 7,
                fecha = hoy,
                horario = "09:30",
                consultorio = "1",
                pacienteId = 4,
                medicoId = 1
            ),
            Turno(
                id = 8,
                fecha = hoy,
                horario = "10:00",
                consultorio = "1",
                pacienteId = 5,
                medicoId = 1
            ),
            Turno(
                id = 9,
                fecha = proximaSemana,
                horario = "10:30",
                consultorio = "1",
                pacienteId = 5,
                medicoId = 1
            ),
            Turno(
                id = 10,
                fecha = hoy,
                horario = "10:30",
                consultorio = "2",
                pacienteId = 6,
                medicoId = 2
            ),
            Turno(
                id = 11,
                fecha = semanaPasada,
                horario = "12:00",
                consultorio = "2",
                pacienteId = 6,
                medicoId = 2
            ),
            Turno(
                id = 12,
                fecha = proximaSemana,
                horario = "11:00",
                consultorio = "2",
                pacienteId = 6,
                medicoId = 2
            ),
            Turno(
                id = 13,
                fecha = hoy,
                horario = "11:00",
                consultorio = "1",
                pacienteId = 7,
                medicoId = 1
            ),
            Turno(
                id = 14,
                fecha = hoy,
                horario = "11:30",
                consultorio = "2",
                pacienteId = 8,
                medicoId = 2
            ),
            Turno(
                id = 15,
                fecha = ayer,
                horario = "14:00",
                consultorio = "2",
                pacienteId = 8,
                medicoId = 2
            ),
            Turno(
                id = 16,
                fecha = hoy,
                horario = "12:00",
                consultorio = "1",
                pacienteId = 9,
                medicoId = 1
            ),
            Turno(
                id = 17,
                fecha = hoy,
                horario = "12:30",
                consultorio = "2",
                pacienteId = 10,
                medicoId = 2
            ),
            Turno(
                id = 18,
                fecha = semanaPasada,
                horario = "15:00",
                consultorio = "2",
                pacienteId = 10,
                medicoId = 2
            ),
            Turno(
                id = 19,
                fecha = proximaSemana,
                horario = "13:00",
                consultorio = "2",
                pacienteId = 10,
                medicoId = 2
            )
        )

        listaTurnos.addAll(turnosIniciales)

        siguienteId =
            (listaTurnos.maxOfOrNull { it.id } ?: 0) + 1
    }

    // FUNCIONES AUXILIARES
    private fun normalizarDni(dni: String): String {
        return dni.filter { it.isDigit() }
    }

    private fun fechaValida(fecha: String): Date? {
        return try {
            val formato = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            ).apply {
                isLenient = false
            }

            val fechaParseada = formato.parse(fecha)
                ?: return null

            if (formato.format(fechaParseada) != fecha) {
                null
            } else {
                fechaParseada
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun minutosDelDia(horario: String): Int? {
        if (
            !Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
                .matches(horario)
        ) {
            return null
        }

        val partes = horario.split(":")

        return partes[0].toInt() * 60 +
                partes[1].toInt()
    }

    private fun diaSemana(fecha: String): Int? {
        val fechaParseada = fechaValida(fecha)
            ?: return null

        val calendario = Calendar.getInstance()
        calendario.time = fechaParseada

        return calendario.get(Calendar.DAY_OF_WEEK)
    }

    private fun estaCancelado(turno: Turno): Boolean {
        return turno.estado.equals(
            ESTADO_CANCELADO,
            ignoreCase = true
        )
    }

    private fun turnoActivo(turno: Turno): Boolean {
        return !estaCancelado(turno)
    }

    private fun seSuperponen(
        horarioA: String,
        horarioB: String
    ): Boolean {
        val inicioA = minutosDelDia(horarioA)
            ?: return false

        val inicioB = minutosDelDia(horarioB)
            ?: return false

        val finA = inicioA + DURACION_TURNO_MINUTOS
        val finB = inicioB + DURACION_TURNO_MINUTOS

        return inicioA < finB && finA > inicioB
    }

    // SINCRONIZACIÓN DE DATOS
    private fun sincronizarDatos() {
        listaTurnos.forEach { turno ->

            // El paciente se relaciona por su ID.
            PacientesRepository.buscarPorId(
                turno.pacienteId
            )

            // Actualiza los datos del médico y consultorio.
            val medico = MedicosRepository.buscarPorId(
                turno.medicoId
            )

            if (medico != null) {
                turno.nombreMedico =
                    "${medico.nombre} ${medico.apellido}"

                turno.consultorio =
                    medico.consultorioAsignado.numero.toString()
            }
        }
    }

    // CONSULTAS
    fun obtenerTodos(): List<Turno> {
        sincronizarDatos()
        return listaTurnos.toList()
    }

    fun buscarPorId(id: Int): Turno? {
        sincronizarDatos()
        return listaTurnos.find { it.id == id }
    }

    fun obtenerTurnosPorPaciente(
        pacienteId: Int
    ): List<Turno> {
        sincronizarDatos()

        return listaTurnos.filter {
            it.pacienteId == pacienteId
        }
    }

    fun obtenerTurnosPorMedico(
        medicoId: Int,
        fecha: String
    ): List<Turno> {
        sincronizarDatos()

        return listaTurnos.filter {
            it.medicoId == medicoId &&
                    it.fecha == fecha &&
                    turnoActivo(it)
        }
    }


    // DISPONIBILIDAD DEL CONSULTORIO
    private fun consultorioDisponible(
        medico: Medico,
        fecha: String,
        horario: String,
        turnoActualId: Int? = null
    ): Boolean {

        val consultorioNumero =
            medico.consultorioAsignado.numero.toString()

        return listaTurnos.none { otroTurno ->

            if (
                otroTurno.id == turnoActualId ||
                !turnoActivo(otroTurno) ||
                otroTurno.fecha != fecha ||
                otroTurno.medicoId == medico.id
            ) {
                false
            } else {
                val mismoConsultorio =
                    otroTurno.consultorio == consultorioNumero

                mismoConsultorio &&
                        seSuperponen(
                            horario,
                            otroTurno.horario
                        )
            }
        }
    }

    // VALIDACIÓN DE DISPONIBILIDAD
    fun estaDisponible(
        medico: Medico,
        fecha: String,
        horario: String
    ): Boolean {

        val inicio = minutosDelDia(horario)
            ?: return false

        if (inicio % DURACION_TURNO_MINUTOS != 0) {
            return false
        }

        if (fechaValida(fecha) == null) {
            return false
        }

        if (
            !MedicosRepository.estaDisponible(
                medico = medico,
                fecha = fecha,
                horario = horario
            )
        ) {
            return false
        }

        val normalExistente = listaTurnos.any {
            it.medicoId == medico.id &&
                    it.fecha == fecha &&
                    it.horario == horario &&
                    !estaCancelado(it) &&
                    !it.esSobreturno
        }

        if (normalExistente) {
            return false
        }

        return consultorioDisponible(
            medico = medico,
            fecha = fecha,
            horario = horario
        )
    }

    // AGENDA Y HORARIOS DISPONIBLES
    fun obtenerHorariosDisponibles(
        medico: Medico,
        fecha: String
    ): List<HorarioDisponible> {

        val dia = diaSemana(fecha)
            ?: return emptyList()

        val franjas = medico.horarios[dia]
            ?: return emptyList()

        val disponibles = mutableListOf<HorarioDisponible>()

        for (franja in franjas) {
            val inicio = minutosDelDia(franja.horaInicio)
                ?: continue

            val fin = minutosDelDia(franja.horaFin)
                ?: continue

            var minuto = inicio

            while (
                minuto + DURACION_TURNO_MINUTOS <= fin
            ) {
                val horario = String.format(
                    Locale.ROOT,
                    "%02d:%02d",
                    minuto / 60,
                    minuto % 60
                )

                val turnosDelHorario = listaTurnos.filter {
                    it.medicoId == medico.id &&
                            it.fecha == fecha &&
                            it.horario == horario &&
                            turnoActivo(it)
                }

                val normalExistente = turnosDelHorario.any { !it.esSobreturno }

                val sobreturnoExistente = turnosDelHorario.any { it.esSobreturno }

                val consultorioLibre = consultorioDisponible(
                    medico = medico,
                    fecha = fecha,
                    horario = horario
                )

                if (consultorioLibre) {
                    when {
                        // No existe un turno: ofrecer uno normal.
                        !normalExistente &&
                                !sobreturnoExistente -> {

                            if (
                                MedicosRepository.estaDisponible(
                                    medico,
                                    fecha,
                                    horario
                                )
                            ) {
                                disponibles.add(
                                    HorarioDisponible(
                                        medico = medico,
                                        fecha = fecha,
                                        horario = horario,
                                        consultorio = medico
                                            .consultorioAsignado
                                            .numero
                                            .toString(),
                                        esSobreturno = false
                                    )
                                )
                            }
                        }

                        // Existe el turno normal: ofrecer sobreturno.
                        normalExistente &&
                                !sobreturnoExistente -> {

                            if (
                                MedicosRepository.estaDisponible(
                                    medico,
                                    fecha,
                                    horario
                                )
                            ) {
                                disponibles.add(
                                    HorarioDisponible(
                                        medico = medico,
                                        fecha = fecha,
                                        horario = horario,
                                        consultorio = medico
                                            .consultorioAsignado
                                            .numero
                                            .toString(),
                                        esSobreturno = true
                                    )
                                )
                            }
                        }
                    }
                }

                minuto += DURACION_TURNO_MINUTOS
            }
        }

        return disponibles
    }

    fun obtenerDisponibilidadPorEspecialidad(
        especialidad: String,
        fecha: String
    ): List<HorarioDisponible> {

        return MedicosRepository
            .buscarPorEspecialidad(especialidad)
            .flatMap { medico ->
                obtenerHorariosDisponibles(medico, fecha)
            }
            .sortedWith(
                compareBy<HorarioDisponible>(
                    { it.horario },
                    { it.medico.apellido }
                )
            )
    }

    // VALIDACIÓN PARA AGREGAR O MODIFICAR
    private fun validarTurno(
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean,
        turnoActualId: Int? = null
    ) {

        sincronizarDatos()

        val dniNormalizado = normalizarDni(dniPaciente)

        require(dniNormalizado.isNotEmpty()) {
            "El DNI del paciente es obligatorio."
        }

        require(
            PacientesRepository.buscarPorDni(
                dniNormalizado
            ) != null
        ) {
            "No existe un paciente registrado con ese DNI."
        }

        require(fechaValida(fecha) != null) {
            "Ingresá una fecha válida con formato dd/MM/aaaa."
        }

        val inicio = minutosDelDia(horario)

        require(inicio != null) {
            "Ingresá un horario válido con formato HH:mm."
        }

        require(inicio % DURACION_TURNO_MINUTOS == 0) {
            "Los turnos deben comenzar cada 30 minutos."
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
            "El médico no atiende en ese día u horario."
        }

        val otrosTurnos = listaTurnos.filter {
            it.id != turnoActualId &&
                    it.fecha == fecha &&
                    it.medicoId == medicoId &&
                    it.horario == horario &&
                    turnoActivo(it)
        }

        val turnoNormalExistente =
            otrosTurnos.any { !it.esSobreturno }

        val sobreturnoExistente =
            otrosTurnos.any { it.esSobreturno }

        if (esSobreturno) {
            require(turnoNormalExistente) {
                "Para asignar un sobreturno debe existir un turno normal activo para ese médico y horario."
            }

            require(!sobreturnoExistente) {
                "Ya existe un sobreturno para ese médico y horario."
            }
        } else {
            require(!turnoNormalExistente) {
                "El médico ya tiene un turno normal en ese horario."
            }
        }

        require(
            consultorioDisponible(
                medico = medico,
                fecha = fecha,
                horario = horario,
                turnoActualId = turnoActualId
            )
        ) {
            "El consultorio ${medico.consultorioAsignado.numero} ya está ocupado por otro médico en ese horario."
        }
    }

    // AGREGAR TURNO
    fun agregarTurno(
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean = false
    ): Turno {

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

        val medico = MedicosRepository.buscarPorId(medicoId)
            ?: throw IllegalArgumentException(
                "Seleccioná un médico válido."
            )

        val nuevoTurno = Turno(
            id = siguienteId++,
            fecha = fecha,
            horario = horario,
            consultorio = medico.consultorioAsignado.numero.toString(),
            estado = ESTADO_PENDIENTE,
            esSobreturno = esSobreturno,
            nombreMedico = "${medico.nombre} ${medico.apellido}",
            pacienteId = paciente.id,
            medicoId = medico.id
        )

        listaTurnos.add(nuevoTurno)

        return nuevoTurno
    }

    // MODIFICAR TURNO
    fun modificarTurno(
        turno: Turno,
        dniPaciente: String,
        fecha: String,
        horario: String,
        medicoId: Int,
        esSobreturno: Boolean
    ) {

        val turnoGuardado = listaTurnos.find {
            it.id == turno.id
        } ?: throw IllegalArgumentException(
            "El turno que intentás modificar no existe."
        )

        require(!estaCancelado(turnoGuardado)) {
            "No se puede modificar un turno cancelado."
        }

        validarTurno(
            dniPaciente = dniPaciente,
            fecha = fecha,
            horario = horario,
            medicoId = medicoId,
            esSobreturno = esSobreturno,
            turnoActualId = turnoGuardado.id
        )

        val paciente = PacientesRepository.buscarPorDni(
            normalizarDni(dniPaciente)
        ) ?: throw IllegalArgumentException(
            "No existe un paciente registrado con ese DNI."
        )

        val medico = MedicosRepository.buscarPorId(medicoId)
            ?: throw IllegalArgumentException(
                "Seleccioná un médico válido."
            )

        turnoGuardado.pacienteId = paciente.id
        turnoGuardado.fecha = fecha
        turnoGuardado.horario = horario
        turnoGuardado.consultorio =
            medico.consultorioAsignado.numero.toString()
        turnoGuardado.medicoId = medico.id
        turnoGuardado.nombreMedico =
            "${medico.nombre} ${medico.apellido}"
        turnoGuardado.esSobreturno = esSobreturno

        if (turno !== turnoGuardado) {
            turno.pacienteId = turnoGuardado.pacienteId
            turno.fecha = turnoGuardado.fecha
            turno.horario = turnoGuardado.horario
            turno.consultorio = turnoGuardado.consultorio
            turno.medicoId = turnoGuardado.medicoId
            turno.nombreMedico = turnoGuardado.nombreMedico
            turno.esSobreturno = turnoGuardado.esSobreturno
        }
    }

    // ESTADOS Y CANCELACIÓN
    fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    ): Boolean {

        val turno = listaTurnos.find { it.id == id }
            ?: return false

        if (nuevoEstado.equals(ESTADO_CANCELADO, true)) {
            return cancelarTurno(id)
        }

        if (nuevoEstado.isBlank()) {
            return false
        }

        turno.estado = nuevoEstado.uppercase(Locale.ROOT)

        return true
    }

    fun cancelarTurno(id: Int): Boolean {
        val turno = listaTurnos.find { it.id == id }
            ?: return false

        if (estaCancelado(turno)) {
            return true
        }

        turno.estado = ESTADO_CANCELADO

        return true
    }
}

// Para la agenda
data class HorarioDisponible(
    val medico: Medico,
    val fecha: String,
    val horario: String,
    val consultorio: String,
    val esSobreturno: Boolean = false
)
