package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetalleCuotaVencidaActivity : AppCompatActivity() {

    private var usuarioNombre: String = ""
    private var usuarioDni: String = ""
    private var usuarioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_cuota_vencida)

        // Capturar los extras enviados
        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: ""
        usuarioDni = intent.getStringExtra("USUARIO_DNI") ?: ""
        usuarioId = intent.getStringExtra("USUARIO_ID") ?: ""
        val fechaVencimiento = intent.getStringExtra("FECHA_VENCIMIENTO") ?: "10/05/2026"


        // Referenciar los TextViews del XML
        val tvNombre = findViewById<TextView>(R.id.tvNombreSocio)
        val tvAfiliado = findViewById<TextView>(R.id.tvNumAfiliado)
        val tvFecha = findViewById<TextView>(R.id.tvFechaVencimiento)

        val btnCobrar = findViewById<Button>(R.id.btnCobrarCuota)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Mostrar los datos en pantalla
        tvNombre.text = usuarioNombre
        tvAfiliado.text = usuarioId.ifEmpty { usuarioDni }
        tvFecha.text = fechaVencimiento

        // Evento Cobrar Cuota
        btnCobrar.setOnClickListener {
            Toast.makeText(this, "Cobrando cuota a $usuarioNombre...", Toast.LENGTH_SHORT).show()
            // Acá podés redirigir a la pantalla de Pago/Comprobante si la tienen
        }

        // Botón Volver (Regresa a la lista de socios)
        btnVolver.setOnClickListener {
            finish()
        }
    }
}