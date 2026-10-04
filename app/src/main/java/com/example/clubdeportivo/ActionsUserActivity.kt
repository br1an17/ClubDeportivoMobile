package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActionsUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions_user)

        val tvNombreSocio = findViewById<TextView>(R.id.tvNombreSocio)
        val btnAsignarActividades = findViewById<Button>(R.id.btnAsignarActividades)
        val btnActividadesAsignadas = findViewById<Button>(R.id.btnVerActividades)

        val btnVolver = findViewById<Button>(R.id.btnVolver)

        val nombreRecibido = intent.getStringExtra("USUARIO_NOMBRE") ?: "Socio no identificado"
        val dniRecibido = intent.getStringExtra("USUARIO_DNI") ?: ""
        val idRecibido = intent.getStringExtra("USUARIO_ID")?:""
        tvNombreSocio.text = nombreRecibido

        btnAsignarActividades.setOnClickListener {
            val intent = Intent(
                this,
                AssignActivityActivity::class.java
            )
            intent.putExtra("USUARIO_NOMBRE", nombreRecibido)
            intent.putExtra("USUARIO_DNI", dniRecibido)
            intent.putExtra("USUARIO_ID", idRecibido)
            startActivity(intent)
        }
        btnActividadesAsignadas.setOnClickListener {
            val intent = Intent(
                this,
                ActivitysAsiggnedActivity::class.java
            )
            intent.putExtra("USUARIO_NOMBRE", nombreRecibido)
            intent.putExtra("USUARIO_DNI", dniRecibido)
            intent.putExtra("USUARIO_ID", idRecibido)
            startActivity(intent)
        }
        btnVolver.setOnClickListener {
            finish()
        }
    }
}