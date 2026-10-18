package com.example.clinicaseprice.model

data class Turno(
    val id: Int,
    var fecha: String,
    var horario: String,
    var consultorio: String,
    var estado: String = "PENDIENTE",
    var esSobreturno: Boolean = false,
    var nombreMedico: String = "Sin asignar",
    var pacienteId: Int,
    var medicoId: Int = 0
    )