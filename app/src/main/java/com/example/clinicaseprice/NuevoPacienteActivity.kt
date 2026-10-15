
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

    private var pacienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pacienteId = intent.getIntExtra("paciente_id", -1)

        configurarPantalla(
            titulo = if (pacienteId == -1) "Nuevo Paciente" else "Editar Paciente",
            subtitulo = if (pacienteId == -1)
                "Alta e ingreso al sistema."
            else
                "Modificá los datos del paciente.",
            layoutContenido = R.layout.activity_nuevo_paciente,
            opcionActiva = "pacientes"
        )

        etNombre = findViewById(R.id.etNuevoNombre)
        etDni = findViewById(R.id.etNuevoDni)
        etTelefono = findViewById(R.id.etNuevoTelefono)
        spObraSocial = findViewById(R.id.spNuevoObraSocial)
        btnGuardar = findViewById(R.id.btnGuardarPaciente)
        btnCancelar = findViewById(R.id.btnCancelarPaciente)

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
        val paciente = PacientesRepository.buscarPorId(pacienteId)

        if (paciente == null) {
            Toast.makeText(
                this,
                "No se encontró el paciente.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        etNombre.setText(paciente.nombreCompleto)
        etDni.setText(paciente.dni)
        etTelefono.setText(paciente.telefono)

        val adapter = spObraSocial.adapter

        for (i in 0 until adapter.count) {
            if (
                adapter.getItem(i).toString()
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
        val obraSocial = spObraSocial.selectedItem?.toString().orEmpty()

        if (nombre.isBlank() || dni.isBlank() || telefono.isBlank()) {
            Toast.makeText(
                this,
                "Completá todos los campos obligatorios.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            if (pacienteId == -1) {
                // El repositorio valida el DNI y registra el paciente.
                PacientesRepository.agregarPaciente(
                    nombre = nombre,
                    dni = dni,
                    telefono = telefono,
                    obraSocial = obraSocial
                )

                Toast.makeText(
                    this,
                    "Paciente registrado con éxito.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                // La actualización también se realiza desde el repositorio.
                val actualizado = PacientesRepository.actualizarPaciente(
                    id = pacienteId,
                    nombre = nombre,
                    dni = dni,
                    telefono = telefono,
                    obraSocial = obraSocial
                )

                if (!actualizado) {
                    Toast.makeText(
                        this,
                        "No se encontró el paciente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Toast.makeText(
                    this,
                    "Datos del paciente actualizados.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()

        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                this,
                e.message ?: "No se pudieron guardar los datos.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
