package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CarnetSocioActivity : AppCompatActivity() {

    // Variables para las vistas
    private lateinit var tvNumAfiliado: TextView
    private lateinit var tvNombreSocio: TextView
    private lateinit var tvDocumentoSocio: TextView
    private lateinit var tvEstadoSocio: TextView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet_socio)

        // 1. Inicializar referencias de la interfaz
        tvNumAfiliado = findViewById(R.id.tvNumAfiliado)
        tvNombreSocio = findViewById(R.id.tvNombreSocio)
        tvDocumentoSocio = findViewById(R.id.tvDocumentoSocio)
        tvEstadoSocio = findViewById(R.id.tvEstadoSocio)
        btnVolver = findViewById(R.id.btnVolverCarnet)

        // 2. Cargar los datos recibidos del socio
        cargarDatosSocio()

        // 3. Configurar el botón "Volver" para ir al menú principal (HomeActivity)
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosSocio() {
        val numAfiliado = intent.getStringExtra("NUM_AFILIADO") ?: "N/A"
        val nombre = intent.getStringExtra("NOMBRE_SOCIO") ?: "Socio No Registrado"
        val documento = intent.getStringExtra("DOCUMENTO_SOCIO") ?: "N/A"
        val estado = intent.getStringExtra("ESTADO_SOCIO") ?: "Inactivo"


        tvNumAfiliado.text = numAfiliado
        tvNombreSocio.text = nombre
        tvDocumentoSocio.text = documento
        tvEstadoSocio.text = estado
    }
}