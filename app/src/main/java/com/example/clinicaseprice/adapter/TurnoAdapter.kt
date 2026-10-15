package com.example.clinicaseprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.R
import com.example.clinicaseprice.model.Turno

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

        holder.tvPaciente.text = turno.nombrePaciente

        val tipoTurno = if (turno.esSobreturno) {
            "SOBRETURNO"
        } else {
            "Turno normal"
        }

        holder.tvDetalles.text =
            "DNI: ${turno.dniPaciente}\n" +
                    "Médico: ${turno.nombreMedico}\n" +
                    "Fecha: ${turno.fecha}\n" +
                    "Horario: ${turno.horario}\n" +
                    "Consultorio: ${turno.consultorio}\n" +
                    "Tipo: $tipoTurno\n" +
                    "Estado: ${turno.estado}"

        holder.btnModificar.setOnClickListener {
            onModificar(turno)
        }

        holder.btnCancelar.setOnClickListener {
            onCancelar(turno)
        }

        val yaAcreditado = turno.estado == "Acreditado"

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
