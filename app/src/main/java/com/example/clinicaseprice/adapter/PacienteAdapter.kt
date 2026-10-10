package com.example.clinicaseprice.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.R
import com.example.clinicaseprice.model.Paciente

class PacienteAdapter(
    private var listaPacientes: List<Paciente>,
    private val onItemClick: (Paciente) -> Unit
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val containerFila: LinearLayout = view.findViewById(R.id.containerFila)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)
        val tvObraSocial: TextView = view.findViewById(R.id.tvObraSocial)
        val tvUltimoTurno: TextView = view.findViewById(R.id.tvUltimoTurno)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnFicha: ImageButton = view.findViewById(R.id.btnFicha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = listaPacientes[position]

        holder.tvNombre.text = paciente.nombreCompleto
        holder.tvDni.text = paciente.dni
        holder.tvTelefono.text = paciente.telefono
        holder.tvObraSocial.text = paciente.obraSocial
        holder.tvUltimoTurno.text = paciente.ultimoTurno
        holder.tvEstado.text = paciente.estado

        if (paciente.isSelected) {
            holder.containerFila.setBackgroundColor(Color.parseColor("#E0F2FE"))
        } else {
            holder.containerFila.setBackgroundColor(Color.TRANSPARENT)
        }

        when (paciente.estado.uppercase()) {
            "CONFIRMADO" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#15803D"))
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_confirmado)
            }
            "PENDIENTE" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#B45309"))
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_pendiente)
            }
            "CANCELADO" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#B91C1C"))
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_cancelado)
            }
        }

        holder.btnFicha.setOnClickListener { onItemClick(paciente) }
    }

    override fun getItemCount(): Int = listaPacientes.size

    fun actualizarLista(nuevaLista: List<Paciente>) {
        this.listaPacientes = nuevaLista
        notifyDataSetChanged()
    }
}