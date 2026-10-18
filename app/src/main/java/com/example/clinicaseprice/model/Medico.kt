package com.example.clinicaseprice.model

data class Medico(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val matricula: String,
    var especialidad: String,
    var consultorioAsignado: Consultorio,
    var horarios: Map<Int, List<FranjaHoraria>>
)

enum class Consultorio(val numero: Int) {
    CONSULTORIO_1(1),
    CONSULTORIO_2(2),
    CONSULTORIO_3(3)
}

data class FranjaHoraria(
    val horaInicio: String,
    val horaFin: String
)
