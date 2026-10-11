package com.example.clinicaseprice.model

data class Paciente(
    val id: Int,
    var nombreCompleto: String,
    var dni: String,
    var telefono: String,
    var obraSocial: String,
    var ultimoTurno: String,
    var estado: String = "PENDIENTE",
    var isSelected: Boolean = false
)
