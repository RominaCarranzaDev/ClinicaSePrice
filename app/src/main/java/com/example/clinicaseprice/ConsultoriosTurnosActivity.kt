
package com.example.clinicaseprice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.TurnoAdapter
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.MedicosRepository
import com.example.clinicaseprice.repository.PacientesRepository
import com.example.clinicaseprice.repository.TurnosRepository

class ConsultoriosTurnosActivity : BaseActivity() {

    private lateinit var rvTurnos: RecyclerView
    private lateinit var spEstadoTurno: Spinner
    private lateinit var etBuscarTurno: EditText
    private lateinit var adaptador: TurnoAdapter

    private val todosLosTurnos = TurnosRepository.turnos

    private val estados = listOf(
        "Todos",
        "Pendiente",
        "Confirmado",
        "Acreditado"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Consultorios Externos. Turnos",
            subtitulo = "Consulta y registro de turnos y sobreturnos.",
            layoutContenido = R.layout.activity_pacientes_turnos,
            opcionActiva = "turnos"
        )

        rvTurnos = findViewById(R.id.rvTurnos)
        spEstadoTurno = findViewById(R.id.spEstadoTurno)
        etBuscarTurno = findViewById(R.id.edtBuscarPaciente)

        cargarTurnosDeEjemplo()

        rvTurnos.layoutManager = LinearLayoutManager(this)

        adaptador = TurnoAdapter(
            mutableListOf(),
            onModificar = { turno ->
                abrirFormularioTurno(turno)
            },
            onCancelar = { turno ->
                cancelarTurno(turno)
            },
            onAcreditar = { turno ->
                acreditarTurno(turno)
            }
        )

        rvTurnos.adapter = adaptador

        configurarFiltro()

        findViewById<Button>(R.id.btnNuevoTurno)
            .setOnClickListener {
                abrirFormularioTurno()
            }

