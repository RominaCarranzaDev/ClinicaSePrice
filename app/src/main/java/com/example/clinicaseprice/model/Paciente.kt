
package com.example.clinicaseprice.model

data class Paciente(
    val id: Int,
    var nombreCompleto: String,
    var dni: String,
    var telefono: String,
    var obraSocial: String,
    var ultimoTurno: String,
    var estado: String = "PENDIENTE",
    var isSelected: Boolean = false,
    var nombre: String = "",
    var apellido: String = "",
    var fechaNacimiento: String = "",
    var domicilio: String = "",
    var email: String = "",
    var numeroObraSocial: String = ""
)
