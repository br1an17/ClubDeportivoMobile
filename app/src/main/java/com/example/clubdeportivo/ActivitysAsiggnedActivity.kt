package com.example.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ActivitysAsiggnedActivity : AppCompatActivity() {

    private var usuarioNombre: String = ""
    private var usuarioDni: String = ""
    private var usuarioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activitys_assigned)


        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: ""
        usuarioDni = intent.getStringExtra("USUARIO_DNI") ?: ""
        usuarioId = intent.getStringExtra("USUARIO_ID") ?: ""

        val nombreSocio =findViewById<TextView>(R.id.tvNombre)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorActividades)
        val btnAsignarMas = findViewById<Button>(R.id.btnAsignarMasActividades)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        nombreSocio.text =usuarioNombre

        cargarActividadesSocio(contenedor)


        btnAsignarMas.setOnClickListener {
            val intent = Intent(this, AssignActivityActivity::class.java)
            intent.putExtra("USUARIO_NOMBRE", usuarioNombre)
            intent.putExtra("USUARIO_DNI", usuarioDni)
            intent.putExtra("USUARIO_ID", usuarioId)
            startActivity(intent)
        }


        btnVolver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val contenedor = findViewById<LinearLayout>(R.id.contenedorActividades)
        cargarActividadesSocio(contenedor)
    }

    private fun cargarActividadesSocio(contenedor: LinearLayout) {
        contenedor.removeAllViews()


        val socio = DatosClub.socios.find {
            (usuarioDni.isNotEmpty() && it.dni == usuarioDni) ||
                    (usuarioId.isNotEmpty() && it.numeroAfiliado == usuarioId) ||
                    (usuarioNombre.isNotEmpty() && it.nombre.equals(usuarioNombre, ignoreCase = true))
        }

        val listaActividades = socio?.actividades ?: mutableListOf()

        if (listaActividades.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "El socio no tiene actividades asignadas"
                setTextColor(Color.GRAY)
                textSize = 16f
                setPadding(0, 32, 0, 0)
            }
            contenedor.addView(tvVacio)
            return
        }

        for (actividad in listaActividades) {
            val botonActividad = MaterialButton(this).apply {
                text = actividad
                setTextColor(Color.BLACK)
                textSize = 16f
                setTypeface(null, Typeface.BOLD)


                setBackgroundColor(Color.parseColor("#DCE7D4"))
                cornerRadius = 24 // Bordes redondeados suaves

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120 // Alto del botón
                ).apply {
                    setMargins(0, 0, 0, 24) // Espaciado entre botones
                }

                setOnClickListener {
                    Toast.makeText(context, "Actividad: $actividad", Toast.LENGTH_SHORT).show()
                }
            }
            contenedor.addView(botonActividad)
        }
    }
}