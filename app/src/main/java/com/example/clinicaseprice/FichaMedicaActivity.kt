
package com.example.clinicaseprice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.clinicaseprice.repository.PacientesRepository

class FichaMedicaActivity : BaseActivity() {

    private var pacienteId: Int = -1

    private lateinit var tvDatosPaciente: TextView
    private lateinit var etAntecedentes: EditText
    private lateinit var etAlergias: EditText
    private lateinit var etMedicacion: EditText
    private lateinit var etObservaciones: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Ficha médica",
            subtitulo = "Información del paciente.",
            layoutContenido = R.layout.activity_ficha_medica,
            opcionActiva = "pacientes"
        )

        pacienteId = intent.getIntExtra("paciente_id", -1)

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

        tvDatosPaciente = findViewById(R.id.tvDatosPacienteFicha)
        etAntecedentes = findViewById(R.id.etAntecedentes)
        etAlergias = findViewById(R.id.etAlergias)
        etMedicacion = findViewById(R.id.etMedicacion)
        etObservaciones = findViewById(R.id.etObservaciones)
        btnGuardar = findViewById(R.id.btnGuardarFicha)

        tvDatosPaciente.text =
            "Nombre: ${paciente.nombreCompleto}\n" +
                    "DNI: ${paciente.dni}\n" +
                    "Teléfono: ${paciente.telefono}\n" +
                    "Obra social: ${paciente.obraSocial}\n" +
                    "Estado: ${paciente.estado}"

        cargarFicha()

        btnGuardar.setOnClickListener {
            guardarFicha()
        }
    }

    private fun cargarFicha() {
        val preferencias = getSharedPreferences(
            "fichas_medicas",
            MODE_PRIVATE
        )

        etAntecedentes.setText(
            preferencias.getString("${pacienteId}_antecedentes", "")
        )

        etAlergias.setText(
            preferencias.getString("${pacienteId}_alergias", "")
        )

        etMedicacion.setText(
            preferencias.getString("${pacienteId}_medicacion", "")
        )

        etObservaciones.setText(
            preferencias.getString("${pacienteId}_observaciones", "")
        )
    }

    private fun guardarFicha() {
        val preferencias = getSharedPreferences(
            "fichas_medicas",
            MODE_PRIVATE
        )

        preferencias.edit()
            .putString(
                "${pacienteId}_antecedentes",
                etAntecedentes.text.toString().trim()
            )
            .putString(
                "${pacienteId}_alergias",
                etAlergias.text.toString().trim()
            )
            .putString(
                "${pacienteId}_medicacion",
                etMedicacion.text.toString().trim()
            )
            .putString(
                "${pacienteId}_observaciones",
                etObservaciones.text.toString().trim()
            )
            .apply()

        Toast.makeText(
            this,
            "Ficha médica guardada",
            Toast.LENGTH_SHORT
        ).show()
    }
}
