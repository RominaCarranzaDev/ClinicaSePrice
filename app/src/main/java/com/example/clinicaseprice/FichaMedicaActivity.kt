package com.example.clinicaseprice

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.PacientesRepository
import com.example.clinicaseprice.repository.TurnosRepository
import java.text.SimpleDateFormat
import java.util.Locale

class FichaMedicaActivity : AppCompatActivity() {

    private var pacienteId: Int = -1
    private var modoEdicionInicial: Boolean = false

    private lateinit var paciente: Paciente

    private lateinit var tvNumeroHistoriaClinica: TextView

    private lateinit var etNombreCompleto: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDomicilio: EditText
    private lateinit var etObraSocial: EditText
    private lateinit var etNumeroObraSocial: EditText
    private lateinit var rvTurnosPaciente: RecyclerView
    private lateinit var tvSinTurnos: TextView
    private lateinit var btnModificar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnVolver: Button

    private lateinit var adaptadorTurnos: TurnosPacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ficha_medica)

        pacienteId = intent.getIntExtra("paciente_id", -1)

        vincularVistas()

        val pacienteEncontrado =
            PacientesRepository.buscarPorId(pacienteId)

        if (pacienteEncontrado == null) {
            Toast.makeText(
                this,
                "No se encontró el paciente.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        paciente = pacienteEncontrado

        configurarTurnos()
        cargarDatos()
        configurarBotones()

        cambiarModoEdicion(modoEdicionInicial)
    }

    private fun vincularVistas() {
        tvNumeroHistoriaClinica =
            findViewById(R.id.tvNumeroHistoriaClinica)

        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        etDni = findViewById(R.id.etDni)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        etDomicilio = findViewById(R.id.etDomicilio)
        etObraSocial = findViewById(R.id.etObraSocial)
        etNumeroObraSocial = findViewById(R.id.etNumeroObraSocial)

        rvTurnosPaciente = findViewById(R.id.rvTurnosPaciente)
        tvSinTurnos = findViewById(R.id.tvSinTurnos)

        btnModificar = findViewById(R.id.btnModificar)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnVolver = findViewById(R.id.btnVolver)
    }

    private fun cargarDatos() {
        tvNumeroHistoriaClinica.text =
            paciente.numeroHistoriaClinica.ifBlank {
                "Sin número asignado"
            }

        etNombreCompleto.setText(paciente.nombreCompleto)
        etDni.setText(paciente.dni)
        etFechaNacimiento.setText(paciente.fechaNacimiento)
        etTelefono.setText(paciente.telefono)
        etEmail.setText(paciente.email)
        etDomicilio.setText(paciente.domicilio)
        etObraSocial.setText(paciente.obraSocial)
        etNumeroObraSocial.setText(paciente.numeroObraSocial)
    }

    private fun configurarBotones() {
        btnModificar.setOnClickListener {
            cambiarModoEdicion(true)
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnCancelar.setOnClickListener {
            cargarDatos()
            cambiarModoEdicion(false)
        }

        btnVolver.setOnClickListener {
            finish()
        }

    }

    private fun cambiarModoEdicion(activo: Boolean) {
        val campos = listOf(
            etNombreCompleto,
            etDni,
            etFechaNacimiento,
            etTelefono,
            etEmail,
            etDomicilio,
            etObraSocial,
            etNumeroObraSocial
        )

        campos.forEach { campo ->
            campo.isEnabled = activo
            campo.isFocusable = activo
            campo.isFocusableInTouchMode = activo
        }

        btnModificar.visibility =
            if (activo) View.GONE else View.VISIBLE

        btnVolver.visibility =
            if (activo) View.GONE else View.VISIBLE

        btnGuardar.visibility =
            if (activo) View.VISIBLE else View.GONE

        btnCancelar.visibility =
            if (activo) View.VISIBLE else View.GONE
    }

    private fun guardarCambios() {
        val nombreCompleto =
            etNombreCompleto.text.toString().trim()

        val dni = etDni.text.toString().trim()
        val fechaNacimiento =
            etFechaNacimiento.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val domicilio = etDomicilio.text.toString().trim()
        val obraSocial = etObraSocial.text.toString().trim()
        val numeroObraSocial =
            etNumeroObraSocial.text.toString().trim()

        if (nombreCompleto.isBlank() || dni.isBlank()) {
            Toast.makeText(
                this,
                "El nombre y el DNI son obligatorios.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Actualizamos el objeto del paciente.
        paciente.nombreCompleto = nombreCompleto
        paciente.dni = dni
        paciente.fechaNacimiento = fechaNacimiento
        paciente.telefono = telefono
        paciente.email = email
        paciente.domicilio = domicilio
        paciente.obraSocial = obraSocial
        paciente.numeroObraSocial = numeroObraSocial

        val partesNombre = nombreCompleto.split("\\s+".toRegex(), limit = 2)
        paciente.nombre = partesNombre.firstOrNull().orEmpty()
        paciente.apellido = partesNombre.getOrNull(1).orEmpty()

        PacientesRepository.actualizarPaciente(
            paciente.id,
            paciente.nombre,
            paciente.dni,
            paciente.telefono,
            paciente.obraSocial,
            paciente.apellido,
            paciente.fechaNacimiento,
            paciente.domicilio,
            paciente.email,
            paciente.numeroObraSocial)

        Toast.makeText(
            this,
            "Ficha actualizada correctamente.",
            Toast.LENGTH_SHORT
        ).show()

        cargarDatos()
        cambiarModoEdicion(false)
    }

    private fun configurarTurnos() {
        adaptadorTurnos = TurnosPacienteAdapter(emptyList())

        rvTurnosPaciente.layoutManager =
            LinearLayoutManager(this)

        rvTurnosPaciente.adapter = adaptadorTurnos
        rvTurnosPaciente.isNestedScrollingEnabled = false

        cargarTurnosPaciente()
    }

    private fun cargarTurnosPaciente() {
        val formatoFecha =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val turnosPaciente = TurnosRepository.obtenerTodos()
            .filter { turno ->
                turno.pacienteId == paciente.id
            }
            .sortedWith(
                compareBy<Turno> {
                    try {
                        formatoFecha.parse(it.fecha)?.time
                            ?: Long.MAX_VALUE
                    } catch (_: Exception) {
                        Long.MAX_VALUE
                    }
                }.thenBy { it.horario }
            )

        val hayTurnos = turnosPaciente.isNotEmpty()

        tvSinTurnos.visibility =
            if (hayTurnos) View.GONE else View.VISIBLE

        rvTurnosPaciente.visibility =
            if (hayTurnos) View.VISIBLE else View.GONE

        adaptadorTurnos.actualizarLista(turnosPaciente)
    }

    // Adaptador de solo lectura para los turnos de esta ficha.
    private class TurnosPacienteAdapter(
        private var turnos: List<Turno>
    ) : RecyclerView.Adapter<TurnosPacienteAdapter.TurnoViewHolder>() {

        class TurnoViewHolder(
            val contenedor: LinearLayout,
            val titulo: TextView,
            val detalles: TextView
        ) : RecyclerView.ViewHolder(contenedor)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TurnoViewHolder {
            val contexto = parent.context

            val contenedor = LinearLayout(contexto).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(
                    android.graphics.Color.rgb(244, 246, 249)
                )
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val titulo = TextView(contexto).apply {
                textSize = 17f
                setTextColor(
                    android.graphics.Color.rgb(32, 42, 68)
                )
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val detalles = TextView(contexto).apply {
                textSize = 14f
                setTextColor(
                    android.graphics.Color.rgb(70, 81, 102)
                )
                setPadding(0, 8, 0, 0)
                gravity = Gravity.START
            }

            contenedor.addView(titulo)
            contenedor.addView(detalles)

            return TurnoViewHolder(contenedor, titulo, detalles)
        }

        override fun onBindViewHolder(
            holder: TurnoViewHolder,
            position: Int
        ) {
            val turno = turnos[position]

            holder.titulo.text = "Turno #${turno.id}"

            holder.detalles.text =
                "Fecha: ${turno.fecha}\n" +
                        "Horario: ${turno.horario}\n" +
                        "Médico: ${turno.nombreMedico}\n" +
                        "Consultorio: ${turno.consultorio}\n" +
                        "Tipo: ${if (turno.esSobreturno) "Sobreturno" else "Normal"}\n" +
                        "Estado: ${turno.estado}"
        }

        override fun getItemCount(): Int = turnos.size

        fun actualizarLista(nuevosTurnos: List<Turno>) {
            turnos = nuevosTurnos
            notifyDataSetChanged()
        }
    }
}

