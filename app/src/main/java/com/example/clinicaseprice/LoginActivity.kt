package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clinicaseprice.model.Usuario


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        btnIngresar.setOnClickListener {
            val usuarioEscrito = etUsuario.text.toString().trim()
            val claveEscrita = etContrasena.text.toString().trim()

            // Validación de los campos requeridos
            if (usuarioEscrito.isEmpty() || claveEscrita.isEmpty()) {

                Toast.makeText(
                    this,
                    "Por favor, completa usuario y contraseña",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                // Validación existencia de usuario
                val usuarioEncontrado = usuarios.find {
                    it.nombreUsuario == usuarioEscrito &&
                            it.clave == claveEscrita
                }
                // Autenticación
                if (usuarioEncontrado != null) {
                    // Control del nivel de acceso. Autorización
                    when (usuarioEncontrado.rol) {

                        "admin" -> {
                            val intent = Intent(this, MenuActivity::class.java)
                            startActivity(intent)
                        }

                        "medico" -> {
                            val intent = Intent(this, AtencionMedicaActivity::class.java)
                            startActivity(intent)
                        }

                        "tecnico" -> {
                            val intent = Intent(this, EstudioMedicoActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Lista de usuarios iniciales con distintos roles
    private val usuarios = listOf(
        Usuario(1,"admin", "123456", "admin"),
        Usuario(2,"medico", "1234", "medico"),
        Usuario(3,"tecnico", "1234", "tecnico")
    )
}