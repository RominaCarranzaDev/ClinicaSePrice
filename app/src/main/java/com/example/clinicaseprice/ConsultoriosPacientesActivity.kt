
package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.PacienteAdapter
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.repository.PacientesRepository

class ConsultoriosPacientesActivity : BaseActivity() {

    private lateinit var rvPacientes: RecyclerView
    private lateinit var spObraSocial: Spinner
    private lateinit var btnNuevoPaciente: Button
    private lateinit var adapter: PacienteAdapter
    private lateinit var edtBuscarPaciente: EditText

    private val listaPacientesOriginal: List<Paciente>
        get() = PacientesRepository.obtenerTodos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurarPantalla(
            titulo = "Pacientes",
            subtitulo = "Consulta y registro.",
            layoutContenido = R.layout.activity_consultorios_pacientes,
            opcionActiva = "pacientes"
        )

        rvPacientes = findViewById(R.id.rvPacientes)
        spObraSocial = findViewById(R.id.spObraSocial)
        btnNuevoPaciente = findViewById(R.id.btnNuevoPaciente)
        edtBuscarPaciente = findViewById(R.id.edtBuscarPaciente)

        configurarRecyclerView()
        configurarSpinners()
        configurarBuscador()

        btnNuevoPaciente.setOnClickListener {
            val intent = Intent(
                this,
                NuevoPacienteActivity::class.java
            )
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            aplicarFiltros()
        }
    }

    private fun configurarRecyclerView() {
        adapter = PacienteAdapter(
            listaPacientes = listaPacientesOriginal,

            onItemClick = { paciente ->
                val intent = Intent(
                    this,
                    FichaMedicaActivity::class.java
                )
                intent.putExtra("paciente_id", paciente.id)
                startActivity(intent)
            },

            onEditClick = { paciente ->
                val intent = Intent(
                    this,
                    NuevoPacienteActivity::class.java
                )
                intent.putExtra("paciente_id", paciente.id)
                startActivity(intent)
            }
        )

        rvPacientes.layoutManager = LinearLayoutManager(this)
        rvPacientes.adapter = adapter
    }

    private fun configurarSpinners() {
        spObraSocial.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    aplicarFiltros()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                    // No se necesita realizar ninguna acción.
                }
            }
    }

    private fun configurarBuscador() {
        edtBuscarPaciente.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    aplicarFiltros()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )
    }

    private fun aplicarFiltros() {
        if (!::adapter.isInitialized) return

        val textoBusqueda = edtBuscarPaciente.text.toString()
            .lowercase()
            .trim()

        val dniBuscado = textoBusqueda.filter { it.isDigit() }

        val obraSocialSeleccionada =
            spObraSocial.selectedItem?.toString()
                ?.replace("Obra Social:", "")
                ?.trim()
                ?: "Todas"

        val listaFiltrada = listaPacientesOriginal.filter { paciente ->

            // Buscar por nombre.
            val coincideNombre =
                paciente.nombreCompleto.lowercase()
                    .contains(textoBusqueda)

            // Buscar por DNI.
            val dniPaciente = paciente.dni.filter { it.isDigit() }

            val coincideDni =
                dniBuscado.isNotEmpty() &&
                        dniPaciente.contains(dniBuscado)

            val coincideTexto =
                textoBusqueda.isEmpty() ||
                        coincideNombre ||
                        coincideDni

            // Filtrar por obra social o Particular.
            val coincideObraSocial =
                obraSocialSeleccionada.equals(
                    "Todas",
                    ignoreCase = true
                ) ||
                        obraSocialSeleccionada.equals(
                            "Obra Social",
                            ignoreCase = true
                        ) ||
                        paciente.obraSocial.equals(
                            obraSocialSeleccionada,
                            ignoreCase = true
                        )

            coincideTexto && coincideObraSocial
        }

        adapter.actualizarLista(listaFiltrada)
    }
}
