package com.example.clinicaseprice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class ConsultoriosPacientesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Consultorios Externos. Pacientes",
            subtitulo = "Consulta y registro.",
            layoutContenido = R.layout.activity_consultorios_pacientes,
            opcionActiva = "pacientes"
        )

    }
}