        filtrarTurnos()
    }

    private fun cargarTurnosDeEjemplo() {
        if (todosLosTurnos.isNotEmpty()) return

        val ejemplos = listOf(
            Triple("38241902", "15/10/2026", "15:00"),
            Triple("29412392", "15/10/2026", "10:00"),
            Triple("34112441", "16/10/2026", "11:00")
        )

        val medicos = listOf(2, 3, 3)

        ejemplos.forEachIndexed { index, ejemplo ->
            val paciente = PacientesRepository.buscarPorDni(
                ejemplo.first
            )

            if (paciente != null) {
                try {
                    val nuevoTurno = TurnosRepository.agregarTurno(
                        dniPaciente = paciente.dni,
                        fecha = ejemplo.second,
                        horario = ejemplo.third,
                        medicoId = medicos[index]
                    )

                    nuevoTurno.estado =
                        if (index == 1) "Confirmado" else "Pendiente"

                } catch (_: IllegalArgumentException) {
                    // Si algún ejemplo no puede cargarse,
                    // la pantalla sigue funcionando.
                }
            }
        }
    }

    private fun configurarFiltro() {
        val adaptadorEstados = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados
        )

        adaptadorEstados.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spEstadoTurno.adapter = adaptadorEstados

        spEstadoTurno.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    filtrarTurnos()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        etBuscarTurno.addTextChangedListener(
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
                    filtrarTurnos()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
    }

    private fun filtrarTurnos() {
        if (!::adaptador.isInitialized) return

        val estadoSeleccionado =
            spEstadoTurno.selectedItem?.toString() ?: "Todos"

        val busqueda = etBuscarTurno.text
            .toString()
            .trim()
            .lowercase()

        val busquedaDni = busqueda.filter { it.isDigit() }

        val resultado = todosLosTurnos.filter { turno ->

            val coincideEstado =
                estadoSeleccionado == "Todos" ||
                        turno.estado.equals(
                            estadoSeleccionado,
                            ignoreCase = true
                        )

            val coincideNombre =
                turno.nombrePaciente.lowercase().contains(busqueda)

            val dniPaciente =
                turno.dniPaciente.filter { it.isDigit() }

            val coincideDni =
                busquedaDni.isNotEmpty() &&
                        dniPaciente.contains(busquedaDni)

            val coincideMedico =
                turno.nombreMedico.lowercase().contains(busqueda)

            val coincideBusqueda =
                busqueda.isEmpty() ||
                        coincideNombre ||
                        coincideDni ||
                        coincideMedico

            coincideEstado && coincideBusqueda
        }

        adaptador.actualizarLista(resultado)
    }

    private fun acreditarTurno(turno: Turno) {
        if (turno.estado == "Acreditado") {
            Toast.makeText(
                this,
                "Este paciente ya está acreditado.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        turno.estado = "Acreditado"

        filtrarTurnos()

        Toast.makeText(
            this,
            "Paciente acreditado y enviado a Sala de espera.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun abrirFormularioTurno(turno: Turno? = null) {

        val pacientesRegistrados = PacientesRepository.pacientes.toList()

        if (pacientesRegistrados.isEmpty()) {
            Toast.makeText(
                this,
                "Primero tenés que registrar un paciente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val medicosRegistrados = MedicosRepository.medicos

        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 8, 40, 0)
        }

        val spPaciente = Spinner(this)
        val spMedico = Spinner(this)

        val adaptadorPacientes = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            pacientesRegistrados.map {
                "${it.nombreCompleto} - DNI: ${it.dni}"
            }
        )

        adaptadorPacientes.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spPaciente.adapter = adaptadorPacientes

        val adaptadorMedicos = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            medicosRegistrados.map {
                "${it.nombre} - Consultorio ${it.consultorio}"
            }
        )

        adaptadorMedicos.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spMedico.adapter = adaptadorMedicos

        val tvDatosPaciente = TextView(this).apply {
            textSize = 15f
            setPadding(0, 12, 0, 16)
        }

        val tvDatosMedico = TextView(this).apply {
            textSize = 14f
            setPadding(0, 8, 0, 16)
        }

        val etFecha = crearCampo("Fecha (dd/mm/aaaa)")
        val etHorario = crearCampo("Horario (ej. 09:00)")

        val cbSobreturno = CheckBox(this).apply {
            text = "Registrar como sobreturno"
            textSize = 14f
        }

        contenedor.addView(TextView(this).apply {
            text = "Seleccionar paciente"
            textSize = 14f
        })
        contenedor.addView(spPaciente)
        contenedor.addView(tvDatosPaciente)

        contenedor.addView(TextView(this).apply {
            text = "Seleccionar médico"
            textSize = 14f
        })
        contenedor.addView(spMedico)
        contenedor.addView(tvDatosMedico)

        contenedor.addView(etFecha)
        contenedor.addView(etHorario)
        contenedor.addView(cbSobreturno)

        if (turno != null) {
            val indicePaciente = pacientesRegistrados.indexOfFirst {
                it.dni.filter { c -> c.isDigit() } ==
                        turno.dniPaciente.filter { c -> c.isDigit() }
            }

            if (indicePaciente >= 0) {
                spPaciente.setSelection(indicePaciente)
            }

            val indiceMedico = medicosRegistrados.indexOfFirst {
                it.id == turno.medicoId
            }

            if (indiceMedico >= 0) {
                spMedico.setSelection(indiceMedico)
            }

            etFecha.setText(turno.fecha)
            etHorario.setText(turno.horario)
            cbSobreturno.isChecked = turno.esSobreturno
        }

        fun actualizarDatosMedico() {
            val medico = medicosRegistrados.getOrNull(
                spMedico.selectedItemPosition
            ) ?: return

            tvDatosMedico.text =
                "Consultorio asignado: ${medico.consultorio}\n" +
                        "Disponibilidad:\n" +
                        MedicosRepository.descripcionHorarios(medico)
        }

        spPaciente.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    pacientesRegistrados.getOrNull(position)?.let {
                        mostrarDatosPaciente(tvDatosPaciente, it)
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        spMedico.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    actualizarDatosMedico()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        val titulo = if (turno == null) {
            "Asignar turno"
        } else {
            "Modificar turno"
        }

        val dialogo = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(contenedor)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val pacienteSeleccionado =
                        pacientesRegistrados.getOrNull(
                            spPaciente.selectedItemPosition
                        )

                    val medicoSeleccionado =
                        medicosRegistrados.getOrNull(
                            spMedico.selectedItemPosition
                        )

                    if (pacienteSeleccionado == null) {
                        Toast.makeText(
                            this,
                            "Seleccioná un paciente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    if (medicoSeleccionado == null) {
                        Toast.makeText(
                            this,
                            "Seleccioná un médico.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val fecha = etFecha.text.toString().trim()
                    val horario = etHorario.text.toString().trim()

                    if (fecha.isBlank() || horario.isBlank()) {
                        Toast.makeText(
                            this,
                            "Completá la fecha y el horario.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val pacienteActual = PacientesRepository.buscarPorId(
                        pacienteSeleccionado.id
                    )

                    if (pacienteActual == null) {
                        Toast.makeText(
                            this,
                            "El paciente ya no está registrado.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }

                    try {
                        if (turno == null) {
                            TurnosRepository.agregarTurno(
                                dniPaciente = pacienteActual.dni,
                                fecha = fecha,
                                horario = horario,
                                medicoId = medicoSeleccionado.id,
                                esSobreturno = cbSobreturno.isChecked
                            )

                            Toast.makeText(
                                this,
                                if (cbSobreturno.isChecked) {
                                    "Sobreturno asignado correctamente."
                                } else {
                                    "Turno asignado a ${pacienteActual.nombreCompleto}."
                                },
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            TurnosRepository.modificarTurno(
                                turno = turno,
                                dniPaciente = pacienteActual.dni,
                                fecha = fecha,
                                horario = horario,
                                medicoId = medicoSeleccionado.id,
                                esSobreturno = cbSobreturno.isChecked
                            )

                            Toast.makeText(
                                this,
                                "Turno modificado correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        filtrarTurnos()
                        dialogo.dismiss()

                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(
                            this,
                            e.message ?: "No se pudo guardar el turno.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        dialogo.show()
    }

    private fun mostrarDatosPaciente(
        vista: TextView,
        paciente: Paciente
    ) {
        vista.text = """
            Nombre: ${paciente.nombreCompleto}
            DNI: ${paciente.dni}
            Teléfono: ${paciente.telefono}
            Obra social: ${paciente.obraSocial}
        """.trimIndent()
    }

    private fun crearCampo(pista: String): EditText {
        return EditText(this).apply {
            hint = pista

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun cancelarTurno(turno: Turno) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar turno")
            .setMessage(
                "¿Querés cancelar y eliminar el turno de " +
                        "${turno.nombrePaciente}?"
            )
            .setPositiveButton("Sí, cancelar") { _, _ ->
                todosLosTurnos.remove(turno)

                filtrarTurnos()

                Toast.makeText(
                    this,
                    "Turno cancelado correctamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
