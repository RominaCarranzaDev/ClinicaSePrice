
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
                    isSelected = true,
                    nombre = "Sofía Valentina",
                    apellido = "Martínez"
                ),
                Paciente(
                    id = 2,
                    nombreCompleto = "Juan Carlos Pérez",
                    dni = "29412392",
                    telefono = "1150124411",
                    obraSocial = "Swiss Medical",
                    ultimoTurno = "23 Oct 2024",
                    estado = "PENDIENTE",
                    nombre = "Juan Carlos",
                    apellido = "Pérez"
                ),
                Paciente(
                    id = 3,
                    nombreCompleto = "Amelia Ruiz",
                    dni = "34112441",
                    telefono = "1132149922",
                    obraSocial = "Galeno",
                    ultimoTurno = "24 Oct 2024",
                    estado = "CONFIRMADO",
                    nombre = "Amelia",
                    apellido = "Ruiz"
                )
            )
        )

        siguienteId = (pacientes.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun normalizarDni(dni: String): String {
        return dni.filter { it.isDigit() }
    }

    fun obtenerTodos(): List<Paciente> {
        return pacientes.toList()
    }

    fun buscarPorDni(dni: String): Paciente? {
        val dniNormalizado = normalizarDni(dni)

        if (dniNormalizado.isEmpty()) return null

        return pacientes.find {
            normalizarDni(it.dni) == dniNormalizado
        }
    }

    fun buscarPorId(id: Int): Paciente? {
        return pacientes.find { it.id == id }
    }

    fun agregarPaciente(
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String,
        apellido: String = "",
        fechaNacimiento: String = "",
        domicilio: String = "",
        email: String = "",
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

        val nombreCompleto = listOf(nombre.trim(), apellido.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")

        val nuevoPaciente = Paciente(
            id = siguienteId++,
            nombreCompleto = nombreCompleto,
            dni = dniNormalizado,
            telefono = telefono.trim(),
            obraSocial = obraSocial.trim(),
            ultimoTurno = "Sin turnos",
            estado = "PENDIENTE",
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            fechaNacimiento = fechaNacimiento.trim(),
            domicilio = domicilio.trim(),
            email = email.trim(),
            numeroObraSocial = numeroObraSocial.trim()
        )

        pacientes.add(nuevoPaciente)

        return nuevoPaciente
    }

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

        val nombreCompleto = listOf(nombre.trim(), apellido.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")

        paciente.nombreCompleto = nombreCompleto
        paciente.nombre = nombre.trim()
        paciente.apellido = apellido.trim()
        paciente.dni = dniNormalizado
        paciente.telefono = telefono.trim()
        paciente.obraSocial = obraSocial.trim()
        paciente.fechaNacimiento = fechaNacimiento.trim()
        paciente.domicilio = domicilio.trim()
        paciente.email = email.trim()
        paciente.numeroObraSocial = numeroObraSocial.trim()

        return true
    }

    fun actualizarEstado(id: Int, nuevoEstado: String): Boolean {
        val paciente = buscarPorId(id) ?: return false
        paciente.estado = nuevoEstado
        return true
    }
}
