package com.example.clinicaseprice

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.HorarioDisponibleAdapter
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.repository.HorarioDisponible
import com.example.clinicaseprice.repository.MedicosRepository
import com.example.clinicaseprice.repository.PacientesRepository
import com.example.clinicaseprice.repository.TurnosRepository
import java.text.SimpleDateFormat
import java.util.Locale

class SolicitarTurno : AppCompatActivity() {
    private lateinit var spPaciente: Spinner
    private lateinit var spEspecialidad: Spinner
    private lateinit var calendarTurnos: CalendarView
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var rvHorariosDisponibles: RecyclerView
    private lateinit var btnConfirmarTurno: Button
    private lateinit var btnVolverTurnos: Button

    private var fechaSeleccionada: String = ""
    private var pacienteIdRecibido: Int = -1

    private var horarioSeleccionado: HorarioDisponible? = null

    private var horariosDisponibles: List<HorarioDisponible> = emptyList()

    private lateinit var pacientes: List<Paciente>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_solicitar_turno)

        vincularVistas()
        configurarCalendario()
        cargarPacientes()
        cargarEspecialidades()
        configurarEventos()
    }

    private fun vincularVistas() {
        spPaciente = findViewById(R.id.spPaciente)
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
        ).format(java.util.Date())

        tvFechaSeleccionada.text =
            "Fecha seleccionada: $fechaSeleccionada"
    }



    private fun cargarPacientes() {
        pacientes = PacientesRepository.obtenerTodos()

        val nombresPacientes = pacientes.map {
            it.nombreCompleto
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresPacientes
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spPaciente.adapter = adapter

        pacienteIdRecibido = intent.getIntExtra(
            "pacienteID",
            -1
        )

        if (pacienteIdRecibido != -1) {
            val posicion = pacientes.indexOfFirst {
                it.id == pacienteIdRecibido
            }

            if (posicion >= 0) {
                spPaciente.setSelection(posicion)
            } else {
                Toast.makeText(
                    this,
                    "No se encontró el paciente recibido.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun cargarEspecialidades() {
        val especialidades = MedicosRepository.obtenerTodos()
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
                    view: android.view.View?,
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
        btnConfirmarTurno.isEnabled = false

        if (spEspecialidad.selectedItem == null) {
            limpiarHorarios()
            return
        }

        val especialidad = spEspecialidad.selectedItem.toString()

        horariosDisponibles =
            TurnosRepository.obtenerDisponibilidadPorEspecialidad(
                especialidad,
                fechaSeleccionada
            )

        rvHorariosDisponibles.adapter =
            HorarioDisponibleAdapter(horariosDisponibles) { horario ->

                horarioSeleccionado = horario
                btnConfirmarTurno.isEnabled = true
            }

        if (horariosDisponibles.isEmpty()) {
            Toast.makeText(
                this,
                "No hay horarios disponibles para esa fecha.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarHorarios() {
        horariosDisponibles = emptyList()
        horarioSeleccionado = null
        rvHorariosDisponibles.adapter =
            HorarioDisponibleAdapter(emptyList()) {}
        btnConfirmarTurno.isEnabled = false
    }

    private fun confirmarTurno() {
        if (spPaciente.selectedItemPosition < 0 ||
            pacientes.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Seleccioná un paciente.",
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

        val paciente = pacientes[spPaciente.selectedItemPosition]

        val agregado = TurnosRepository.agregarTurno(
            dniPaciente = paciente.dni,
            fecha = horario.fecha,
            horario = horario.horario,
            medicoId = horario.medico.id
        )

        actualizarHorarios()

    }
}

