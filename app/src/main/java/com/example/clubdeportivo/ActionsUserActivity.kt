
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActionsUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_actions_user)

        // ELEMENTOS DE LA PANTALLA

        val tvNombreSocio =
            findViewById<TextView>(R.id.tvNombreSocio)

        val btnAsignarActividades =
            findViewById<Button>(R.id.btnAsignarActividades)

        val btnActividadesAsignadas =
            findViewById<Button>(R.id.btnVerActividades)

        val btnCobrarCuota =
            findViewById<Button>(R.id.btnCobrarCuota)

        val btnCarnet =
            findViewById<Button>(R.id.btnCarnet)

        val btnHistorialPagos =
            findViewById<Button>(R.id.btnHistorialPagos)

        val btnVolver =
            findViewById<Button>(R.id.btnVolver)

        // RECIBIR DATOS

        val nombreRecibido =
            intent.getStringExtra("USUARIO_NOMBRE") ?: ""

        val dniRecibido =
            intent.getStringExtra("USUARIO_DNI") ?: ""

        val idRecibido =
            intent.getStringExtra("USUARIO_ID") ?: ""

        // BUSCAR SOCIO EN SQLITE

        val dbHelper = SocioDBHelper(this)

        val socio = try {

            dbHelper.cargarDatosInicialesSiFaltan()

            when {
                dniRecibido.isNotBlank() ->
                    dbHelper.obtenerClientePorDni(dniRecibido)

                idRecibido.isNotBlank() ->
                    dbHelper.buscarClientePorCriterio(idRecibido)

                else -> null
            }

        } finally {
            dbHelper.close()
        }

        // DATOS DEL SOCIO

        val nombreFinal = socio?.let {
            "${it.nombre} ${it.apellido}".trim()
        } ?: nombreRecibido

        val dniFinal = socio?.dni ?: dniRecibido
        val idFinal = socio?.numeroAfiliado ?: idRecibido

        tvNombreSocio.text =
            nombreFinal.ifBlank { "Socio no identificado" }

        // ASIGNAR ACTIVIDADES

        btnAsignarActividades.setOnClickListener {

            val intent = Intent(
                this,
                AssignActivityActivity::class.java
            )

            intent.putExtra("USUARIO_NOMBRE", nombreFinal)
            intent.putExtra("USUARIO_DNI", dniFinal)
            intent.putExtra("USUARIO_ID", idFinal)

            startActivity(intent)
        }

        // VER ACTIVIDADES ASIGNADAS

        btnActividadesAsignadas.setOnClickListener {

            val intent = Intent(
                this,
                ActivitysAsiggnedActivity::class.java
            )

            intent.putExtra("USUARIO_NOMBRE", nombreFinal)
            intent.putExtra("USUARIO_DNI", dniFinal)
            intent.putExtra("USUARIO_ID", idFinal)

            startActivity(intent)
        }

        // COBRAR CUOTA

        btnCobrarCuota.setOnClickListener {

            val intent = Intent(
                this,
                CobroCuotaActivity::class.java
            )

            intent.putExtra("USUARIO_NOMBRE", nombreFinal)
            intent.putExtra("USUARIO_DNI", dniFinal)
            intent.putExtra("USUARIO_ID", idFinal)

            startActivity(intent)
        }

        // HISTORIAL DE PAGOS

        btnHistorialPagos.setOnClickListener {

            if (dniFinal.isBlank()) {
                Toast.makeText(
                    this,
                    "No se encontró el DNI del socio",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(
                this,
                HistorialPagosActivity::class.java
            )

            intent.putExtra("USUARIO_NOMBRE", nombreFinal)
            intent.putExtra("USUARIO_DNI", dniFinal)
            intent.putExtra("USUARIO_ID", idFinal)

            startActivity(intent)
        }

        // CARNET DE SOCIO

        btnCarnet.setOnClickListener {

            val estadoTexto =
                if (socio?.cuotaAlDia == true) {
                    "Al día"
                } else {
                    "Pendiente"
                }

            val intent = Intent(
                this,
                CarnetSocioActivity::class.java
            )

            intent.putExtra("NUM_AFILIADO", idFinal)
            intent.putExtra("NOMBRE_SOCIO", nombreFinal)
            intent.putExtra("DOCUMENTO_SOCIO", dniFinal)
            intent.putExtra("ESTADO_SOCIO", estadoTexto)
            intent.putExtra("EMAIL_SOCIO", socio?.email ?: "")

            startActivity(intent)
        }

        // VOLVER

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
