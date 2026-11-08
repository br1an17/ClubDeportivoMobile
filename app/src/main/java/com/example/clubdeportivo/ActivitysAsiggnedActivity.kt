
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

    private var usuarioNombre = ""
    private var usuarioDni = ""
    private var usuarioId = ""

    private lateinit var contenedorActividades: LinearLayout
    private lateinit var nombreSocio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_activitys_assigned)

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
        // ELEMENTOS DE LA PANTALLA
        // =====================================================

        nombreSocio = findViewById(R.id.tvNombre)

        contenedorActividades =
            findViewById(R.id.contenedorActividades)

        val btnAsignarMas =
            findViewById<Button>(R.id.btnAsignarMasActividades)

        val btnVolver =
            findViewById<Button>(R.id.btnVolver)

        nombreSocio.text = usuarioNombre

        // =====================================================
        // ASIGNAR MÁS ACTIVIDADES
        // =====================================================

        btnAsignarMas.setOnClickListener {

            val intent = Intent(
                this,
                AssignActivityActivity::class.java
            )

            intent.putExtra("USUARIO_NOMBRE", usuarioNombre)
            intent.putExtra("USUARIO_DNI", usuarioDni)
            intent.putExtra("USUARIO_ID", usuarioId)

            startActivity(intent)
        }

        // =====================================================
        // VOLVER
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // =====================================================
    // ACTUALIZAR ACTIVIDADES AL VOLVER A LA PANTALLA
    // =====================================================

    override fun onResume() {
        super.onResume()

        cargarActividadesSocio()
    }

    // =====================================================
    // CONSULTAR ACTIVIDADES DESDE SQLITE
    // =====================================================

    private fun cargarActividadesSocio() {

        contenedorActividades.removeAllViews()

        val dbHelper = SocioDBHelper(this)

        val socio = try {

            when {

                usuarioDni.isNotBlank() ->
                    dbHelper.obtenerClientePorDni(usuarioDni)

                usuarioId.isNotBlank() ->
                    dbHelper.buscarClientePorCriterio(usuarioId)

                else -> null
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al consultar las actividades: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            null

        } finally {
            dbHelper.close()
        }

        // =====================================================
        // VERIFICAR CLIENTE
        // =====================================================

        if (socio == null) {

            mostrarMensaje(
                "No se encontró el socio en la base de datos"
            )

            return
        }

        // Actualizar nombre completo
        nombreSocio.text =
            "${socio.nombre} ${socio.apellido}".trim()

        // Obtener actividades recuperadas desde SQLite
        val listaActividades = socio.actividades

        // =====================================================
        // SI NO TIENE ACTIVIDADES
        // =====================================================
        val dbHelper = SocioDBHelper(this)
        val listaActividades = dbHelper.obtenerActividadesDeUsuario(usuarioDni)

        if (listaActividades.isEmpty()) {

            mostrarMensaje(
                "El socio no tiene actividades asignadas"
            )

            return
        }

        // =====================================================
        // MOSTRAR ACTIVIDADES
        // =====================================================

        for (actividad in listaActividades) {

            val botonActividad = MaterialButton(this).apply {

                text = actividad

                setTextColor(Color.BLACK)

                textSize = 16f

                setTypeface(null, Typeface.BOLD)

                setBackgroundColor(
                    Color.parseColor("#DCE7D4")
                )

                cornerRadius = 24

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120
                ).apply {
                    setMargins(0, 0, 0, 24)
                }

                setOnClickListener {

                    android.app.AlertDialog.Builder(this@ActivitysAsiggnedActivity)
                        .setTitle("Eliminar actividad")
                        .setMessage(
                            "¿Estás seguro de que deseas quitar la actividad '$actividad' de este socio?"
                        )
                        .setPositiveButton("Sí, eliminar") { _, _ ->

                            if (usuarioDni.isBlank()) {
                                Toast.makeText(
                                    this@ActivitysAsiggnedActivity,
                                    "No se pudo identificar el DNI del socio",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@setPositiveButton
                            }

                            val exito = try {
                                SocioDBHelper(this@ActivitysAsiggnedActivity)
                                    .quitarActividadASocio(usuarioDni, actividad)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ActivitysAsiggnedActivity,
                                    "Error al eliminar: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()

                                false
                            }

                            if (exito) {
                                Toast.makeText(
                                    this@ActivitysAsiggnedActivity,
                                    "Se eliminó la actividad $actividad",
                                    Toast.LENGTH_SHORT
                                ).show()

                                cargarActividadesSocio()

                            } else {
                                Toast.makeText(
                                    this@ActivitysAsiggnedActivity,
                                    "No se pudo eliminar la actividad",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }

            contenedorActividades.addView(botonActividad)
        }
    }

    // =====================================================
    // MOSTRAR MENSAJE CUANDO NO HAY ACTIVIDADES
    // =====================================================

    private fun mostrarMensaje(mensaje: String) {

        val tvVacio = TextView(this).apply {

            text = mensaje

            setTextColor(Color.GRAY)

            textSize = 16f

            setPadding(0, 32, 0, 0)
        }

        contenedorActividades.addView(tvVacio)
    }
                    val builder = android.app.AlertDialog.Builder(this@ActivitysAsiggnedActivity)
                    builder.setTitle("Eliminar actividad")
                    builder.setMessage("¿Estás seguro de que deseas quitar la actividad '$actividad' de este socio?")

                    builder.setPositiveButton("Sí, eliminar") { _, _ ->
                        val idActividad = dbHelper.obtenerIdActividadPorNombre(actividad)
                        val exito = dbHelper.desasignarActividadDeUsuario(usuarioDni, idActividad)

                        if (exito) {
                            Toast.makeText(context, "Se eliminó la actividad $actividad", Toast.LENGTH_SHORT).show()
                            cargarActividadesSocio(contenedor)
                        } else {
                            Toast.makeText(context, "No se pudo eliminar la actividad", Toast.LENGTH_SHORT).show()
                        }
                    }

                    builder.setNegativeButton("Cancelar", null)
                    builder.show()
                }
            }
            // Aquí se agrega correctamente dentro del for, pero fuera del setOnClickListener
            contenedor.addView(botonActividad)
        }
    }
}
