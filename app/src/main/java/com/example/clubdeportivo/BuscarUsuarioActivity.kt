package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

class BuscarUsuarioActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_usuario)


        val etDni = findViewById<EditText>(R.id.etDni)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)


        btnBuscar.setOnClickListener {
            val dniIngresado = etDni.text.toString().trim()

            if (dniIngresado.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un número de DNI", Toast.LENGTH_SHORT).show()
            } else {
                // Aquí agregarás la búsqueda en tu base de datos o lista
                Toast.makeText(this, "Buscando DNI: $dniIngresado...", Toast.LENGTH_SHORT).show()
            }
        }


        btnVolver.setOnClickListener {
            finish()
        }
    }
}