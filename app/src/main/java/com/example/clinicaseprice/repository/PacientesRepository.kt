package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Paciente

object PacientesRepository {

    val pacientes = mutableListOf<Paciente>()

    private var siguienteId = 1

    init {
        pacientes.addAll(
            listOf(
                Paciente(
                    id = 1,
                    nombreCompleto = "Sofía Valentina Martínez",
                    dni = "38241902",
                    telefono = "1140591921",
                    obraSocial = "OSDE",
                    ultimoTurno = "23 Oct 2024",
                    estado = "CONFIRMADO",
                    isSelected = true
                ),
                Paciente(
                    id = 2,
                    nombreCompleto = "Juan Carlos Pérez",
                    dni = "29412392",
                    telefono = "1150124411",
                    obraSocial = "Swiss Medical",
                    ultimoTurno = "23 Oct 2024",
                    estado = "PENDIENTE"
                ),
                Paciente(
                    id = 3,
                    nombreCompleto = "Amelia Ruiz",
                    dni = "34112441",
                    telefono = "1132149922",
                    obraSocial = "Galeno",
                    ultimoTurno = "24 Oct 2024",
                    estado = "CONFIRMADO"
                )
            )
        )

        siguienteId = (pacientes.maxOfOrNull { it.id } ?: 0) + 1
    }

    // Normaliza el DNI para evitar diferencias por espacios o puntos.
    private fun normalizarDni(dni: String): String {
        return dni.filter { it.isDigit() }
    }

    // Devuelve todos los pacientes registrados.
    fun obtenerTodos(): List<Paciente> {
        return pacientes.toList()
    }

    // Busca un paciente por DNI.
    fun buscarPorDni(dni: String): Paciente? {
        val dniNormalizado = normalizarDni(dni)

        if (dniNormalizado.isEmpty()) return null

        return pacientes.find {
            normalizarDni(it.dni) == dniNormalizado
        }
    }

    // Busca un paciente por su identificador.
    fun buscarPorId(id: Int): Paciente? {
        return pacientes.find { it.id == id }
    }

    // Registra un paciente nuevo.
    fun agregarPaciente(
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String
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

        val nuevoPaciente = Paciente(
            id = siguienteId++,
            nombreCompleto = nombre.trim(),
            dni = dniNormalizado,
            telefono = telefono.trim(),
            obraSocial = obraSocial.trim(),
            ultimoTurno = "Sin turnos",
            estado = "PENDIENTE"
        )

        pacientes.add(nuevoPaciente)

        return nuevoPaciente
    }

    // Actualiza los datos de un paciente existente.
    fun actualizarPaciente(
        id: Int,
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String
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

        paciente.nombreCompleto = nombre.trim()
        paciente.dni = dniNormalizado
        paciente.telefono = telefono.trim()
        paciente.obraSocial = obraSocial.trim()

        return true
    }
    fun actualizarEstado(id: Int, nuevoEstado: String): Boolean {
        val paciente = buscarPorId(id) ?: return false
        paciente.estado = nuevoEstado
        return true
    }
}
