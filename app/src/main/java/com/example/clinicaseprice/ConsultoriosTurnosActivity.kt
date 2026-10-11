package com.example.clinicaseprice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.TurnoAdapter

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

        // Utiliza la barra de búsqueda compartida de activity_base.xml.
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

        todosLosTurnos.addAll(
            listOf(
                Turno(
                    id = 1,
                    nombrePaciente = "Juan Pérez",
                    dniPaciente = "30123456",
                    fecha = "15/10/2026",
                    horario = "09:00",
                    consultorio = "1",
                    estado = "Pendiente"
                ),
                Turno(
                    id = 2,
                    nombrePaciente = "María Gómez",
                    dniPaciente = "28456789",
                    fecha = "15/10/2026",
                    horario = "10:30",
                    consultorio = "2",
                    estado = "Confirmado"
                ),
                Turno(
                    id = 3,
                    nombrePaciente = "Carlos López",
                    dniPaciente = "32567890",
                    fecha = "16/10/2026",
                    horario = "11:00",
                    consultorio = "1",
                    estado = "Pendiente"
                )
            )
        )
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

            val coincideBusqueda =
                busqueda.isEmpty() ||
                        coincideNombre ||
                        coincideDni

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
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 8, 40, 0)
        }

        val etNombre = crearCampo("Nombre del paciente")
        val etDni = crearCampo("DNI")
        val etFecha = crearCampo("Fecha (dd/mm/aaaa)")
        val etHorario = crearCampo("Horario (ej. 09:30)")
        val etConsultorio = crearCampo("Número de consultorio")

        contenedor.addView(etNombre)
        contenedor.addView(etDni)
        contenedor.addView(etFecha)
        contenedor.addView(etHorario)
        contenedor.addView(etConsultorio)

        if (turno != null) {
            etNombre.setText(turno.nombrePaciente)
            etDni.setText(turno.dniPaciente)
            etFecha.setText(turno.fecha)
            etHorario.setText(turno.horario)
            etConsultorio.setText(turno.consultorio)
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

                    val nombre = etNombre.text.toString().trim()
                    val dni = etDni.text.toString().trim()
                    val fecha = etFecha.text.toString().trim()
                    val horario = etHorario.text.toString().trim()
                    val consultorio =
                        etConsultorio.text.toString().trim()

                    if (
                        nombre.isEmpty() ||
                        dni.isEmpty() ||
                        fecha.isEmpty() ||
                        horario.isEmpty() ||
                        consultorio.isEmpty()
                    ) {
                        Toast.makeText(
                            this,
                            "Completá todos los campos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    if (turno == null) {
                        val nuevoId =
                            (todosLosTurnos.maxOfOrNull { it.id } ?: 0) + 1

                        todosLosTurnos.add(
                            Turno(
                                id = nuevoId,
                                nombrePaciente = nombre,
                                dniPaciente = dni,
                                fecha = fecha,
                                horario = horario,
                                consultorio = consultorio,
                                estado = "Pendiente"
                            )
                        )

                        Toast.makeText(
                            this,
                            "Turno asignado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        turno.nombrePaciente = nombre
                        turno.dniPaciente = dni
                        turno.fecha = fecha
                        turno.horario = horario
                        turno.consultorio = consultorio

                        Toast.makeText(
                            this,
                            "Turno modificado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    filtrarTurnos()
                    dialogo.dismiss()
                }
        }

        dialogo.show()
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
                "¿Querés cancelar y eliminar el turno de ${turno.nombrePaciente}?"
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
