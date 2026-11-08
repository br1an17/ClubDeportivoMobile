
package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private var usuarioNombre = ""
    private var usuarioDni = ""
    private var usuarioId = ""

    private var actividadNombre = ""
    private var actividadHorario = ""
    private var actividadProfesor = ""
    private var actividadCosto = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_activitys)

        // =====================================================
        // RECIBIR DATOS
        // =====================================================

        usuarioNombre =
            intent.getStringExtra("USUARIO_NOMBRE") ?: ""

        usuarioDni =
            intent.getStringExtra("USUARIO_DNI") ?: ""

        usuarioId =
            intent.getStringExtra("USUARIO_ID") ?: ""

        actividadNombre =
            intent.getStringExtra("ACTIVIDAD_NOMBRE") ?: ""

        actividadHorario =
            intent.getStringExtra("ACTIVIDAD_HORARIO") ?: "20:00"

        actividadProfesor =
            intent.getStringExtra("ACTIVIDAD_PROFESOR") ?: "Por Asignar"

        actividadCosto =
            intent.getDoubleExtra("ACTIVIDAD_VALOR", 0.0)

        // =====================================================
        // CONECTAR ELEMENTOS
        // =====================================================

        val tvNombreActividad =
            findViewById<TextView>(R.id.tvNombreActividad)

        val tvHorarioActividad =
            findViewById<TextView>(R.id.tvHorarioActividad)

        val tvNombreProfesor =
            findViewById<TextView>(R.id.tvNombreProfesor)

        val tvValorActividad =
            findViewById<TextView>(R.id.tvValor)

        val btnConfirmarAsignar =
            findViewById<Button>(R.id.btnConfirmarAsignar)

        val btnVolver =
            findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // MOSTRAR DATOS DE LA ACTIVIDAD
        // =====================================================

        tvNombreActividad.text = actividadNombre
        tvHorarioActividad.text = actividadHorario
        tvNombreProfesor.text = actividadProfesor

        tvValorActividad.text = String.format(
            Locale.US,
            "$ %.2f",
            actividadCosto
        )

        // =====================================================
        // CONFIRMAR ASIGNACIÓN
        // =====================================================

        btnConfirmarAsignar.setOnClickListener {
            asignarActividadASocio()
        }

        // =====================================================
        // VOLVER
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // =====================================================
    // ASIGNAR ACTIVIDAD UTILIZANDO SQLITE
    // =====================================================

    private fun asignarActividadASocio() {

        if (usuarioDni.isBlank()) {

            Toast.makeText(
                this,
                "No se recibió el DNI del cliente",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val dbHelper = SocioDBHelper(this)

        try {

            // Asegurar que el catálogo esté cargado
            dbHelper.sincronizarCatalogoActividades(
                DatosClub.listaActividades
            )

            // Buscar cliente en SQLite
            val socio =
                dbHelper.obtenerClientePorDni(usuarioDni)

            if (socio == null) {

                Toast.makeText(
                    this,
                    "No se encontró el cliente en SQLite",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // Verificar si ya tiene esta actividad
            if (
                dbHelper.tieneActividadAsignada(
                    usuarioDni,
                    actividadNombre
                )
            ) {

                Toast.makeText(
                    this,
                    "El cliente ya tiene asignada esta actividad",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // Guardar la relación en SQLite
            val asignado =
                dbHelper.asignarActividadASocio(
                    usuarioDni,
                    actividadNombre
                )

            if (asignado) {

                Toast.makeText(
                    this,
                    "$actividadNombre asignada correctamente a ${socio.nombre}",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "No se pudo guardar la actividad",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al asignar actividad: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

        } finally {
            dbHelper.close()
        }
    }
}
