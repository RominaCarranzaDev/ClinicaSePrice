package com.example.clinicaseprice.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.clinicaseprice.model.Paciente
import java.util.Locale


class BuscadorPacienteAdapter(
    context: Context,
    private val pacientesOriginales: List<Paciente>
) : ArrayAdapter<Paciente>(
    context,
    android.R.layout.simple_dropdown_item_1line,
    pacientesOriginales.toMutableList()
) {

    fun textoPaciente(paciente: Paciente): String {
        return "${paciente.nombreCompleto} - DNI: ${paciente.dni}"
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val vista = super.getView(
            position,
            convertView,
            parent
        ) as TextView

        vista.text = textoPaciente(getItem(position)!!)
        return vista
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val vista = super.getDropDownView(
            position,
            convertView,
            parent
        ) as TextView

        vista.text = textoPaciente(getItem(position)!!)
        return vista
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(
                constraint: CharSequence?
            ): FilterResults {

                val consulta = constraint
                    ?.toString()
                    ?.trim()
                    .orEmpty()

                val consultaNombre =
                    consulta.lowercase(Locale.getDefault())

                val consultaDni = consulta.filter {
                    it.isDigit()
                }

                val coincidencias =
                    if (consulta.isBlank()) {
                        emptyList()
                    } else {
                        pacientesOriginales.filter { paciente ->

                            val nombre = paciente.nombreCompleto
                                .lowercase(Locale.getDefault())

                            val dni = paciente.dni
                                .toString()
                                .filter { it.isDigit() }

                            nombre.contains(consultaNombre) ||
                                    (
                                            consultaDni.isNotEmpty() &&
                                                    dni.contains(consultaDni)
                                            )
                        }
                    }

                return FilterResults().apply {
                    values = coincidencias
                    count = coincidencias.size
                }
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                clear()

                @Suppress("UNCHECKED_CAST")
                val coincidencias =
                    results?.values as? List<Paciente>
                        ?: emptyList()

                addAll(coincidencias)
                notifyDataSetChanged()
            }

            override fun convertResultToString(
                resultValue: Any
            ): CharSequence {
                return textoPaciente(resultValue as Paciente)
            }
        }
    }
}
