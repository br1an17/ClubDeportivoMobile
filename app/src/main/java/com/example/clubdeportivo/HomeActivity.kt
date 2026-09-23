package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


    class HomeActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_home)

            val nombreUsuario = intent.getStringExtra("USUARIO_NOMBRE") ?: "Usuario"

            val tvSubtitulo = findViewById<TextView>(R.id.tvSubtitulo)

            tvSubtitulo.text = "$nombreUsuario"

            val btnSalir = findViewById<Button>(R.id.btnSalir)

            btnSalir.setOnClickListener {
                finish()
            }
        }
    }
