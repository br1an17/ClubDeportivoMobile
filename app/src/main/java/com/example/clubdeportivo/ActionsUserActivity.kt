package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActionsUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions_user) // Asegúrate de que coincida con tu layout XML

        // ID del TextView donde querés mostrar el nombre del socio buscado
        val tvNombreSocio = findViewById<TextView>(R.id.tvNombreSocio)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Recuperar el nombre recibido desde SerchUserActivity
        val nombreRecibido = intent.getStringExtra("USUARIO_NOMBRE") ?: "Socio no identificado"

        // Mostrar el nombre en la pantalla
        tvNombreSocio.text = nombreRecibido

        btnVolver.setOnClickListener {
            finish()
        }
    }
}