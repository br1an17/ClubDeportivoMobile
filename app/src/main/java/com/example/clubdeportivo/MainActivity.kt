package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Vinculamos el botón del XML por su ID
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)

        // 2. Escuchamos el evento Click del botón
        btnIngresar.setOnClickListener {
            // 3. Crear el Intent explícito hacia la Activity de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}