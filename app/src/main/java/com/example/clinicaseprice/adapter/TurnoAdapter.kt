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
    private val onAcreditar: (Turno) -> Unit
) : RecyclerView.Adapter<TurnoAdapter.TurnoViewHolder>() {

    class TurnoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

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

        val paciente = PacientesRepository.buscarPorId(
            turno.pacienteId
        )

        holder.tvPaciente.text =
            paciente?.nombreCompleto ?: "Paciente no encontrado"

        val tipoTurno = if (turno.esSobreturno) {
            "SOBRETURNO"
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
                    "$tipoTurno\n"

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

        holder.btnAcreditar.text =
            if (yaAcreditado) {
                "Paciente acreditado"
            } else {
                "Acreditar y enviar a sala de espera"
            }

        holder.btnAcreditar.isEnabled = !yaAcreditado

        holder.btnAcreditar.setOnClickListener {
            onAcreditar(turno)
        }
    }

    override fun getItemCount(): Int {
        return listaTurnos.size
    }

    fun actualizarLista(nuevaLista: List<Turno>) {
        listaTurnos.clear()
        listaTurnos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
