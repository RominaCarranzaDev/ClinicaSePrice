package com.example.clinicaseprice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.clinicaseprice.repository.PacientesRepository

class NuevoPacienteActivity : BaseActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etTelefono: EditText
    private lateinit var spObraSocial: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    // -1 significa que estamos registrando un paciente nuevo.
    private var pacienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pacienteId = intent.getIntExtra("paciente_id", -1)

        configurarPantalla(
            titulo = if (pacienteId == -1) {
                "Nuevo Paciente"
            } else {
                "Editar Paciente"
            },
            subtitulo = if (pacienteId == -1) {
                "Alta e ingreso al sistema."
            } else {
                "Modificá los datos del paciente."
            },
            layoutContenido = R.layout.activity_nuevo_paciente,
            opcionActiva = "pacientes"
        )

        etNombre = findViewById(R.id.etNuevoNombre)
        etDni = findViewById(R.id.etNuevoDni)
        etTelefono = findViewById(R.id.etNuevoTelefono)
        spObraSocial = findViewById(R.id.spNuevoObraSocial)
        btnGuardar = findViewById(R.id.btnGuardarPaciente)
        btnCancelar = findViewById(R.id.btnCancelarPaciente)

        // Si recibimos un ID, cargamos los datos para editarlos.
        if (pacienteId != -1) {
            cargarDatosPaciente()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarPaciente()
        }
    }

    private fun cargarDatosPaciente() {
        val paciente = PacientesRepository.pacientes.find {
            it.id == pacienteId
        }

        if (paciente == null) {
            Toast.makeText(
                this,
                "No se encontró el paciente",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        etNombre.setText(paciente.nombreCompleto)
        etDni.setText(paciente.dni)
        etTelefono.setText(paciente.telefono)

        // Seleccionar la obra social que ya tenía.
        val opciones = spObraSocial.adapter

        for (i in 0 until opciones.count) {
            if (
                opciones.getItem(i).toString()
                    .equals(paciente.obraSocial, ignoreCase = true)
            ) {
                spObraSocial.setSelection(i)
                break
            }
        }
    }

    private fun guardarPaciente() {
        val nombre = etNombre.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val obraSocial = spObraSocial.selectedItem?.toString() ?: ""

        // Validar campos obligatorios.
        if (
            nombre.isEmpty() ||
            dni.isEmpty() ||
            telefono.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Completá todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Evitar DNI duplicados, excepto el del propio paciente editado.
        val dniExistente = PacientesRepository.pacientes.any {
            it.dni == dni && it.id != pacienteId
        }

        if (dniExistente) {
            etDni.error = "Ya existe otro paciente con ese DNI"
            return
        }

        if (pacienteId == -1) {

            // Registrar un paciente nuevo.
            PacientesRepository.agregarPaciente(
                nombre = nombre,
                dni = dni,
                telefono = telefono,
                obraSocial = obraSocial
            )

        } else {

            // Actualizar el paciente existente.
            val paciente = PacientesRepository.pacientes.find {
                it.id == pacienteId
            }

            if (paciente == null) {
                Toast.makeText(
                    this,
                    "No se encontró el paciente",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            paciente.nombreCompleto = nombre
            paciente.dni = dni
            paciente.telefono = telefono
            paciente.obraSocial = obraSocial
        }

        Toast.makeText(
            this,
            if (pacienteId == -1) {
                "Paciente registrado con éxito"
            } else {
                "Datos del paciente actualizados"
            },
            Toast.LENGTH_SHORT
        ).show()

        // Volver al listado actualizado.
        finish()
    }
}
