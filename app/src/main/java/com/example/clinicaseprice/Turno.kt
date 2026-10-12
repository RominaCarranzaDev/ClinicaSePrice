package com.example.clinicaseprice

data class Turno(
    val id: Int,
    var nombrePaciente: String,
    var dniPaciente: String,
    var fecha: String,
    var horario: String,
    var consultorio: String,
    var estado: String
)
