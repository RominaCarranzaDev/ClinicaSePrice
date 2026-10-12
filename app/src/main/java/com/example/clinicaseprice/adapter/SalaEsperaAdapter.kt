
package com.example.clinicaseprice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val paciente = pacientes[position]

        holder.tvNombre.text = paciente.nombrePaciente

        holder.tvDetalles.text =
            "DNI: ${paciente.dniPaciente}\n" +
                    "Horario: ${paciente.horario}\n" +
                    "Consultorio: ${paciente.consultorio}"
    }

    override fun getItemCount(): Int = pacientes.size
    fun actualizarLista(nuevaLista: List<Turno>) {
        pacientes = nuevaLista
        notifyDataSetChanged()
    }
}
