package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val cardConsultorios = findViewById<LinearLayout>(R.id.cardConsultorios)

        val cardEstudios = findViewById<LinearLayout>(R.id.cardEstudios)

        cardConsultorios.setOnClickListener {

            val intent = Intent(
                this,
                ConsultoriosActivity::class.java
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


}