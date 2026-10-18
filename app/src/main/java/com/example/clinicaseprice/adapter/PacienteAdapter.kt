
package com.example.clinicaseprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.R
import com.example.clinicaseprice.model.Paciente

class PacienteAdapter(
    private var listaPacientes: List<Paciente>,
    private val onItemClick: (Paciente) -> Unit,
    private val onEditClick: (Paciente) -> Unit = {}
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDni: TextView = itemView.findViewById(R.id.tvDni)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvObraSocial: TextView = itemView.findViewById(R.id.tvObraSocial)

        val btnFicha: ImageButton = itemView.findViewById(R.id.btnFicha)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente, parent, false)

        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PacienteViewHolder,
        position: Int
    ) {
        val paciente = listaPacientes[position]

        holder.tvNombre.text = paciente.nombreCompleto
        holder.tvDni.text = paciente.dni
        holder.tvTelefono.text = paciente.telefono
        holder.tvObraSocial.text = paciente.obraSocial

        holder.btnFicha.setOnClickListener {
            onItemClick(paciente)
        }

        holder.itemView.setOnClickListener {
            onItemClick(paciente)
        }
    }

    override fun getItemCount(): Int = listaPacientes.size

    fun actualizarLista(nuevaLista: List<Paciente>) {
        listaPacientes = nuevaLista
        notifyDataSetChanged()
    }
}
