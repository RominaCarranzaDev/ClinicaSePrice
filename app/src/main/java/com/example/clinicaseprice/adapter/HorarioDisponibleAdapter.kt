package com.example.clinicaseprice.adapter

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.repository.HorarioDisponible

class HorarioDisponibleAdapter(
    private val horarios: List<HorarioDisponible>,
    private val onSeleccionar: (HorarioDisponible) -> Unit
) : RecyclerView.Adapter<HorarioDisponibleAdapter.HorarioViewHolder>() {

    private var posicionSeleccionada = RecyclerView.NO_POSITION

    class HorarioViewHolder(
        val texto: TextView
    ) : RecyclerView.ViewHolder(texto)

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): HorarioViewHolder {
        val texto = TextView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            setPadding(24, 24, 24, 24)
            textSize = 16f
            isClickable = true
            isFocusable = true
        }

        return HorarioViewHolder(texto)
    }

    override fun onBindViewHolder(
        holder: HorarioViewHolder,
        position: Int
    ) {
        val horario = horarios[position]

        holder.texto.text =
            "${horario.horario} - Dr./Dra. ${horario.medico.nombre} " +
                    "${horario.medico.apellido} - Consultorio ${horario.consultorio}"

        holder.texto.isSelected =
            position == posicionSeleccionada

        holder.texto.setBackgroundResource(
            if (position == posicionSeleccionada) {
                android.R.drawable.list_selector_background
            } else {
                android.R.drawable.btn_default
            }
        )

        holder.texto.setOnClickListener {
            val posicionAnterior = posicionSeleccionada
            posicionSeleccionada = holder.bindingAdapterPosition

            if (posicionSeleccionada == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            if (posicionAnterior != RecyclerView.NO_POSITION) {
                notifyItemChanged(posicionAnterior)
            }

            notifyItemChanged(posicionSeleccionada)

            onSeleccionar(horarios[posicionSeleccionada])
        }
    }

    override fun getItemCount(): Int = horarios.size
}
