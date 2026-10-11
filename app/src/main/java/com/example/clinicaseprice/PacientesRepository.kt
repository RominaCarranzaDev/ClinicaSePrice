
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

    fun agregarPaciente(
        nombre: String,
        dni: String,
        telefono: String,
        obraSocial: String
    ): Paciente {
        val nuevoPaciente = Paciente(
            id = siguienteId++,
            nombreCompleto = nombre,
            dni = dni,
            telefono = telefono,
            obraSocial = obraSocial,
            ultimoTurno = "Sin turnos",
            estado = "PENDIENTE"
        )

        pacientes.add(nuevoPaciente)
        return nuevoPaciente
    }
}
