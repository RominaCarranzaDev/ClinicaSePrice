package com.example.clinicaseprice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.clinicaseprice.repository.PacientesRepository

class NuevoPacienteActivity : BaseActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDomicilio: EditText
    private lateinit var etEmail: EditText
    private lateinit var etNumeroObraSocial: EditText

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
                "Modificá los datos personales del paciente.",
            layoutContenido = R.layout.activity_nuevo_paciente,
            opcionActiva = "pacientes"
        )

        etNombre = findViewById(R.id.etNuevoNombre)
        etApellido = findViewById(R.id.etNuevoApellido)
        etDni = findViewById(R.id.etNuevoDni)
        etFechaNacimiento = findViewById(R.id.etNuevoFechaNacimiento)
        etFechaNacimiento.addTextChangedListener(object : TextWatcher {

            private var modificando = false

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
            ) {}

            override fun afterTextChanged(s: Editable?) {

                if (modificando) return

                val numeros = s.toString()
                    .filter { it.isDigit() }
                    .take(8)

                val fecha = StringBuilder()

                for (i in numeros.indices) {

                    if (i == 2 || i == 4) {
                        fecha.append("/")
                    }

                    fecha.append(numeros[i])
                }

                if (fecha.toString() != s.toString()) {
                    modificando = true

                    etFechaNacimiento.setText(fecha.toString())
                    etFechaNacimiento.setSelection(fecha.length)

                    modificando = false
                }
            }
        })
        etTelefono = findViewById(R.id.etNuevoTelefono)
        etDomicilio = findViewById(R.id.etNuevoDomicilio)
        etEmail = findViewById(R.id.etNuevoEmail)
        etNumeroObraSocial = findViewById(R.id.etNuevoNumeroObraSocial)

        spObraSocial = findViewById(R.id.spNuevoObraSocial)
        btnGuardar = findViewById(R.id.btnGuardarPaciente)
        btnCancelar = findViewById(R.id.btnCancelarPaciente)

        configurarObraSocial()

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

    private fun configurarObraSocial() {
        spObraSocial.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val obraSocial = spObraSocial.selectedItem
                        ?.toString()
                        .orEmpty()

                    val esParticular =
                        obraSocial.equals("Particular", ignoreCase = true)

                    etNumeroObraSocial.isEnabled = !esParticular

                    if (esParticular) {
                        etNumeroObraSocial.setText("")
                        etNumeroObraSocial.hint = "No corresponde"
                    } else {
                        etNumeroObraSocial.hint = "Número de afiliado"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    etNumeroObraSocial.isEnabled = true
                    etNumeroObraSocial.hint = "Número de afiliado"
                }
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

        etNombre.setText(
            paciente.nombre.ifBlank { paciente.nombreCompleto }
        )
        etApellido.setText(paciente.apellido)
        etDni.setText(paciente.dni)
        val fecha = paciente.fechaNacimiento.filter { it.isDigit() }

        if (fecha.length == 8) {
            etFechaNacimiento.setText(
                "${fecha.substring(0, 2)}/${fecha.substring(2, 4)}/${fecha.substring(4, 8)}"
            )
        } else {
            etFechaNacimiento.setText(paciente.fechaNacimiento)
        }
        etTelefono.setText(paciente.telefono)
        etDomicilio.setText(paciente.domicilio)
        etEmail.setText(paciente.email)

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

        if (!paciente.obraSocial.equals("Particular", ignoreCase = true)) {
            etNumeroObraSocial.setText(paciente.numeroObraSocial)
        }
    }

    private fun guardarPaciente() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val fechaNacimiento = etFechaNacimiento.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val domicilio = etDomicilio.text.toString().trim()
        val email = etEmail.text.toString().trim()

        val obraSocial = spObraSocial.selectedItem
            ?.toString()
            .orEmpty()

        val esParticular =
            obraSocial.equals("Particular", ignoreCase = true)

        val numeroObraSocial = if (esParticular) {
            ""
        } else {
            etNumeroObraSocial.text.toString().trim()
        }

        if (nombre.isBlank() || apellido.isBlank() ||
            dni.isBlank() || telefono.isBlank()
        ) {
            Toast.makeText(
                this,
                "Completá nombre, apellido, DNI y teléfono.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            if (pacienteId == -1) {
                PacientesRepository.agregarPaciente(
                    nombre = nombre,
                    apellido = apellido,
                    dni = dni,
                    telefono = telefono,
                    obraSocial = obraSocial,
                    fechaNacimiento = fechaNacimiento,
                    domicilio = domicilio,
                    email = email,
                    numeroObraSocial = numeroObraSocial
                )

                Toast.makeText(
                    this,
                    "Paciente registrado con éxito.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                val actualizado = PacientesRepository.actualizarPaciente(
                    id = pacienteId,
                    nombre = nombre,
                    apellido = apellido,
                    dni = dni,
                    telefono = telefono,
                    obraSocial = obraSocial,
                    fechaNacimiento = fechaNacimiento,
                    domicilio = domicilio,
                    email = email,
                    numeroObraSocial = numeroObraSocial
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
