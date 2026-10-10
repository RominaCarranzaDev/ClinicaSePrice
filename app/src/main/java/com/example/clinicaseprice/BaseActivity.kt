package com.example.clinicaseprice

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    protected lateinit var contenedorContenido: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)

        contenedorContenido =
            findViewById(R.id.contenedorContenido)
    }

    protected fun configurarPantalla(
        titulo: String,
        subtitulo: String,
        layoutContenido: Int
    ) {
        findViewById<TextView>(R.id.txtTitulo).text = titulo
        findViewById<TextView>(R.id.txtSubtitulo).text = subtitulo

        contenedorContenido.removeAllViews()

        LayoutInflater.from(this).inflate(
            layoutContenido,
            contenedorContenido,
            true
        )
    }
}