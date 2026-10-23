package com.example.clinicaseprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.R
import com.example.clinicaseprice.model.AtencionMedica
import com.example.clinicaseprice.repository.MedicosRepository

class AtencionMedicaAdapter(
    private val atenciones: List<AtencionMedica>
) : RecyclerView.Adapter<AtencionMedicaAdapter.AtencionViewHolder>() {

    class AtencionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvFecha: TextView =
            itemView.findViewById(R.id.tvFechaAtencion)

        val tvMedico: TextView =
            itemView.findViewById(R.id.tvMedicoAtencion)

        val tvAnamnesis: TextView =
            itemView.findViewById(R.id.tvAnamnesis)

        val tvDiagnostico: TextView =
            itemView.findViewById(R.id.tvDiagnostico)

        val tvObservaciones: TextView =
            itemView.findViewById(R.id.tvObservaciones)

        // Nuevos datos clínicos
        val tvSignosVitales: TextView =
            itemView.findViewById(R.id.tvSignosVitales)

        val tvFactoresRiesgo: TextView =
            itemView.findViewById(R.id.tvFactoresRiesgo)

        val tvPrescripciones: TextView =
            itemView.findViewById(R.id.tvPrescripciones)

        val tvEstudios: TextView =
            itemView.findViewById(R.id.tvEstudios)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AtencionViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_historia_clinica,
                parent,
                false
            )

        return AtencionViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: AtencionViewHolder,
        position: Int
    ) {
        val atencion = atenciones[position]

        val medico = MedicosRepository.obtenerTodos()
            .find { it.id == atencion.medicoId }

        holder.tvFecha.text = "Fecha: ${atencion.fecha}"

        holder.tvMedico.text = medico?.let {
            "Médico: ${it.nombre} ${it.apellido}"
        } ?: "Médico ID: ${atencion.medicoId}"

        holder.tvAnamnesis.text =
            "Anamnesis:\n${atencion.anamnesis}"

        holder.tvDiagnostico.text =
            "Diagnóstico:\n${atencion.diagnostico}"

        holder.tvObservaciones.text =
            if (atencion.observaciones.isBlank()) {
                "Observaciones: Sin observaciones"
            } else {
                "Observaciones:\n${atencion.observaciones}"
            }

        // Signos vitales
        holder.tvSignosVitales.text = atencion.signosVitales?.let { signos ->
            """
            Signos vitales:
            Presión arterial: ${signos.presionArterial.ifBlank { "Sin registrar" }}
            Frecuencia cardíaca: ${signos.frecuenciaCardiaca.ifBlank { "Sin registrar" }}
            Temperatura: ${signos.temperatura.ifBlank { "Sin registrar" }}
            Saturación de oxígeno: ${signos.saturacionOxigeno.ifBlank { "Sin registrar" }}
            """.trimIndent()
        } ?: "Signos vitales: Sin registrar"

        // Factores de riesgo
        holder.tvFactoresRiesgo.text =
            if (atencion.factoresRiesgo.isEmpty()) {
                "Factores de riesgo: Sin registrar"
            } else {
                "Factores de riesgo:\n" +
                        atencion.factoresRiesgo.joinToString("\n") { "• $it" }
            }

        // Prescripciones médicas
        holder.tvPrescripciones.text =
            if (atencion.prescripciones.isEmpty()) {
                "Prescripciones: Sin registrar"
            } else {
                "Prescripciones:\n" +
                        atencion.prescripciones.joinToString("\n\n") { prescripcion ->
                            """
                        • ${prescripcion.medicamento}
                        Posología: ${prescripcion.posologia}
                        Duración: ${prescripcion.duracion}
                        Indicaciones: ${prescripcion.indicaciones.ifBlank { "Sin indicaciones adicionales" }}
                        """.trimIndent()
                        }
            }

        // Estudios solicitados
        holder.tvEstudios.text =
            if (atencion.estudios.isEmpty()) {
                "Estudios solicitados: Sin registrar"
            } else {
                "Estudios solicitados:\n" +
                        atencion.estudios.joinToString("\n") { estudio ->
                            "• $estudio"
                        }
            }
    }

    override fun getItemCount(): Int = atenciones.size
}


