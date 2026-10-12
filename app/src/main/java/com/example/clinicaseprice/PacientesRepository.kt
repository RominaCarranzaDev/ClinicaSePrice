
package com.example.clinicaseprice

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

    private fun normalizarDni(dni: String): String {
        return dni.filter { it.isDigit() }
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
}
