package com.example.clubdeportivo

import android.content.Intent
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

            val btnRegistro = findViewById<Button>(R.id.btnRegistro)
            btnRegistro.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            val btnSalir = findViewById<Button>(R.id.btnSalir)

            btnSalir.setOnClickListener {
                finish()
            }
        }
    }
