package com.example.clinicaseprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.R
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.PacientesRepository


class TurnoAdapter(
    private val listaTurnos: MutableList<Turno>,
    private val onModificar: (Turno) -> Unit,
    private val onCancelar: (Turno) -> Unit,
    private val onAcreditar: (Turno) -> Unit,
    private val onSeleccionar: (Turno?) -> Unit
) : RecyclerView.Adapter<TurnoAdapter.TurnoViewHolder>() {

    private var turnoSeleccionadoId: Int? = null

    class TurnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val cardTurno: View =
            itemView.findViewById(R.id.cardTurno)

        val tvPaciente: TextView =
            itemView.findViewById(R.id.tvPacienteTurno)

        val tvDetalles: TextView =
            itemView.findViewById(R.id.tvDetallesTurno)

        val btnModificar: Button =
            itemView.findViewById(R.id.btnModificarTurno)

        val btnCancelar: Button =
            itemView.findViewById(R.id.btnCancelarTurno)

        val btnAcreditar: Button =
            itemView.findViewById(R.id.btnAcreditarTurno)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TurnoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_turno, parent, false)

        return TurnoViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: TurnoViewHolder,
        position: Int
    ) {
        val turno = listaTurnos[position]
        val seleccionado = turnoSeleccionadoId == turno.id

        // Apariencia de la tarjeta seleccionada
        val densidad = holder.itemView.resources.displayMetrics.density

        holder.cardTurno.elevation = (if (seleccionado) 10f else 2f) * densidad


        // Selecciona la tarjeta
        holder.cardTurno.setOnClickListener {
            val posicionActual = holder.bindingAdapterPosition

            if (posicionActual != RecyclerView.NO_POSITION) {
                val turnoTocado = listaTurnos[posicionActual]

                turnoSeleccionadoId =
                    if (turnoSeleccionadoId == turnoTocado.id) {
                        null
                    } else {
                        turnoTocado.id
                    }

                val turnoActual = listaTurnos.find {
                    it.id == turnoSeleccionadoId
                }

                onSeleccionar(turnoActual)
                notifyDataSetChanged()
            }
        }

        // Datos del paciente
        val paciente = PacientesRepository.buscarPorId(turno.pacienteId)

        holder.tvPaciente.text = paciente?.nombreCompleto ?: "Paciente no encontrado"

        val tipoTurno = if (turno.esSobreturno) {
            "\nSOBRETURNO"
            } else {
                ""
            }

        holder.tvDetalles.text =
            "DNI: ${paciente?.dni ?: "No disponible"}\n" +
            "Médico: ${turno.nombreMedico}\n" +
            "Fecha: ${turno.fecha}\n" +
            "Horario: ${turno.horario}\n" +
            "Consultorio: ${turno.consultorio}\n" +
            "Estado: ${turno.estado}" +
            tipoTurno

        val yaAcreditado = turno.estado.equals(
            "ACREDITADO",
            ignoreCase = true
        )

        // Botón Modificar
        holder.btnModificar.isEnabled = !yaAcreditado
        holder.btnModificar.setOnClickListener {
            if (!yaAcreditado) {
                onModificar(turno)
            }
        }

        // Botón Cancelar
        holder.btnCancelar.isEnabled = !yaAcreditado
        holder.btnCancelar.setOnClickListener {
            if (!yaAcreditado) {
                onCancelar(turno)
            }
        }

        // Botón Acreditar
        holder.btnAcreditar.text =
            if (yaAcreditado) {
                "Paciente acreditado"
            } else {
                "Acreditar y enviar a sala de espera"
            }

        holder.btnAcreditar.isEnabled = !yaAcreditado
        holder.btnAcreditar.setOnClickListener {
            if (!yaAcreditado) {
                onAcreditar(turno)
            }
        }
    }

    override fun getItemCount(): Int = listaTurnos.size

    fun actualizarLista(nuevaLista: List<Turno>) {
        listaTurnos.clear()
        listaTurnos.addAll(nuevaLista)

        // Si el turno seleccionado ya no está en la lista,
        // se limpia la selección.
        if (listaTurnos.none { it.id == turnoSeleccionadoId }) {
            turnoSeleccionadoId = null
            onSeleccionar(null)
        }

        notifyDataSetChanged()
    }
}

