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

        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: ""
        usuarioDni = intent.getStringExtra("USUARIO_DNI") ?: ""
        usuarioId = intent.getStringExtra("USUARIO_ID") ?: ""

        val btnVolver = findViewById<Button>(R.id.btnVolver)

        val botonesMap = mapOf(
            R.id.btnNatacion to "Natación",
            R.id.btnKarate to "karate",
            R.id.btnTennis to "Tennis",
            R.id.btnFutbol to "Fútbol",
            R.id.btnGArtistica to "G. artistica",
            R.id.btnbBasquet to "Basquet",
            R.id.btnVoley to "Voley"
        )

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

        val actividadInfo = DatosClub.listaActividades.find {
            it.nombre.equals(nombreActividad, ignoreCase = true)
        }
         val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("USUARIO_NOMBRE", usuarioNombre)
        intent.putExtra("USUARIO_DNI", usuarioDni)
        intent.putExtra("USUARIO_ID", usuarioId)
        intent.putExtra("ACTIVIDAD_NOMBRE", nombreActividad)
        intent.putExtra("ACTIVIDAD_HORARIO", actividadInfo?.horario ?: "20:00")
        intent.putExtra("ACTIVIDAD_PROFESOR", actividadInfo?.profesor ?: "Por Asignar")
        intent.putExtra("ACTIVIDAD_VALOR",actividadInfo?.costo?:10000)
        startActivity(intent)
    }
}