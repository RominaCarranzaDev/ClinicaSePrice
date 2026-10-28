package com.example.clinicaseprice

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.HorarioDisponibleAdapter
import com.example.clinicaseprice.adapter.BuscadorPacienteAdapter
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.repository.HorarioDisponible
import com.example.clinicaseprice.repository.MedicosRepository
import com.example.clinicaseprice.repository.PacientesRepository
import com.example.clinicaseprice.repository.TurnosRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitarTurnoActivity : AppCompatActivity() {
    private lateinit var spEspecialidad: Spinner
    private lateinit var calendarTurnos: CalendarView
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var rvHorariosDisponibles: RecyclerView
    private lateinit var btnConfirmarTurno: Button
    private lateinit var btnVolverTurnos: Button
    private lateinit var actvPaciente: AutoCompleteTextView
    private lateinit var pacientes: List<Paciente>
    private lateinit var adapterPacientes: BuscadorPacienteAdapter

    private var pacienteSeleccionado: Paciente? = null
    private var fechaSeleccionada: String = ""
    private var horarioSeleccionado: HorarioDisponible? = null
    private var horariosDisponibles: List<HorarioDisponible> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_solicitar_turno)

        vincularVistas()
        cargarPacientes()
        configurarCalendario()
        cargarEspecialidades()
        configurarEventos()

        actualizarHorarios()
    }

    private fun vincularVistas() {
        actvPaciente = findViewById(R.id.actvPaciente)
        spEspecialidad = findViewById(R.id.spEspecialidad)
        calendarTurnos = findViewById(R.id.calendarTurnos)
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada)
        rvHorariosDisponibles = findViewById(R.id.rvHorariosDisponibles)
        btnConfirmarTurno = findViewById(R.id.btnConfirmarTurno)
        btnVolverTurnos = findViewById(R.id.btnVolverTurnos)

        rvHorariosDisponibles.layoutManager =
            LinearLayoutManager(this)

        btnConfirmarTurno.isEnabled = false
    }

    private fun configurarCalendario() {
        val hoy = Calendar.getInstance()

        calendarTurnos.minDate = hoy.timeInMillis

        fechaSeleccionada = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        tvFechaSeleccionada.text =
            "Fecha seleccionada: $fechaSeleccionada"
    }



    private fun cargarPacientes() {
        pacientes = PacientesRepository.obtenerTodos()

        adapterPacientes = BuscadorPacienteAdapter(
            this,
            pacientes
        )

        actvPaciente.setAdapter(adapterPacientes)
        actvPaciente.threshold = 1

        actvPaciente.setOnClickListener {
            actvPaciente.showDropDown()
        }

        // Capturar el paciente elegido del desplegable.
        actvPaciente.setOnItemClickListener {
                parent, _, position, _ ->

            val paciente = parent.getItemAtPosition(position)
                    as? Paciente

            if (paciente != null) {
                pacienteSeleccionado = paciente

                actvPaciente.setText(
                    adapterPacientes.textoPaciente(paciente),
                    false
                )

                actualizarEstadoConfirmar()
            }
        }

        // Si el usuario modifica el texto, debe volver
        // a seleccionar una coincidencia válida.
        actvPaciente.addTextChangedListener(
            object : android.text.TextWatcher {

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
                    val texto = s?.toString().orEmpty()

                    val textoSeleccionado =
                        pacienteSeleccionado?.let {
                            adapterPacientes.textoPaciente(it)
                        }

                    if (texto != textoSeleccionado) {
                        pacienteSeleccionado = null
                    }

                    actualizarEstadoConfirmar()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )
    }

    private fun cargarEspecialidades() {
        val especialidades = listOf("Seleccioná una especialidad") +
                MedicosRepository.obtenerTodos()
                    .map { it.especialidad }
                    .distinct()
                    .sorted()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            especialidades
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spEspecialidad.adapter = adapter
    }


    private fun configurarEventos() {

        calendarTurnos.setOnDateChangeListener {
                _, year, month, dayOfMonth ->

            val calendario = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            fechaSeleccionada = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(calendario.time)

            tvFechaSeleccionada.text =
                "Fecha seleccionada: $fechaSeleccionada"

            actualizarHorarios()
        }

        spEspecialidad.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    actualizarHorarios()
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                    limpiarHorarios()
                }
            }

        btnConfirmarTurno.setOnClickListener {
            confirmarTurno()
        }

        btnVolverTurnos.setOnClickListener {
            finish()
        }
    }

    private fun actualizarHorarios() {
        horarioSeleccionado = null

        if (spEspecialidad.selectedItemPosition <= 0) {
            limpiarHorarios()
            return
        }

        val especialidad =
            spEspecialidad.selectedItem.toString()

        horariosDisponibles =
            TurnosRepository.obtenerDisponibilidadPorEspecialidad(
                especialidad,
                fechaSeleccionada
            )

        rvHorariosDisponibles.adapter =
            HorarioDisponibleAdapter(horariosDisponibles) { horario ->

                horarioSeleccionado = horario
                actualizarEstadoConfirmar()
            }

        if (horariosDisponibles.isEmpty()) {
            Toast.makeText(
                this,
                "No hay horarios disponibles para esa fecha.",
                Toast.LENGTH_SHORT
            ).show()
        }

        actualizarEstadoConfirmar()
    }

    private fun limpiarHorarios() {
        horariosDisponibles = emptyList()
        horarioSeleccionado = null

        rvHorariosDisponibles.adapter =
            HorarioDisponibleAdapter(emptyList()) {}

        actualizarEstadoConfirmar()
    }

    private fun actualizarEstadoConfirmar() {
        val pacienteValido = pacienteSeleccionado != null

        val especialidadValida =
            spEspecialidad.selectedItemPosition > 0

        val horarioValido = horarioSeleccionado != null

        btnConfirmarTurno.isEnabled =
            pacienteValido &&
                    especialidadValida &&
                    horarioValido
    }

    private fun confirmarTurno() {
        val paciente = pacienteSeleccionado

        if (paciente == null) {
            Toast.makeText(
                this,
                "Buscá y seleccioná un paciente existente.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Comprobar que el paciente siga existiendo.
        val pacienteExistente =
            PacientesRepository.buscarPorId(paciente.id)

        if (pacienteExistente == null) {
            pacienteSeleccionado = null
            actualizarEstadoConfirmar()

            Toast.makeText(
                this,
                "El paciente ya no existe en el registro.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (spEspecialidad.selectedItemPosition <= 0) {
            Toast.makeText(
                this,
                "Seleccioná una especialidad.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val horario = horarioSeleccionado

        if (horario == null) {
            Toast.makeText(
                this,
                "Seleccioná un horario disponible.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val agregado = TurnosRepository.agregarTurno(
            dniPaciente = pacienteExistente.dni,
            fecha = horario.fecha,
            horario = horario.horario,
            medicoId = horario.medico.id
        )

        if (agregado != null ) {
            Toast.makeText(
                this,
                "Turno solicitado correctamente.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        } else {
            Toast.makeText(
                this,
                "No se pudo crear el turno. Verificá la disponibilidad.",
                Toast.LENGTH_LONG
            ).show()

            actualizarHorarios()
        }
    }
}

