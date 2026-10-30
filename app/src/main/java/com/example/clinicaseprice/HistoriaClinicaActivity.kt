package com.example.clinicaseprice

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinicaseprice.adapter.AtencionMedicaAdapter
import com.example.clinicaseprice.repository.HistoriaClinicaRepository

class HistoriaClinicaActivity : AppCompatActivity() {
    private lateinit var rvAtenciones: RecyclerView
    private lateinit var tvSinAtenciones: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historia_clinica)
        val btnVolver = findViewById<TextView>(R.id.btnVolver)

        btnVolver.setOnClickListener {
            finish()
        }
        rvAtenciones = findViewById(R.id.rvAtenciones)
        tvSinAtenciones = findViewById(R.id.tvSinAtenciones)

        rvAtenciones.layoutManager = LinearLayoutManager(this)

        val pacienteId = intent.getIntExtra("paciente_id", -1)

        if (pacienteId == -1) {
            Toast.makeText(
                this,
                "No se recibió el paciente.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        cargarAtenciones(pacienteId)
    }

    private fun cargarAtenciones(pacienteId: Int) {
        val historia =
            HistoriaClinicaRepository.buscarPorPaciente(pacienteId)

        val atenciones = historia?.atenciones
            ?.sortedByDescending { it.id }
            ?: emptyList()

        rvAtenciones.adapter = AtencionMedicaAdapter(atenciones)

        tvSinAtenciones.visibility =
            if (atenciones.isEmpty()) View.VISIBLE else View.GONE

        rvAtenciones.visibility =
            if (atenciones.isEmpty()) View.GONE else View.VISIBLE
    }
}