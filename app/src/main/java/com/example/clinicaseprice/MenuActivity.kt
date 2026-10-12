package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val cardConsultorios = findViewById<LinearLayout>(R.id.cardConsultorios)

        val cardEstudios = findViewById<LinearLayout>(R.id.cardEstudios)

        val btnCerrarSesion = findViewById< ImageButton>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        cardConsultorios.setOnClickListener {

            val intent = Intent(
                this,
                ConsultoriosPacientesActivity::class.java
            )

            startActivity(intent)
        }

        cardEstudios.setOnClickListener {

            val intent = Intent(
                this,
                EstudiosActivity::class.java
            )

            startActivity(intent)
        }
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