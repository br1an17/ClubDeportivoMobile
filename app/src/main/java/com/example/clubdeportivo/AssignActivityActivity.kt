package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AssignActivityActivity : AppCompatActivity() {

    private var usuarioNombre: String = ""
    private var usuarioDni: String = ""
    private var usuarioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_activity)

        // Capturar los datos del socio que vienen desde la pantalla anterior
        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: ""
        usuarioDni = intent.getStringExtra("USUARIO_DNI") ?: ""
        usuarioId = intent.getStringExtra("USUARIO_ID") ?: ""

        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Mapa relacionando los IDs del XML con las actividades
        val botonesMap = mapOf(
            R.id.btnNatacion to "Natación",
            R.id.btnKarate to "karate",
            R.id.btnTennis to "Tennis",
            R.id.btnFutbol to "Fútbol",
            R.id.btnGArtistica to "G. artistica",
            R.id.btnbBasquet to "Basquet",
            R.id.btnVoley to "Voley"
        )

        // Recorrer las actividades de DatosClub y asignar nombre, icono y evento
        for ((btnId, nombreActividad) in botonesMap) {
            val boton = findViewById<MaterialButton>(btnId) ?: continue
            val actividadData = DatosClub.listaActividades.find {
                it.nombre.equals(nombreActividad, ignoreCase = true)
            }

            if (actividadData != null) {
                boton.text = actividadData.nombre
                actividadData.iconoResId?.let { resId ->
                    boton.icon = ContextCompat.getDrawable(this, resId)
                }
            }

            boton.setOnClickListener {
                seleccionarActividad(nombreActividad)
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun seleccionarActividad(nombreActividad: String) {

         val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("USUARIO_NOMBRE", usuarioNombre)
        intent.putExtra("USUARIO_DNI", usuarioDni)
        intent.putExtra("USUARIO_ID", usuarioId)
        intent.putExtra("ACTIVIDAD_NOMBRE", nombreActividad)
        startActivity(intent)
    }
}