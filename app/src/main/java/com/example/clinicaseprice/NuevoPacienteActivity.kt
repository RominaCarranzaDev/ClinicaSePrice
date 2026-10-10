package com.example.clinicaseprice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

class NuevoPacienteActivity : BaseActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etTelefono: EditText
    private lateinit var spObraSocial: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Nuevo Paciente",
            subtitulo = "Alta e ingreso al sistema.",
            layoutContenido = R.layout.activity_nuevo_paciente,
            opcionActiva = "pacientes"
        )

        etNombre = findViewById(R.id.etNuevoNombre)
        etDni = findViewById(R.id.etNuevoDni)
        etTelefono = findViewById(R.id.etNuevoTelefono)
        spObraSocial = findViewById(R.id.spNuevoObraSocial)
        btnGuardar = findViewById(R.id.btnGuardarPaciente)
        btnCancelar = findViewById(R.id.btnCancelarPaciente)

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarPaciente()
        }
    }

    private fun guardarPaciente() {
        val nombre = etNombre.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val obraSocial = spObraSocial.selectedItem.toString()

        if (nombre.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Paciente registrado con éxito", Toast.LENGTH_SHORT).show()
        finish()
    }
}