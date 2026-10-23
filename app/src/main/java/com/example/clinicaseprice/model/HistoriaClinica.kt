package com.example.clinicaseprice.model

data class HistoriaClinica(
    val id: Int,
    val numeroHistoriaClinica: String,
    val pacienteId: Int?,
    val fechaCreacion: String,

    val grupoSanguineo: GrupoSanguineo? = null,
    val factoresRiesgo: MutableSet<FactorRiesgo> = mutableSetOf(),
    val observacionesGenerales: String = "",

    val atenciones: MutableList<AtencionMedica> = mutableListOf()
)

enum class GrupoSanguineo(val descripcion: String) {
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-"),
    O_POSITIVO("O+"),
    O_NEGATIVO("O-")
}

enum class FactorRiesgo(val descripcion: String) {
    HIPERTENSION("Hipertensión"),
    DIABETES("Diabetes"),
    TABAQUISMO("Tabaquismo"),
    OBESIDAD("Obesidad"),
    SEDENTARISMO("Sedentarismo"),
    ALCOHOL("Consumo de alcohol"),
    ALERGIAS("Alergias"),
    ENFERMEDAD_CARDIOVASCULAR("Enfermedad cardiovascular"),
}

enum class SignoVital(val descripcion: String, val unidad: String) {
    PRESION_ARTERIAL("Presión arterial", "mmHg"),
    FRECUENCIA_CARDIACA("Frecuencia cardíaca", "lpm"),
    FRECUENCIA_RESPIRATORIA("Frecuencia respiratoria", "rpm"),
    TEMPERATURA("Temperatura", "°C"),
    SATURACION_OXIGENO("Saturación de oxígeno", "%"),
    GLUCEMIA("Glucemia", "mg/dL"),
    PESO("Peso", "kg"),
    ALTURA("Altura", "cm")
}

enum class OrdenMedicaEstudio(val descripcion: String) {
    LABORATORIO("Análisis de laboratorio"),
    RADIOGRAFIA("Radiografía"),
    ECOGRAFIA("Ecografía"),
    TOMOGRAFIA("Tomografía"),
    RESONANCIA("Resonancia magnética"),
    ELECTROCARDIOGRAMA("Electrocardiograma"),
    OTRO("Otro")
}

enum class Medicamento(val nombre: String) {
    PARACETAMOL("Paracetamol"),
    IBUPROFENO("Ibuprofeno"),
    AMOXICILINA("Amoxicilina"),
    OMEPRAZOL("Omeprazol"),
    LORATADINA("Loratadina"),
    DICLOFENAC("Diclofenac")
}

enum class PresentacionMedicamento(val descripcion: String) {
    COMPRIMIDOS("Comprimidos"),
    CAPSULAS("Cápsulas"),
    JARABE("Jarabe"),
    GOTAS("Gotas"),
    CREMA("Crema"),
    POMADA("Pomada"),
    INYECTABLE("Inyectable"),
    SPRAY("Spray")
}

data class PrescripcionMedica(
    val medicamento: Medicamento,
    val presentacion: PresentacionMedicamento,
    val posologia: String = "Cada 6 hs.",
    val duracion: String = "Durante 7 días",
    val indicaciones: String = ""
)

data class AtencionMedica(
    val id: Int,
    val fecha: String,
    val medicoId: Int?,
    val anamnesis: String,
    val diagnostico: String,
    val observaciones: String = "",

    val signosVitales: SignosVitales? = null,
    val factoresRiesgo: Set<FactorRiesgo> = emptySet(),
    val prescripciones: List<PrescripcionMedica> = emptyList(),
    val estudios: List<OrdenMedicaEstudio> = emptyList()
)

data class SignosVitales(
    val presionArterial: String = "",
    val frecuenciaCardiaca: String = "",
    val temperatura: String = "",
    val saturacionOxigeno: String = ""
)