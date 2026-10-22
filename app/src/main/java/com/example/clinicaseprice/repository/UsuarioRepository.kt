package com.example.clinicaseprice.repository

import com.example.clinicaseprice.model.Usuario

object UsuarioRepository {
    private val usuarios = mutableListOf(
        Usuario(
            id = 1,
            nombreUsuario = "admin",
            clave = "123456",
            rol = "administrador"
        ),
        Usuario(
            id = 2,
            nombreUsuario = "medico",
            clave = "1234",
            rol = "medico"
        ),
        Usuario(
            id = 3,
            nombreUsuario = "medico1",
            clave = "1234",
            rol = "medico"
        ),
        Usuario(
            id = 4,
            nombreUsuario = "medico2",
            clave = "1234",
            rol = "medico"
        ),
        Usuario(
            id = 5,
            nombreUsuario = "medico3",
            clave = "1234",
            rol = "medico"
        ),
        Usuario(
            id = 6,
            nombreUsuario = "medico4",
            clave = "1234",
            rol = "medico"
        ),
        Usuario(
            id = 7,
            nombreUsuario = "medico5",
            clave = "1234",
            rol = "medico"
        )
    )

    fun buscarPorCredenciales(
        nombreUsuario: String,
        clave: String
    ): Usuario? {
        return usuarios.find {
            it.nombreUsuario.equals(nombreUsuario.trim(), ignoreCase = true) &&
                    it.clave == clave
        }
    }
}
