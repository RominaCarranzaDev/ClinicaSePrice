package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.model.Turno
import com.example.clinicaseprice.repository.TurnosRepository
import android.widget.Toast
import com.example.clinicaseprice.model.Medicamento
import com.example.clinicaseprice.model.Medico
import com.example.clinicaseprice.model.OrdenMedicaEstudio
import com.example.clinicaseprice.model.Paciente
import com.example.clinicaseprice.model.PrescripcionMedica
import com.example.clinicaseprice.model.PresentacionMedicamento
import com.example.clinicaseprice.model.SignosVitales
import com.example.clinicaseprice.repository.HistoriaClinicaRepository
import com.example.clinicaseprice.repository.MedicosRepository
import com.example.clinicaseprice.repository.PacientesRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AtencionMedicaActivity : AppCompatActivity() {

    // Lista de espera
    private lateinit var adapterSalaEspera: SalaEsperaAdapter
    private val pacientesEnEspera = mutableListOf<Turno>()
    private lateinit var tvCantidadPacientes: TextView
    private var medico: Medico? = null


    // Búsqueda de medicamentos y estudios
    private lateinit var etBuscarMedicamento: AutoCompleteTextView
    private lateinit var etBuscarEstudio: AutoCompleteTextView
    private lateinit var llListadoPrescripciones: LinearLayout
    private lateinit var llListadoEstudios: LinearLayout

    // Datos del paciente llamado
    private var pacienteEnAtencion: Paciente? = null

    private var turnoEnAtencion: Turno? = null
    private lateinit var tvNombrePaciente: TextView
    private lateinit var tvInicialesPaciente: TextView
    private  lateinit var hcDniPaciente: TextView
    private  lateinit var fechaNacEdad: TextView
    private  lateinit var emailPaciente: TextView
    private  lateinit var telPaciente: TextView
    private  lateinit var tvGrupoyFactorSanguineo: TextView
    private lateinit var tvFactorRiesgo: TextView
    private lateinit var etAnamnesis: EditText
    private lateinit var etDiagnostico: EditText
    private lateinit var etObservaciones: EditText
    private val prescripcionesActuales = mutableListOf<PrescripcionMedica>()
    private val estudiosActuales = mutableListOf<OrdenMedicaEstudio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atencion_medica)

        // Datos del paciente
        tvNombrePaciente = findViewById(R.id.tvNombrePaciente)
        tvInicialesPaciente = findViewById(R.id.tvInicialesPaciente)
        hcDniPaciente = findViewById(R.id.tvHCyDNIPaciente)
        fechaNacEdad = findViewById(R.id.tvFechaNacyEdadPaciente)
        emailPaciente = findViewById(R.id.tvEmailPaciente)
        telPaciente = findViewById(R.id.tvTelPaciente)

        // Historia clínica
        tvGrupoyFactorSanguineo = findViewById(R.id.tvGrupoyFactorSanguineo)
        tvFactorRiesgo = findViewById(R.id.tvFactorRiesgo)

        etAnamnesis = findViewById(R.id.etAnamnesis)
        etDiagnostico= findViewById(R.id.etDiagnostico)
        etObservaciones = findViewById(R.id.etObservaciones)

        // Contenedores
        llListadoPrescripciones = findViewById(R.id.llListadoPrescripciones)
        llListadoEstudios = findViewById(R.id.llListadoEstudios)

        val nombreMedico = findViewById<TextView>(R.id.tvNombreProfesional)
        val especialidad = findViewById<TextView>(R.id.tvEspecialidadProfesional)
        val nroConsultorio = findViewById<TextView>(R.id.tvNroConsultorio)

        val btnCerrarSesion = findViewById<ImageButton>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        // Data Medico
        val usuarioId = intent.getIntExtra("usuarioId", -1)
        medico = MedicosRepository.buscarPorIdUsuario(usuarioId)

        nombreMedico.text = "${medico?.nombre} ${medico?.apellido}"
        especialidad.text = "${medico?.especialidad}"
        nroConsultorio.text = " CONSULTORIO N ${medico?.consultorioAsignado?.numero}"

        // Registrar la atención en la Historia Clínica
        val btnRegistrarAtencion = findViewById<Button>(R.id.btnFinalizarConsulta)

        btnRegistrarAtencion.setOnClickListener {

            // Verificar que haya un turno en atención
            val turnoActual = turnoEnAtencion

            if (turnoActual == null) {
                Toast.makeText(
                    this,
                    "Primero debés llamar a un paciente.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Verificar que el turno todavía esté en atención
            val turnoGuardado = TurnosRepository.buscarPorId(turnoActual.id)

            if (turnoGuardado?.estado != "EN ATENCIÓN") {
                Toast.makeText(
                    this,
                    "Este turno ya no está en atención.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val pacienteActual = pacienteEnAtencion
            val medicoActual = medico

            if (pacienteActual == null || medicoActual == null) {
                Toast.makeText(
                    this,
                    "No se pudo identificar al paciente o al médico.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val anamnesis = etAnamnesis.text.toString().trim()
            val diagnostico = etDiagnostico.text.toString().trim()
            val observaciones = etObservaciones.text.toString().trim()

            // Validar datos obligatorios
            if (anamnesis.isEmpty() || diagnostico.isEmpty()) {
                Toast.makeText(
                    this,
                    "Completá la anamnesis y el diagnóstico.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val presionMax = findViewById<EditText>(R.id.etPresionArterialMax)
            val presionMin = findViewById<EditText>(R.id.etPresionArterialMin)
            val etFrecuenciaCardiaca =
                findViewById<EditText>(R.id.etFrecuenciaCardiaca)
            val etTemperatura =
                findViewById<EditText>(R.id.etTemperatura)
            val etSaturacionOxigeno =
                findViewById<EditText>(R.id.etSaturacionOxigeno)

            val signosVitalesActuales = SignosVitales(
                presionArterial =
                    "${presionMax.text.toString().trim()} - ${presionMin.text.toString().trim()}",
                frecuenciaCardiaca =
                    etFrecuenciaCardiaca.text.toString().trim(),
                temperatura =
                    etTemperatura.text.toString().trim(),
                saturacionOxigeno =
                    etSaturacionOxigeno.text.toString().trim()
            )

            // Registrar la atención en la historia clínica
            try {
                HistoriaClinicaRepository.registrarAtencion(
                    pacienteId = pacienteActual.id,
                    medicoId = medicoActual.id,
                    anamnesis = anamnesis,
                    diagnostico = diagnostico,
                    observaciones = observaciones,
                    signosVitales = signosVitalesActuales,
                    prescripciones = prescripcionesActuales.toList(),
                    estudios = estudiosActuales.toList()
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "No se pudo registrar la atención: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Cambiar el estado del turno a ATENDIDO
            val actualizado = TurnosRepository.actualizarEstado(
                id = turnoActual.id,
                nuevoEstado = "ATENDIDO"
            )

            if (!actualizado) {
                Toast.makeText(
                    this,
                    "La atención se guardó, pero no se pudo actualizar el turno.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Consulta finalizada correctamente.",
                Toast.LENGTH_SHORT
            ).show()

            // Limpiar la atención actual
            turnoEnAtencion = null
            pacienteEnAtencion = null

            limpiarCamposAtencion()

            prescripcionesActuales.clear()
            estudiosActuales.clear()

            llListadoPrescripciones.removeAllViews()
            llListadoEstudios.removeAllViews()

            tvNombrePaciente.text = "Seleccione un paciente"
            tvInicialesPaciente.text = "--"
            hcDniPaciente.text = ""
            fechaNacEdad.text = ""
            emailPaciente.text = ""
            telPaciente.text = ""

            presionMax.text.clear()
            presionMin.text.clear()
            etFrecuenciaCardiaca.text.clear()
            etTemperatura.text.clear()
            etSaturacionOxigeno.text.clear()

            cargarSalaEspera()
        }
        // Configurar Sala de espera
        tvCantidadPacientes = findViewById(R.id.tvCantidadPacientesEnEspera)

        val rvSalaEspera = findViewById<RecyclerView>(R.id.rvListadoSalaEspera)

        rvSalaEspera.layoutManager = LinearLayoutManager(this)

        adapterSalaEspera = SalaEsperaAdapter(emptyList())
        rvSalaEspera.adapter = adapterSalaEspera

        cargarSalaEspera()

        // Botón Llamar siguiente
        val btnLlamarSiguiente = findViewById<Button>(R.id.btnLlamarSiguiente)

        btnLlamarSiguiente.setOnClickListener {
            llamarSiguientePaciente()
        }

        // Buscar medicamentos
        etBuscarMedicamento = findViewById(R.id.etBuscarMedicamento)

        val nombresMedicamentos = Medicamento.values().map { it.nombre }

        val adapterMedicamentos = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            nombresMedicamentos
        )

        etBuscarMedicamento.setAdapter(adapterMedicamentos)

        etBuscarMedicamento.setOnClickListener {
            etBuscarMedicamento.showDropDown()
        }

        etBuscarMedicamento.setOnItemClickListener {
                _, _, position, _ ->

            val nombre = adapterMedicamentos.getItem(position)
                ?: return@setOnItemClickListener

            val medicamento = Medicamento.values()
                .find { it.nombre == nombre }
                ?: return@setOnItemClickListener

            mostrarDialogoPrescripcion(medicamento)

            etBuscarMedicamento.text.clear()
        }

        // Buscar estudios
        etBuscarEstudio = findViewById(R.id.etBuscarEstudio)

        val nombresEstudios = OrdenMedicaEstudio.values().map { it.descripcion }

        val adapterEstudios = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            nombresEstudios
        )

        etBuscarEstudio.setAdapter(adapterEstudios)

        etBuscarEstudio.setOnClickListener {
            etBuscarEstudio.showDropDown()
        }

        etBuscarEstudio.setOnItemClickListener {
                _, _, position, _ ->

            val descripcion = adapterEstudios.getItem(position)
                ?: return@setOnItemClickListener

            val estudio = OrdenMedicaEstudio.values()
                .find { it.descripcion == descripcion }
                ?: return@setOnItemClickListener

            if (estudio !in estudiosActuales) {
                estudiosActuales.add(estudio)

                agregarTarjeta(
                    contenedor = llListadoEstudios,
                    texto = estudio.descripcion,
                    esEstudio = true
                )
            }

            etBuscarEstudio.text.clear()
        }

        val btnHistoria = findViewById<Button>(R.id.btnHistoria)

        btnHistoria.setOnClickListener {
            val intent = Intent(
                this,
                HistoriaClinicaActivity::class.java
            )

            intent.putExtra("paciente_id", pacienteEnAtencion?.id)
            startActivity(intent)
        }
        }

    override fun onResume() {
        super.onResume()
        cargarSalaEspera()
    }

    // Cargar los turnos acreditados en la lista de espera
    private fun cargarSalaEspera() {
        // Limpia el listado para cargarlo actualizado
        pacientesEnEspera.clear()

        pacientesEnEspera.addAll(
            TurnosRepository.obtenerTodos().filter { turno ->
                turno.estado.equals("Acreditado", ignoreCase = true) &&
                        turno.consultorio == medico?.consultorioAsignado?.numero.toString()
            }
        )
        // Mantiene actualizado el listado
        adapterSalaEspera.actualizarLista(pacientesEnEspera)
        // Contador de pacientes en espera
        actualizarCantidadPacientes()
    }

    // Actualizar el contador de pacientes en espera
    private fun actualizarCantidadPacientes() {

        val cantidad = pacientesEnEspera.size

        tvCantidadPacientes.text = if (cantidad == 0) {
            "No hay paciente en espera"
        } else {
            "$cantidad pacientes en espera"
        }
    }

    // Llama al primer paciente de la lista para ser atendido
    private fun llamarSiguientePaciente() {

        if (pacientesEnEspera.isEmpty()) {
            tvNombrePaciente.text = "No hay pacientes en espera"
            return
        }
        // Obtener el primer paciente
        val turno = pacientesEnEspera[0]
        val paciente = PacientesRepository.buscarPorId(turno.pacienteId)
        pacienteEnAtencion = paciente
        cargarDatosPaciente(paciente)

        // Cambiar el estado para que no vuelva a la lista
        val actualizado = TurnosRepository.actualizarEstado(
            id = turno.id,
            nuevoEstado = "En atención"
        )
        if (!actualizado) {
            Toast.makeText(
                this,
                "No se pudo actualizar el estado del turno.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        turnoEnAtencion = turno

        cargarHistoriaClinica(turno.pacienteId)

        // Quitar el paciente de la lista de espera
        cargarSalaEspera()
    }

    private fun cargarDatosPaciente(paciente: Paciente?) {
        if (paciente == null) {
            tvNombrePaciente.text = "PACIENTE NO ENCONTRADO"
            return
        }

        // Nombre completo
        tvNombrePaciente.text = paciente.nombreCompleto

        // Iniciales
        val iniciales = paciente.nombreCompleto
            .trim()
            .split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")

        tvInicialesPaciente.text = iniciales

        // Otros datos del paciente
        hcDniPaciente.text = "${paciente.numeroHistoriaClinica}  ●  DNI ${paciente.dni.toString()}"
        fechaNacEdad.text = "${paciente.fechaNacimiento.toString()}  ●  ${calcularEdad(paciente.fechaNacimiento)} años"
        emailPaciente.text = "${paciente.email}"
        telPaciente.text = "${paciente.telefono}"
    }
    private fun cargarHistoriaClinica(pacienteId: Int) {

        // Limpiar los datos del paciente anterior
        llListadoPrescripciones.removeAllViews()
        llListadoEstudios.removeAllViews()

        val historia = HistoriaClinicaRepository.buscarPorPaciente(pacienteId)

        if (historia == null) {
            tvGrupoyFactorSanguineo.text = "GRUPO SANGUINEO\n\nSin datos"
            tvFactorRiesgo.text = "⚠ FACTORES DE RIESGO\n\nSin datos"

            return
        }

        // Grupo sanguíneo y factor Rh
        tvGrupoyFactorSanguineo.text = "GRUPO SANGUINEO:\n\n${historia.grupoSanguineo?.descripcion}"

        tvFactorRiesgo.text =
            if (historia.factoresRiesgo.isEmpty()) {
                "⚠ FACTORES DE RIESGO\n\nSin factores de riesgo registrados"
            } else {
                "⚠ FACTORES DE RIESGO\n\n" +
                        historia.factoresRiesgo.joinToString("\n") {
                            "• ${it.descripcion}"
                        }
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
                "$texto\n"
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

    private fun mostrarDialogoPrescripcion(
        medicamento: Medicamento
    ) {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 8, 48, 0)
        }

        val tvMedicamento = TextView(this).apply {
            text = "Medicamento: ${medicamento.nombre}"
            textSize = 18f
        }

        val tvPresentacion = TextView(this).apply {
            text = "Presentación"
        }

        val spinnerPresentacion = Spinner(this)

        val presentaciones = PresentacionMedicamento.values()

        val adapterPresentaciones = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            presentaciones.map { it.descripcion }
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        spinnerPresentacion.adapter = adapterPresentaciones

        val etPosologia = EditText(this).apply {
            hint = "Posología (ej.: según indicación médica)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 2
        }

        val etDuracion = EditText(this).apply {
            hint = "Duración del tratamiento"
        }

        val etIndicaciones = EditText(this).apply {
            hint = "Indicaciones adicionales (opcional)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 2
        }

        contenedor.addView(tvMedicamento)
        contenedor.addView(tvPresentacion)
        contenedor.addView(spinnerPresentacion)
        contenedor.addView(etPosologia)
        contenedor.addView(etDuracion)
        contenedor.addView(etIndicaciones)

        val dialogo = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Nueva prescripción")
            .setView(contenedor)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Agregar", null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(
                androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val posologia = etPosologia.text.toString().trim()
                val duracion = etDuracion.text.toString().trim()
                val indicaciones = etIndicaciones.text.toString().trim()

                if (posologia.isBlank() || duracion.isBlank()) {
                    Toast.makeText(
                        this,
                        "Completá la posología y la duración.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val presentacion =
                    presentaciones[spinnerPresentacion.selectedItemPosition]

                val prescripcion = PrescripcionMedica(
                    medicamento = medicamento,
                    presentacion = presentacion,
                    posologia = posologia,
                    duracion = duracion,
                    indicaciones = indicaciones
                )

                prescripcionesActuales.add(prescripcion)

                val detalle = buildString {
                    appendLine(prescripcion.medicamento.nombre)
                    appendLine("Presentación: ${prescripcion.presentacion.descripcion}")
                    appendLine("Posología: ${prescripcion.posologia}")
                    appendLine("Duración: ${prescripcion.duracion}")

                    if (prescripcion.indicaciones.isNotBlank()) {
                        append("Indicaciones: ${prescripcion.indicaciones}")
                    }
                }.trim()

                agregarTarjeta(
                    contenedor = llListadoPrescripciones,
                    texto = detalle
                )

                dialogo.dismiss()
            }
        }

        dialogo.show()
    }

    //Calcula la edad segun la fecha de nacimiento
    private fun calcularEdad(fechaNacimiento: String): Int {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false

        val fecha = formato.parse(fechaNacimiento) ?: return 0

        val nacimiento = Calendar.getInstance().apply { time = fecha }

        val hoy = Calendar.getInstance()

        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

        val aunNoCumplio =
            hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH) || (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH) && hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))

        if (aunNoCumplio) {
            edad--
        }

        return edad
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun limpiarCamposAtencion() {
        etAnamnesis.text.clear()
        etDiagnostico.text.clear()
        etObservaciones.text.clear()
    }
}

