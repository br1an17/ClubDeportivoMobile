
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AssignActivityActivity : AppCompatActivity() {

    private var usuarioNombre: String = ""
    private var usuarioDni: String = ""
    private var usuarioId: String = ""

    private var actividadesDisponibles: List<Actividad> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_assign_activity)

        // =====================================================
        // RECIBIR DATOS DEL CLIENTE
        // =====================================================

        usuarioNombre =
            intent.getStringExtra("USUARIO_NOMBRE") ?: ""

        usuarioDni =
            intent.getStringExtra("USUARIO_DNI") ?: ""

        usuarioId =
            intent.getStringExtra("USUARIO_ID") ?: ""

        // =====================================================
        // CONECTAR CON SQLITE
        // =====================================================

        val dbHelper = SocioDBHelper(this)

        actividadesDisponibles =
            dbHelper.obtenerCatalogoActividades()

        // Cargar catálogo inicial si la tabla está vacía
        if (actividadesDisponibles.isEmpty()) {

            dbHelper.sincronizarCatalogoActividades(
                DatosClub.listaActividades
            )

            actividadesDisponibles =
                dbHelper.obtenerCatalogoActividades()
        }

        dbHelper.close()

        if (actividadesDisponibles.isEmpty()) {

            Toast.makeText(
                this,
                "No hay actividades cargadas en la base de datos",
                Toast.LENGTH_LONG
            ).show()

        }

        // =====================================================
        // BOTÓN VOLVER
        // =====================================================

        val btnVolver =
            findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // RELACIONAR BOTONES CON ACTIVIDADES
        // =====================================================

        val botonesMap = mapOf(
            R.id.btnNatacion to "Natación",
            R.id.btnKarate to "Karate",
            R.id.btnTennis to "Tennis",
            R.id.btnFutbol to "Fútbol",
            R.id.btnGArtistica to "G. artistica",
            R.id.btnbBasquet to "Basquet",
            R.id.btnVoley to "Voley"
        )

        for ((btnId, nombreActividad) in botonesMap) {

            val boton =
                findViewById<MaterialButton>(btnId)
                    ?: continue

            // Buscar actividad en SQLite
            val actividad = actividadesDisponibles.firstOrNull {
                it.nombre.equals(
                    nombreActividad,
                    ignoreCase = true
                )
            }

            if (actividad != null) {

                boton.text = actividad.nombre

                actividad.iconoResId?.let { resId ->

                    boton.icon =
                        ContextCompat.getDrawable(this, resId)
                }

                boton.setOnClickListener {

                    seleccionarActividad(actividad)
                }

            } else {

                boton.isEnabled = false
            }
        }

        // =====================================================
        // VOLVER
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // =====================================================
    // ABRIR DETALLE DE ACTIVIDAD
    // =====================================================

    private fun seleccionarActividad(actividad: Actividad) {

        val intent = Intent(
            this,
            DetailActivity::class.java
        )

        intent.putExtra(
            "USUARIO_NOMBRE",
            usuarioNombre
        )

        intent.putExtra(
            "USUARIO_DNI",
            usuarioDni
        )

        intent.putExtra(
            "USUARIO_ID",
            usuarioId
        )

        intent.putExtra(
            "ACTIVIDAD_NOMBRE",
            actividad.nombre
        )

        intent.putExtra(
            "ACTIVIDAD_HORARIO",
            actividad.horario
        )

        intent.putExtra(
            "ACTIVIDAD_PROFESOR",
            actividad.profesor
        )

        intent.putExtra(
            "ACTIVIDAD_VALOR",
            actividad.costo
        )

        startActivity(intent)
    }
}
