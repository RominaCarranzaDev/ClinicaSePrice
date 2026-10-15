package com.example.clinicaseprice.model

data class Medico(
    val id: Int,
    val nombre: String,
    val consultorioAsignado: Consultorio,
    val horarios: Map<Int, List<FranjaHoraria>>
)

enum class Consultorio(val numero: Int) {
    CONSULTORIO_1(1),
    CONSULTORIO_2(2),
    CONSULTORIO_3(3)
}

data class FranjaHoraria(
    val horaInicio: Int,
    val horaFin: Int
)
