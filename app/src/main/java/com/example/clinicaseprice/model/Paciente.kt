package com.example.clinicaseprice.model

data class Paciente(
    val id: Int,
    var nombreCompleto: String,
    var dni: String,
    var telefono: String,

    // Datos opcionales
    var obraSocial: String = "",
    var nombre: String = "",
    var apellido: String = "",
    var fechaNacimiento: String = "",
    var domicilio: String = "",
    var email: String = "",
    var numeroObraSocial: String = "",
    var isSelected : Boolean = false,
    var numeroHistoriaClinica: String = "",


    // Historial de turnos
    var turnos: MutableList<Turno> = mutableListOf()
)
