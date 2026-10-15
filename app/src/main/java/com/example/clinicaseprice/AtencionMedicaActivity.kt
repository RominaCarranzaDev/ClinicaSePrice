package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.TurnosRepository

class AtencionMedicaActivity : AppCompatActivity() {

    // Lista de espera
    private lateinit var adapterSalaEspera: SalaEsperaAdapter
    private val pacientesEnEspera = mutableListOf<Turno>()

    // Búsqueda de medicamentos y estudios
    private lateinit var etBuscarMedicamento: AutoCompleteTextView
    private lateinit var etBuscarEstudio: AutoCompleteTextView

    // Datos del paciente llamado
    private lateinit var tvNombrePaciente: TextView
    private lateinit var tvInicialesPaciente: TextView


    private lateinit var tvCantidadPacientes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atencion_medica)

        val btnCerrarSesion = findViewById<ImageButton>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
        // Inicializar vistas
        tvNombrePaciente = findViewById(R.id.tvNombrePaciente)
        tvInicialesPaciente = findViewById(R.id.tvInicialesPaciente)

        tvCantidadPacientes = findViewById(R.id.tvCantidadPacientesEnEspera)

        // Configurar lista de espera
        val rvSalaEspera = findViewById<RecyclerView>(R.id.rvListadoSalaEspera)

        rvSalaEspera.layoutManager = LinearLayoutManager(this)

        adapterSalaEspera = SalaEsperaAdapter(emptyList())
        rvSalaEspera.adapter = adapterSalaEspera

        cargarSalaEspera()

        // Configurar botón Llamar siguiente
        val btnLlamarSiguiente =
            findViewById<Button>(R.id.btnLlamarSiguiente)

        btnLlamarSiguiente.setOnClickListener {
            llamarSiguientePaciente()
        }

        // Configurar búsqueda de medicamentos
        etBuscarMedicamento =
            findViewById(R.id.etBuscarMedicamento)

        val principiosActivos = listOf(
            "Paracetamol",
            "Pantoprazol",
            "Ibuprofeno",
            "Amoxicilina",
            "Azitromicina"
        )

        val adapterMedicamentos = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            principiosActivos
        )

        etBuscarMedicamento.setAdapter(adapterMedicamentos)

        etBuscarMedicamento.setOnClickListener {
            etBuscarMedicamento.showDropDown()
        }

        // Configurar búsqueda de estudios
        etBuscarEstudio =
            findViewById(R.id.etBuscarEstudio)

        val estudios = listOf(
            "Hemograma completo",
            "Glucemia",
            "Colesterol total",
            "Orina completa",
            "Electrocardiograma",
            "Radiografía de tórax"
        )

        val adapterEstudios = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            estudios
        )

        etBuscarEstudio.setAdapter(adapterEstudios)

        etBuscarEstudio.setOnClickListener {
            etBuscarEstudio.showDropDown()
        }


        val llListadoPrescripciones =
            findViewById<LinearLayout>(
                R.id.llListadoPrescripciones
            )

        val llListadoEstudios =
            findViewById<LinearLayout>(
                R.id.llListadoEstudios
            )

        etBuscarMedicamento.setOnItemClickListener {
                _, _, position, _ ->

            val medicamento = adapterMedicamentos
                .getItem(position)
                .toString()

            agregarTarjeta(
                contenedor = llListadoPrescripciones,
                texto = medicamento
            )

            etBuscarMedicamento.text.clear()
        }

        // Agregar estudio al seleccionarlo
        etBuscarEstudio.setOnItemClickListener {
                _, _, position, _ ->

            val estudio = adapterEstudios
                .getItem(position)
                .toString()

            agregarTarjeta(
                contenedor = llListadoEstudios,
                texto = estudio,
                esEstudio = true
            )

            etBuscarEstudio.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarSalaEspera()
    }

    // Cargar los turnos acreditados en la lista de espera
    private fun cargarSalaEspera() {

        pacientesEnEspera.clear()

        pacientesEnEspera.addAll(
            TurnosRepository.turnos.filter { turno ->
                turno.estado == "Acreditado"
            }
        )

        adapterSalaEspera.actualizarLista(pacientesEnEspera)

        actualizarCantidadPacientes()
    }

    // Llamar al primer paciente de la lista
    private fun llamarSiguientePaciente() {

        if (pacientesEnEspera.isEmpty()) {
            tvNombrePaciente.text = "No hay pacientes en espera"
            tvInicialesPaciente.text = "--"
            return
        }

        // Obtener el primer paciente
        val turno = pacientesEnEspera[0]

        // Mostrar el nombre completo
        tvNombrePaciente.text = turno.nombrePaciente

        // Calcular las iniciales
        val iniciales = turno.nombrePaciente
            .trim()
            .split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")

        tvInicialesPaciente.text = iniciales

        // Cambiar el estado para que no vuelva a la lista
        turno.estado = "En atención"

        // Quitar el paciente de la lista de espera
        pacientesEnEspera.removeAt(0)

        // Actualizar la lista y el contador
        adapterSalaEspera.actualizarLista(pacientesEnEspera)

        actualizarCantidadPacientes()
    }

    // Actualizar el contador de pacientes en espera
    private fun actualizarCantidadPacientes() {

        val cantidad = pacientesEnEspera.size

        tvCantidadPacientes.text = if (cantidad == 1) {
            "1 paciente en espera"
        } else {
            "$cantidad pacientes en espera"
        }
    }

    // Crear una tarjeta para un medicamento o estudio
    private fun agregarTarjeta(
        contenedor: LinearLayout,
        texto: String,
        esEstudio: Boolean = false
    ) {

        val tarjeta = TextView(this).apply {

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(
                    R.dimen.spacing_s
                )
            }

            text = if (esEstudio) {
                "● $texto"
            } else {
                "$texto\n\nPosología: pendiente de completar."
            }

            setPadding(
                resources.getDimensionPixelSize(R.dimen.spacing_s),
                resources.getDimensionPixelSize(R.dimen.spacing_s),
                resources.getDimensionPixelSize(R.dimen.spacing_s),
                resources.getDimensionPixelSize(R.dimen.spacing_s)
            )

            setTextColor(
                resources.getColor(R.color.black, theme)
            )

            textSize = resources.getDimension(
                R.dimen.text_small
            ) / resources.displayMetrics.scaledDensity

            setBackgroundResource(R.drawable.bg_card_default)
        }

        contenedor.addView(tarjeta)
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}

