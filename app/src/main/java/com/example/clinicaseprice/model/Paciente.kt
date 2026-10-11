package com.example.clinicaseprice.model

data class Paciente(
    val id: Int,
    val nombreCompleto: String,
    val dni: String,
    val telefono: String,
    val obraSocial: String,
    val ultimoTurno: String,
    val estado: String,
    val isSelected: Boolean = false
)