package com.example.clinicaseprice

import android.app.DatePickerDialog
import java.util.Calendar
import android.content.Intent
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.TurnoAdapter
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.MedicosRepository
import com.example.clinicaseprice.repository.PacientesRepository
import com.example.clinicaseprice.repository.TurnosRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class ConsultoriosTurnosActivity : BaseActivity() {

    private lateinit var rvTurnos: RecyclerView
    private lateinit var spEstadoTurno: Spinner
    private lateinit var etBuscarTurno: EditText
    private lateinit var adaptador: TurnoAdapter
    private var paciente: Paciente? = null
    private var turnoSeleccionado: Turno? = null

    // La Activity consulta los turnos desde el repositorio central.
    private fun obtenerTurnos(): List<Turno> {
        return TurnosRepository.obtenerTodos()
    }

    private val estados = listOf(
        "Todos",
        "PENDIENTE",
        "CONFIRMADO",
        "ACREDITADO",
        "CANCELADO"
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

        val btnNuevoTurno = findViewById<Button>(R.id.btnNuevoTurno)

        rvTurnos.layoutManager = GridLayoutManager(this, 2)


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
            },

            onSeleccionar = { turno ->
                turnoSeleccionado = turno
            }
        )

        rvTurnos.adapter = adaptador


        configurarFiltro()

        btnNuevoTurno.setOnClickListener {
            val intent = Intent(this, SolicitarTurnoActivity::class.java)
            startActivity(intent)
            }

        filtrarTurnos()
    }


    private fun obtenerFechaHoy(): String {
        return SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())
    }

    // Configura el filtro por estado, nombre, DNI o médico.
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
                ) {
                }
            }

        etBuscarTurno.addTextChangedListener(
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
                    filtrarTurnos()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )
    }

    // Obtiene la lista centralizada y aplica los filtros de la pantalla.

    private fun filtrarTurnos() {
        if (!::adaptador.isInitialized) return

        val estadoSeleccionado =
            spEstadoTurno.selectedItem?.toString() ?: "Todos"

        val busqueda = etBuscarTurno.text
            .toString()
            .trim()
            .lowercase()

        val busquedaDni = busqueda.filter { it.isDigit() }

        val fechaHoy = obtenerFechaHoy()

        val resultado = obtenerTurnos().filter { turno ->

            // Mostrar únicamente los turnos de hoy para acreditar solo quienes se atienden en fecha actual
            val coincideFecha = turno.fecha == fechaHoy

            val coincideEstado =
                estadoSeleccionado == "Todos" ||
                        turno.estado.equals(
                        estadoSeleccionado,
                        ignoreCase = true
                    )

            // Buscar el paciente mediante su ID.
            paciente = PacientesRepository.buscarPorId(
                turno.pacienteId
            )

            val nombrePaciente =
                paciente?.nombreCompleto.orEmpty().lowercase()

            val dniPaciente =
                paciente?.dni.orEmpty().filter { it.isDigit() }

            val coincideNombre =
                nombrePaciente.contains(busqueda)

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

            coincideFecha && coincideEstado && coincideBusqueda
        }

        adaptador.actualizarLista(resultado)
    }


    // El cambio de estado se solicita al repositorio.
    private fun acreditarTurno(turno: Turno) {
        if (turno.estado.equals("ACREDITADO", ignoreCase = true)) {
            Toast.makeText(
                this,
                "Este paciente ya está acreditado.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val actualizado = TurnosRepository.actualizarEstado(
            id = turno.id,
            nuevoEstado = "ACREDITADO"
        )

        if (actualizado) {
            filtrarTurnos()

            Toast.makeText(
                this,
                "Paciente acreditado y enviado a Sala de espera.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "No se encontró el turno.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Formulario para registrar un turno o modificar uno existente.
    private fun abrirFormularioTurno(turno: Turno? = null) {

        val pacientesRegistrados = PacientesRepository.obtenerTodos()

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
                "${it.nombre} - Consultorio ${it.consultorioAsignado.numero}"
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

        if (turno != null) {

            etFecha.isFocusable = false
            etFecha.isClickable = true
            etFecha.hint = "Seleccionar fecha"

            etFecha.setOnClickListener {

                val calendario = Calendar.getInstance()

                try {
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formato.isLenient = false

                    val fechaActual = formato.parse(etFecha.text.toString())

                    if (fechaActual != null) {
                        calendario.time = fechaActual
                    }
                } catch (e: Exception) {

                }

                val selectorFecha = DatePickerDialog(
                    this,
                    { _, anio, mes, dia ->

                        val fechaSeleccionada = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dia,
                            mes + 1,
                            anio
                        )

                        etFecha.setText(fechaSeleccionada)
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                )

                selectorFecha.show()
            }
        }

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
                it.id == turno.pacienteId
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
                "Consultorio asignado: ${medico.consultorioAsignado.numero}\n" +
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
                ) {
                }
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
                ) {
                }
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

                    // Se vuelve a consultar el paciente por su ID.
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
                            // El repositorio realiza la modificación.
                            val turnoActual = TurnosRepository.buscarPorId(
                                turno.id
                            )

                            if (turnoActual == null) {
                                Toast.makeText(
                                    this,
                                    "No se encontró el turno.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }

                            TurnosRepository.modificarTurno(
                                turno = turnoActual,
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

    // Muestra información tomada del objeto Paciente del repositorio.
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

    // La cancelación se solicita al repositorio.
    private fun cancelarTurno(turno: Turno) {
        val paciente = PacientesRepository.buscarPorId(
            turno.pacienteId
        )

        val nombrePaciente =
            paciente?.nombreCompleto ?: "paciente"

        AlertDialog.Builder(this)
            .setTitle("Cancelar turno")
            .setMessage(
                "¿Querés cancelar el turno de $nombrePaciente?"
            )
            .setPositiveButton("Sí, cancelar") { _, _ ->

                val cancelado =
                    TurnosRepository.cancelarTurno(turno.id)

                if (cancelado) {
                    filtrarTurnos()

                    Toast.makeText(
                        this,
                        "Turno cancelado correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "No se encontró el turno.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

}
