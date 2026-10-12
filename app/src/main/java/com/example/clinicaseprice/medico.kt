
package com.example.clinicaseprice

data class Medico(
    val id: Int,
    val nombre: String,
    val consultorio: String,
    val horarios: Map<Int, List<FranjaHoraria>>
)

data class FranjaHoraria(
    val horaInicio: Int,
    val horaFin: Int
)
