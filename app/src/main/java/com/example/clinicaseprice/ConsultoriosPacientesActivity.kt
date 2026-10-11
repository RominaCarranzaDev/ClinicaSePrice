package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.PacienteAdapter
import com.example.clinicaseprice.model.Paciente

class ConsultoriosPacientesActivity : BaseActivity() {

    private lateinit var rvPacientes: RecyclerView
    private lateinit var spEstado: Spinner
    private lateinit var spObraSocial: Spinner
    private lateinit var btnNuevoPaciente: Button

    private lateinit var adapter: PacienteAdapter
    private var listaPacientesOriginal = listOf<Paciente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Pacientes",
            subtitulo = "Consulta y registro.",
            layoutContenido = R.layout.activity_consultorios_pacientes,
            opcionActiva = "pacientes"
        )

        // 1. Vinculación de vistas del layout_contenido
        rvPacientes = findViewById(R.id.rvPacientes)
        spEstado = findViewById(R.id.spEstado)
        spObraSocial = findViewById(R.id.spObraSocial)
        btnNuevoPaciente = findViewById(R.id.btnNuevoPaciente)

        // 2. Carga de datos de prueba (idénticos al diseño)
        cargarDatosPrueba()

        // 3. Configuración del RecyclerView
        setupRecyclerView()

        // 4. Configuración de Listeners para Spinners y Buscador de la Base
        setupSpinners()
        setupBuscadorBase()

        // Acción Botón Nuevo Paciente
        btnNuevoPaciente.setOnClickListener {
            val intent = Intent(this, NuevoPacienteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosPrueba() {
        listaPacientesOriginal = listOf(
            Paciente(1, "Sofía Valentina Ma...", "38.241.902", "+54 9 11 4059-1921", "OSDE", "23 Oct 2024", "CONFIRMADO", isSelected = true),
            Paciente(2, "Juan Carlos Pérez", "29.412.392", "+54 9 11 5012-4411", "Swiss Medical", "23 Oct 2024", "PENDIENTE"),
            Paciente(3, "Amelia Ruiz", "34.112.441", "+54 9 11 3214-9922", "Galeno", "24 Oct 2024", "CONFIRMADO"),
            Paciente(4, "Andrés Miguel Silva", "41.902.122", "+54 9 11 6012-3211", "Particular", "24 Oct 2024", "CANCELADO"),
            Paciente(5, "Gabriel Ocampo", "37.521.841", "+54 9 11 2011-9944", "OSDE", "27 Oct 2024", "PENDIENTE")
        )
    }

    private fun setupRecyclerView() {
        adapter = PacienteAdapter(listaPacientesOriginal) { paciente ->
            Toast.makeText(this, "Ver ficha de: ${paciente.nombreCompleto}", Toast.LENGTH_SHORT).show()
        }
        rvPacientes.layoutManager = LinearLayoutManager(this)
        rvPacientes.adapter = adapter
    }

    private fun setupSpinners() {
        val listenerSpinners = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spEstado.onItemSelectedListener = listenerSpinners
        spObraSocial.onItemSelectedListener = listenerSpinners
    }

    private fun setupBuscadorBase() {
        // Escucha cambios en el EditText 'edtBuscarPaciente' que se encuentra en activity_base.xml
        val edtBuscarBase = findViewById<EditText>(R.id.edtBuscarPaciente)
        edtBuscarBase?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aplicarFiltros()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun aplicarFiltros() {
        val textoBusqueda = findViewById<EditText>(R.id.edtBuscarPaciente)?.text.toString().lowercase().trim()
        val estadoSel = spEstado.selectedItem?.toString()?.replace("Estado: ", "")?.trim() ?: "Todos"
        val obraSocialSel = spObraSocial.selectedItem?.toString()?.replace("Obra Social: ", "")?.trim() ?: "Todas"

        val listaFiltrada = listaPacientesOriginal.filter { paciente ->
            // Filtro por nombre o DNI
            val coincideTexto = textoBusqueda.isEmpty() ||
                    paciente.nombreCompleto.lowercase().contains(textoBusqueda) ||
                    paciente.dni.contains(textoBusqueda)

            // Filtro por Estado
            val coincideEstado = estadoSel.equals("Todos", ignoreCase = true) ||
                    paciente.estado.equals(estadoSel, ignoreCase = true)

            // Filtro por Obra Social
            val coincideOS = obraSocialSel.equals("Todas", ignoreCase = true) ||
                    paciente.obraSocial.equals(obraSocialSel, ignoreCase = true)

            coincideTexto && coincideEstado && coincideOS
        }

        adapter.actualizarLista(listaFiltrada)
    }
}