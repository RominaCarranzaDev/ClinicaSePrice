package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.model.Turno

import java.text.SimpleDateFormat
import java.util.Locale

object PacientesRepository {

    val pacientes = mutableListOf<Paciente>()

    private var siguienteId = 1

    private val formatoFechaHora =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT).apply {
            isLenient = false
        }

    init {

        pacientes.addAll(
            listOf(
                Paciente(
                    id = 1,
                    nombreCompleto = "Sofía Valentina Martínez",
                    dni = "38241902",
                    telefono = "1140591921",
                    obraSocial = "Particular",
                    nombre = "Sofía Valentina",
                    apellido = "Martínez",
                    numeroHistoriaClinica = "HC-001",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 2,
                    nombreCompleto = "Juan Carlos Pérez",
                    dni = "29412392",
                    telefono = "1150124411",
                    obraSocial = "Swiss Medical",
                    nombre = "Juan Carlos",
                    apellido = "Pérez",
                    numeroHistoriaClinica = "HC-002",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 3,
                    nombreCompleto = "Amelia Ruiz",
                    dni = "34112441",
                    telefono = "1132149922",
                    obraSocial = "Galeno",
                    nombre = "Amelia",
                    apellido = "Ruiz",
                    numeroHistoriaClinica = "HC-003",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 4,
                    nombreCompleto = "Martín González",
                    dni = "32845123",
                    telefono = "1162034512",
                    obraSocial = "OSDE",
                    nombre = "Martín",
                    apellido = "González",
                    numeroHistoriaClinica = "HC-004",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 5,
                    nombreCompleto = "Lucía Fernández",
                    dni = "40123876",
                    telefono = "1145876321",
                    obraSocial = "PAMI",
                    nombre = "Lucía",
                    apellido = "Fernández",
                    numeroHistoriaClinica = "HC-005",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 6,
                    nombreCompleto = "Diego Rodríguez",
                    dni = "27654321",
                    telefono = "1156987452",
                    obraSocial = "IOMA",
                    nombre = "Diego",
                    apellido = "Rodríguez",
                    numeroHistoriaClinica = "HC-006",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 7,
                    nombreCompleto = "Valentina López",
                    dni = "42356789",
                    telefono = "1167892345",
                    obraSocial = "Swiss Medical",
                    nombre = "Valentina",
                    apellido = "López",
                    numeroHistoriaClinica = "HC-007",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 8,
                    nombreCompleto = "Facundo Torres",
                    dni = "31567890",
                    telefono = "1141236789",
                    obraSocial = "Galeno",
                    nombre = "Facundo",
                    apellido = "Torres",
                    numeroHistoriaClinica = "HC-008",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 9,
                    nombreCompleto = "Camila Sánchez",
                    dni = "39567812",
                    telefono = "1154321987",
                    obraSocial = "OSDE",
                    nombre = "Camila",
                    apellido = "Sánchez",
                    numeroHistoriaClinica = "HC-009",
                    fechaNacimiento = "11/10/1990"
                ),
                Paciente(
                    id = 10,
                    nombreCompleto = "Mateo Díaz",
                    dni = "44876543",
                    telefono = "1165432198",
                    obraSocial = "Particular",
                    nombre = "Mateo",
                    apellido = "Díaz",
                    numeroHistoriaClinica = "HC-010",
                    fechaNacimiento = "11/10/1990"
                )
            )
        )

        siguienteId = (pacientes.maxOfOrNull { it.id } ?: 0) + 1
    }

    // Normaliza el DNI para comparar solo los números.
    private fun normalizarDni(dni: String): String =
        dni.filter { it.isDigit() }

    // Obtener todos los pacientes.
    fun obtenerTodos(): List<Paciente> =
        pacientes.toList()

    // Buscar por DNI.
    fun buscarPorDni(dni: String): Paciente? {
        val dniNormalizado = normalizarDni(dni)

        if (dniNormalizado.isEmpty()) return null

        return pacientes.find {
            normalizarDni(it.dni) == dniNormalizado
        }
    }

    // Buscar por nombre, apellido o nombre completo.
    fun buscarPorNombre(nombre: String): List<Paciente> {
        val consulta = nombre.trim()

        if (consulta.isBlank()) return emptyList()

        return pacientes.filter {
            it.nombreCompleto.contains(
                consulta,
                ignoreCase = true
            ) ||
                    it.nombre.contains(
                        consulta,
                        ignoreCase = true
                    ) ||
                    it.apellido.contains(
                        consulta,
                        ignoreCase = true
                    )
        }
    }

    // Buscar por ID.
    fun buscarPorId(id: Int): Paciente? =
        pacientes.find { it.id == id }

    // Crear un paciente nuevo.
    fun agregarPaciente(
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String,
        apellido: String,
        fechaNacimiento: String,
        domicilio: String = "",
        email: String,
        numeroObraSocial: String = ""
    ): Paciente {

        require(nombre.isNotBlank()) {
            "El nombre del paciente es obligatorio."
        }

        val dniNormalizado = normalizarDni(dni)

        require(dniNormalizado.isNotEmpty()) {
            "El DNI es obligatorio."
        }

        require(buscarPorDni(dniNormalizado) == null) {
            "Ya existe un paciente registrado con ese DNI."
        }

        val nombreLimpio = nombre.trim()
        val apellidoLimpio = apellido.trim()

        val nombreCompleto = listOf(
            nombreLimpio,
            apellidoLimpio
        ).filter { it.isNotEmpty() }.joinToString(" ")

        val numeroHistoria = "HC-${String.format("%03d", siguienteId)}"

        val nuevoPaciente = Paciente(
            id = siguienteId++,
            nombreCompleto = nombreCompleto,
            dni = dniNormalizado,
            telefono = telefono.trim(),
            obraSocial = obraSocial.trim(),
            nombre = nombreLimpio,
            apellido = apellidoLimpio,
            fechaNacimiento = fechaNacimiento.trim(),
            domicilio = domicilio.trim(),
            email = email.trim(),
            numeroObraSocial = numeroObraSocial.trim(),
            numeroHistoriaClinica = numeroHistoria
        )

        pacientes.add(nuevoPaciente)

        return nuevoPaciente
    }

    // Actualizar los datos sin perder los turnos.
    fun actualizarPaciente(
        id: Int,
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String,
        apellido: String = "",
        fechaNacimiento: String = "",
        domicilio: String = "",
        email: String = "",
        numeroObraSocial: String = ""
    ): Boolean {

        val paciente = buscarPorId(id) ?: return false

        require(nombre.isNotBlank()) {
            "El nombre del paciente es obligatorio."
        }

        val dniNormalizado = normalizarDni(dni)

        require(dniNormalizado.isNotEmpty()) {
            "El DNI es obligatorio."
        }

        require(
            pacientes.none {
                it.id != id &&
                        normalizarDni(it.dni) == dniNormalizado
            }
        ) {
            "Ya existe otro paciente registrado con ese DNI."
        }

        val nombreLimpio = nombre.trim()
        val apellidoLimpio = apellido.trim()

        paciente.nombre = nombreLimpio
        paciente.apellido = apellidoLimpio
        paciente.nombreCompleto = listOf(
            nombreLimpio,
            apellidoLimpio
        ).filter { it.isNotEmpty() }.joinToString(" ")

        paciente.dni = dniNormalizado
        paciente.telefono = telefono.trim()
        paciente.obraSocial = obraSocial.trim()
        paciente.fechaNacimiento = fechaNacimiento.trim()
        paciente.domicilio = domicilio.trim()
        paciente.email = email.trim()
        paciente.numeroObraSocial = numeroObraSocial.trim()

        return true
    }

    // Obtener todos los turnos de un paciente.
    fun obtenerTurnos(id: Int): List<Turno> {
        return buscarPorId(id)
            ?.turnos
            ?.toList()
            ?: emptyList()
    }

    // Convierte fecha y hora a un valor comparable.
    private fun fechaHoraComoLong(turno: Turno): Long? {
        return runCatching {
            formatoFechaHora.parse(
                "${turno.fecha} ${turno.horario}"
            )?.time
        }.getOrNull()
    }

    // Obtener un turno de una fecha determinada.
    fun obtenerTurnoDelDia(
        id: Int,
        fecha: String
    ): Turno? {
        return obtenerTurnos(id).firstOrNull {
            it.fecha == fecha
        }
    }
}