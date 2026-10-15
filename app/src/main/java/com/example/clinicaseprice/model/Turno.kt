package com.example.clinicaseprice.model

data class Turno(
    val id: Int,
    var nombrePaciente: String,
    var dniPaciente: String,
    var fecha: String,
    var horario: String,
    var consultorio: String,
    var estado: String,
    var medicoId: Int = 0,
    var nombreMedico: String = "Sin asignar",
    var esSobreturno: Boolean = false
)