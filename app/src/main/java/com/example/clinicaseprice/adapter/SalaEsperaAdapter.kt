package com.example.clinicaseprice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.PacientesRepository

class SalaEsperaAdapter(
    private var pacientes: List<Turno>
) : RecyclerView.Adapter<SalaEsperaAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNombre: TextView =
            itemView.findViewById(R.id.tvNombreSalaEspera)

        val tvDetalles: TextView =
            itemView.findViewById(R.id.tvDetallesSalaEspera)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacienteViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sala_espera, parent, false)

        return PacienteViewHolder(vista)
    }


    override fun onBindViewHolder(
        holder: PacienteViewHolder,
        position: Int
    ) {
        val turno = pacientes[position]

        val paciente = PacientesRepository.buscarPorId(
            turno.pacienteId
        )

        holder.tvNombre.text =
            paciente?.nombreCompleto ?: "Paciente no encontrado"

        holder.tvDetalles.text =
            "DNI: ${paciente?.dni ?: "No disponible"}\n" +
                    "Horario: ${turno.horario}\n" +
                    "Consultorio: ${turno.consultorio}"
    }


    override fun getItemCount(): Int = pacientes.size
    fun actualizarLista(nuevaLista: List<Turno>) {
        pacientes = nuevaLista
        notifyDataSetChanged()
    }
}
