package com.example.clinicaseprice.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
    private val onEstadoClick: (Paciente) -> Unit = {},
    private val onEditClick: (Paciente) -> Unit = {}
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDni: TextView = itemView.findViewById(R.id.tvDni)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvObraSocial: TextView = itemView.findViewById(R.id.tvObraSocial)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)

        val btnFicha: ImageButton = itemView.findViewById(R.id.btnFicha)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
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

        // Mostrar únicamente el dato correspondiente a cada columna.
        holder.tvNombre.text = paciente.nombreCompleto
        holder.tvDni.text = paciente.dni
        holder.tvTelefono.text = paciente.telefono
        holder.tvObraSocial.text = paciente.obraSocial
        holder.tvEstado.text = paciente.estado.uppercase()

        // Colores diferentes según el estado.
        val (colorTexto, colorFondo) = when (
            paciente.estado.trim().uppercase()
        ) {
            "CONFIRMADO" -> Pair(
                Color.parseColor("#166534"),
                Color.parseColor("#DCFCE7")
            )

            "CANCELADO" -> Pair(
                Color.parseColor("#991B1B"),
                Color.parseColor("#FEE2E2")
            )

            "PENDIENTE" -> Pair(
                Color.parseColor("#92400E"),
                Color.parseColor("#FEF3C7")
            )

            "ACTIVO" -> Pair(
                Color.parseColor("#166534"),
                Color.parseColor("#DCFCE7")
            )

            "INACTIVO" -> Pair(
                Color.parseColor("#991B1B"),
                Color.parseColor("#FEE2E2")
            )

            else -> Pair(
                Color.parseColor("#475569"),
                Color.parseColor("#E2E8F0")
            )
        }

        holder.tvEstado.setTextColor(colorTexto)

        // Mantener la forma redondeada de la etiqueta.
        val fondo = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f
            setColor(colorFondo)
        }
        holder.tvEstado.background = fondo

        // Acciones independientes de cada control.
        holder.tvEstado.setOnClickListener {
            onEstadoClick(paciente)
        }

        holder.btnFicha.setOnClickListener {
            onItemClick(paciente)
        }

        holder.btnEditar.setOnClickListener {
            onEditClick(paciente)
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