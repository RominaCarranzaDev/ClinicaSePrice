package com.example.clinicaseprice

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {

    protected lateinit var contenedorContenido: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)

        contenedorContenido = findViewById(R.id.contenedorContenido)

        configurarMenu()
    }

    private fun configurarMenu() {

        val navMenu = findViewById<TextView>(R.id.navMenu)
        val navPacientes = findViewById<TextView>(R.id.navPacientes)
        val navTurnos = findViewById<TextView>(R.id.navTurnos)
        val navSalaEspera = findViewById<TextView>(R.id.navSalaEspera)
        val navAdministracion = findViewById<TextView>(R.id.navAdministracion)
        val btnCerrarSesion = findViewById<TextView>(R.id.btnCerrarSesion)

        navMenu.setOnClickListener {
            abrirPantalla(MenuActivity::class.java)
        }

        navPacientes.setOnClickListener {
            abrirPantalla(ConsultoriosPacientesActivity::class.java)
        }

        navTurnos.setOnClickListener {
            abrirPantalla(ConsultoriosTurnosActivity::class.java)
        }

        navSalaEspera.setOnClickListener {
            abrirPantalla(SalaEsperaActivity::class.java)
        }

        navAdministracion.setOnClickListener {
            abrirPantalla(AdministracionActivity::class.java)
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun abrirPantalla(destino: Class<out Activity>) {

        if (javaClass != destino) {
            val intent = Intent(this, destino)
            startActivity(intent)
            finish()
        }
    }

    protected fun configurarPantalla(
        titulo: String,
        subtitulo: String,
        layoutContenido: Int,
        opcionActiva: String
    ) {

        findViewById<TextView>(R.id.txtTitulo).text = titulo
        findViewById<TextView>(R.id.txtSubtitulo).text = subtitulo


    // Mostrar el buscador en las pantallas que lo necesitan.
    // En Administración no corresponde mostrarlo.
        val layoutBusqueda = findViewById<android.view.View>(
            R.id.layoutBusquedaGlobal
        )

        layoutBusqueda.visibility = if (opcionActiva == "administracion") {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }

        contenedorContenido.removeAllViews()
        LayoutInflater.from(this).inflate(
            layoutContenido,
            contenedorContenido,
            true
        )

        actualizarMenu(opcionActiva)
    }

    private fun actualizarMenu(opcionActiva: String) {

        val opciones = mapOf(
            "pacientes" to findViewById<TextView>(R.id.navPacientes),
            "turnos" to findViewById<TextView>(R.id.navTurnos),
            "sala_espera" to findViewById<TextView>(R.id.navSalaEspera),
            "administracion" to findViewById<TextView>(R.id.navAdministracion),
            "inicio" to findViewById<TextView>(R.id.navMenu)
        )

        opciones.forEach { (nombre, elemento) ->

            val seleccionado = nombre == opcionActiva

            val color = ContextCompat.getColor(
                this,
                if (seleccionado) R.color.primary else R.color.shadow
            )

            elemento.setBackgroundColor(
                if (seleccionado) {
                    ContextCompat.getColor(this, R.color.blur)
                } else {
                    Color.TRANSPARENT
                }
            )

            elemento.setTextColor(color)

            elemento.setTypeface(
                null,
                if (seleccionado) Typeface.BOLD else Typeface.NORMAL
            )

            elemento.compoundDrawableTintList = ColorStateList.valueOf(color)
        }
    }
    protected fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}