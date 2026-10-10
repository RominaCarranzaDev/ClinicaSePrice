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

    // Configura la navegación compartida
    private fun configurarMenu() {

        val navMenu = findViewById<TextView>(R.id.navMenu)
        val navPacientes = findViewById<TextView>(R.id.navPacientes)
        val navTurnos = findViewById<TextView>(R.id.navTurnos)
        val navAdministracion = findViewById<TextView>(R.id.navAdministracion)

        navMenu.setOnClickListener {
            abrirPantalla(MenuActivity::class.java)
        }

        navPacientes.setOnClickListener {
            abrirPantalla(ConsultoriosPacientesActivity::class.java)
        }

        navTurnos.setOnClickListener {
            abrirPantalla(ConsultoriosTurnosActivity::class.java)
        }

        //navAdministracion.setOnClickListener {
        //    abrirPantalla(AdministracionActivity::class.java)
        //}
    }

    // Evita volver a abrir la pantalla actual
    private fun abrirPantalla(
        destino: Class<out Activity>
    ) {
        if (javaClass != destino) {
            val intent = Intent(this, destino)
            startActivity(intent)
            finish()
        }
    }

    // Configura el header: título, subtítulo y contenido
    protected fun configurarPantalla(
        titulo: String,
        subtitulo: String,
        layoutContenido: Int,
        opcionActiva: String
    ) {
        findViewById<TextView>(R.id.txtTitulo).text = titulo

        findViewById<TextView>(R.id.txtSubtitulo).text = subtitulo

        contenedorContenido.removeAllViews()

        LayoutInflater.from(this).inflate(
            layoutContenido,
            contenedorContenido,
            true
        )

        actualizarMenu(opcionActiva)
    }

    // Resalta la opción del menú correspondiente
    private fun actualizarMenu(opcionActiva: String) {

        val opciones = mapOf(
            "pacientes" to findViewById<TextView>(R.id.navPacientes),
            "turnos" to findViewById<TextView>(R.id.navTurnos),
            "administracion" to findViewById<TextView>(R.id.navAdministracion),
            "inicio" to findViewById<TextView>(R.id.navMenu)
        )

        opciones.forEach { (nombre, elemento) ->

            val seleccionado = nombre == opcionActiva

            // Color según el estado
            val color = ContextCompat.getColor(
                this,
                if (seleccionado) R.color.primary else R.color.shadow
            )

            // Fondo
            elemento.setBackgroundColor(
                if (seleccionado)
                    ContextCompat.getColor(this, R.color.blur)
                else
                    Color.TRANSPARENT
            )

            // Color del texto
            elemento.setTextColor(color)

            // Negrita para la opción seleccionada
            elemento.setTypeface(
                null,
                if (seleccionado) Typeface.BOLD else Typeface.NORMAL
            )

            // Color del drawable (ícono)
            elemento.compoundDrawableTintList = ColorStateList.valueOf(color)
        }
    }
}