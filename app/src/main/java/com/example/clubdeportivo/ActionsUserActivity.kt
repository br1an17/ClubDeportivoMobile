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
        val btnCobrarCuota = findViewById<Button>(R.id.btnCobrarCuota)

        val btnCarnet = findViewById<Button>(R.id.btnCarnet)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        val nombreRecibido = intent.getStringExtra("USUARIO_NOMBRE") ?: "Socio no identificado"
        val dniRecibido = intent.getStringExtra("USUARIO_DNI") ?: ""
        val idRecibido = intent.getStringExtra("USUARIO_ID")?:""
        tvNombreSocio.text = nombreRecibido

        val socio = DatosClub.socios.find {
            (dniRecibido.isNotEmpty() && it.dni == dniRecibido) ||
                    (idRecibido.isNotEmpty() && it.numeroAfiliado == idRecibido) ||
                    (nombreRecibido.isNotEmpty() && it.nombre.equals(nombreRecibido, ignoreCase = true))
        }
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
        btnCobrarCuota.setOnClickListener {
            val intent = Intent(
                this,
                CobroCuotaActivity::class.java
            )
            intent.putExtra("USUARIO_NOMBRE", nombreRecibido)
            intent.putExtra("USUARIO_DNI", dniRecibido)
            intent.putExtra("USUARIO_ID", idRecibido)
            startActivity(intent)
        }
        btnCarnet.setOnClickListener {
            val intent = Intent(this, CarnetSocioActivity::class.java)


            val estadoTexto = if (socio?.cuotaAlDia == true) "Al día" else "Pendiente"

            intent.putExtra("NUM_AFILIADO", socio?.numeroAfiliado ?: idRecibido)
            intent.putExtra("NOMBRE_SOCIO", socio?.nombre ?: nombreRecibido)
            intent.putExtra("DOCUMENTO_SOCIO", socio?.dni ?: dniRecibido)
            intent.putExtra("ESTADO_SOCIO", estadoTexto)
            startActivity(intent)
        }
        btnVolver.setOnClickListener {
            finish()
        }
    }
}