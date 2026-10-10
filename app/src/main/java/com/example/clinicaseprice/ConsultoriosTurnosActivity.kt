package com.example.clinicaseprice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class ConsultoriosTurnosActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configurarPantalla(
            titulo = "Consultorios Externos. Turnos",
            subtitulo = "Consulta y registro de turnos y sobreturnos.",
            layoutContenido = R.layout.activity_pacientes_turnos,
            opcionActiva = "turnos"
        )
    }
}