
package com.example.clinicaseprice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.repository.TurnosRepository

class SalaEsperaActivity : BaseActivity() {

    private lateinit var rvSalaEspera: RecyclerView
    private lateinit var tvVacia: TextView
    private lateinit var edtBuscarPaciente: EditText
    private lateinit var adaptador: SalaEsperaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Sala de espera",
            subtitulo = "Pacientes acreditados pendientes de atención.",
            layoutContenido = R.layout.activity_sala_espera,
            opcionActiva = "sala_espera"
        )

        rvSalaEspera = findViewById(R.id.rvSalaEspera)
        tvVacia = findViewById(R.id.tvSalaEsperaVacia)

        // Usa la misma barra de búsqueda de activity_base.xml.
        edtBuscarPaciente = findViewById(R.id.edtBuscarPaciente)

        rvSalaEspera.layoutManager = LinearLayoutManager(this)

        adaptador = SalaEsperaAdapter(emptyList())
        rvSalaEspera.adapter = adaptador

        edtBuscarPaciente.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    filtrarPacientes()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )

        filtrarPacientes()
    }

    private fun filtrarPacientes() {
        val busqueda = edtBuscarPaciente.text
            .toString()
            .trim()
            .lowercase()

        val busquedaDni = busqueda.filter { it.isDigit() }

        val pacientesAcreditados = TurnosRepository.turnos.filter { turno ->

            val estaAcreditado = turno.estado == "Acreditado"

            val coincideNombre =
                turno.nombrePaciente.lowercase().contains(busqueda)

            val dniPaciente =
                turno.dniPaciente.filter { it.isDigit() }

            val coincideDni =
                busquedaDni.isNotEmpty() &&
                        dniPaciente.contains(busquedaDni)

            val coincideBusqueda =
                busqueda.isEmpty() ||
                        coincideNombre ||
                        coincideDni

            estaAcreditado && coincideBusqueda
        }

        adaptador = SalaEsperaAdapter(pacientesAcreditados)
        rvSalaEspera.adapter = adaptador

        tvVacia.visibility =
            if (pacientesAcreditados.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvVacia.text =
            if (busqueda.isEmpty()) {
                "No hay pacientes en sala de espera."
            } else {
                "No se encontraron pacientes con esa búsqueda."
            }
    }
   // Actualizá la sala de espera al volver a la pantalla
    override fun onResume() {
        super.onResume()

        if (::edtBuscarPaciente.isInitialized) {
            filtrarPacientes()
        }
    }
}
