
package com.example.clinicaseprice

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.clinicaseprice.repository.HistoriaClinicaRepository
import com.example.clinicaseprice.repository.MedicosRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdministracionActivity : BaseActivity() {

    companion object {
        private const val VALOR_POR_ATENCION = 80000L
    }

    private lateinit var btnSeleccionarFecha: Button
    private lateinit var txtFechaSeleccionada: TextView
    private lateinit var contenedorLiquidacion: LinearLayout
    private lateinit var txtCantidadTotal: TextView
    private lateinit var txtTotalHonorarios: TextView

    private val formatoFecha =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }

    private var fechaSeleccionada = formatoFecha.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurarPantalla(
            titulo = "Administración",
            subtitulo = "Liquidación diaria de honorarios médicos.",
            layoutContenido = R.layout.activity_administracion,
            opcionActiva = "administracion"
        )

        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha)
        txtFechaSeleccionada = findViewById(R.id.txtFechaSeleccionada)
        contenedorLiquidacion = findViewById(R.id.contenedorLiquidacion)
        txtCantidadTotal = findViewById(R.id.txtCantidadTotal)
        txtTotalHonorarios = findViewById(R.id.txtTotalHonorarios)

        btnSeleccionarFecha.setOnClickListener {
            seleccionarFecha()
        }

        actualizarLiquidacion()
    }

    private fun seleccionarFecha() {
        val calendario = Calendar.getInstance()

        try {
            calendario.time = formatoFecha.parse(fechaSeleccionada) ?: Date()
        } catch (_: Exception) {
            calendario.time = Date()
        }

        DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                calendario.set(anio, mes, dia)
                fechaSeleccionada = formatoFecha.format(calendario.time)
                actualizarLiquidacion()
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun actualizarLiquidacion() {
        txtFechaSeleccionada.text = "Fecha: $fechaSeleccionada"
        contenedorLiquidacion.removeAllViews()

        // Obtener las atenciones registradas para la fecha seleccionada.
        val atencionesDelDia =
            HistoriaClinicaRepository.obtenerTodasLasAtenciones()
                .filter { it.fecha == fechaSeleccionada }

        // Recorrer TODOS los médicos, incluso los que no atendieron ese día.
        val medicos = MedicosRepository.obtenerTodos().sortedBy { it.id }

        var totalAtenciones = 0
        var totalHonorarios = 0L

        if (medicos.isEmpty()) {
            agregarTexto(
                "No hay médicos registrados.",
                contenedorLiquidacion
            )
        } else {
            medicos.forEach { medico ->

                // Contar únicamente las atenciones vinculadas a este médico.
                val atencionesDelMedico = atencionesDelDia.filter {
                    it.medicoId == medico.id
                }

                val nombreMedico = "${medico.nombre} ${medico.apellido}"
                val cantidad = atencionesDelMedico.size
                val honorarios = cantidad * VALOR_POR_ATENCION

                totalAtenciones += cantidad
                totalHonorarios += honorarios

                agregarFila(
                    nombreMedico = nombreMedico,
                    cantidad = cantidad,
                    honorarios = honorarios
                )
            }
        }

        txtCantidadTotal.text = "Total de atenciones: $totalAtenciones"
        txtTotalHonorarios.text =
            "Total a liquidar: ${formatoMoneda.format(totalHonorarios)}"
    }

    private fun agregarFila(
        nombreMedico: String,
        cantidad: Int,
        honorarios: Long
    ) {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)

            setBackgroundColor(
                ContextCompat.getColor(
                    this@AdministracionActivity,
                    R.color.blur
                )
            )
        }

        val textoMedico = TextView(this).apply {
            text = nombreMedico
            textSize = 16f
            setTextColor(
                ContextCompat.getColor(
                    this@AdministracionActivity,
                    R.color.primary
                )
            )
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val textoCantidad = TextView(this).apply {
            text = "Atenciones realizadas: $cantidad"
            textSize = 14f
            setTextColor(
                ContextCompat.getColor(
                    this@AdministracionActivity,
                    R.color.shadow
                )
            )
        }

        val textoHonorarios = TextView(this).apply {
            text = "Honorarios: ${formatoMoneda.format(honorarios)}"
            textSize = 16f
            setTextColor(
                ContextCompat.getColor(
                    this@AdministracionActivity,
                    R.color.black
                )
            )
            setPadding(0, 8, 0, 0)
        }

        fila.addView(textoMedico)
        fila.addView(textoCantidad)
        fila.addView(textoHonorarios)

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        contenedorLiquidacion.addView(fila, parametros)
    }

    private fun agregarTexto(
        mensaje: String,
        contenedor: LinearLayout
    ) {
        val texto = TextView(this).apply {
            text = mensaje
            textSize = 15f
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(
                ContextCompat.getColor(
                    this@AdministracionActivity,
                    R.color.shadow
                )
            )
            setPadding(0, 12, 0, 12)
        }

        contenedor.addView(texto)
    }
}
