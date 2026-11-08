package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity: AppCompatActivity(){

    private var usuarioNombre: String = ""
    private var usuarioDni: String = ""
    private var usuarioId: String = ""
    private var actividadNombre: String = ""
    private var actividadHorario: String = ""
    private var actividadProfesor: String = ""

    private var actividadCosto: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activitys)

        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: ""
        usuarioDni = intent.getStringExtra("USUARIO_DNI") ?: ""
        usuarioId = intent.getStringExtra("USUARIO_ID") ?: ""
        actividadNombre = intent.getStringExtra("ACTIVIDAD_NOMBRE") ?: "Sin Actividad"
        actividadHorario = intent.getStringExtra("ACTIVIDAD_HORARIO") ?: "20:00"
        actividadProfesor = intent.getStringExtra("ACTIVIDAD_PROFESOR") ?: "Por Asignar"
        actividadCosto = intent.getDoubleExtra("ACTIVIDAD_VALOR", 10000.0)

        val tvNombreActividad = findViewById<TextView>(R.id.tvNombreActividad)
        val tvHorarioActividad = findViewById<TextView>(R.id.tvHorarioActividad)
        val tvNombreProfesor = findViewById<TextView>(R.id.tvNombreProfesor)
        val tvValorActividad = findViewById<TextView>(R.id.tvValor)
        val btnConfirmarAsignar = findViewById<Button>(R.id.btnConfirmarAsignar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        tvNombreActividad.text = actividadNombre
        tvHorarioActividad.text = actividadHorario
        tvNombreProfesor.text = actividadProfesor
        tvValorActividad.text = "$ $actividadCosto"

        btnConfirmarAsignar.setOnClickListener {
            asignarActividadASocio()
        }

        btnVolver.setOnClickListener {
            finish()
        }}
        private fun asignarActividadASocio() {
            val dbHelper = SocioDBHelper(this)

            val idActividad = dbHelper.obtenerIdActividadPorNombre(actividadNombre)

            if (usuarioDni.isNotEmpty() && idActividad != -1) {

                val clienteEncontrado = dbHelper.buscarClientePorCriterio(usuarioDni)

                if (clienteEncontrado != null) {
                    val exito = dbHelper.asignarActividadUsuario(usuarioDni, idActividad)

                    if (exito) {
                        Toast.makeText(
                            this,
                            "¡Actividad $actividadNombre asignada con éxito!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish() // Cerramos el detalle y volvemos
                    } else {
                        Toast.makeText(
                            this,
                            "El socio ya tiene asignada esta actividad o hubo un error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "No se encontró el socio en la base de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Error: DNI de socio inválido o actividad no registrada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
