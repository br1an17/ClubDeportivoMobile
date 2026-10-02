package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SocioCuotaVenceHoyActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etAfiliado: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_cuota_vence_hoy)

        etNombre = findViewById(R.id.etNombre)
        etAfiliado = findViewById(R.id.etAfiliado)

        val btnCobrarCuota = findViewById<Button>(R.id.btnCobrarCuota)
        val btnEnviarAlerta = findViewById<Button>(R.id.btnEnviarAlerta)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // OBTENER SOCIO DESDE DATOSCLUB
        // =====================================================

        // Primero intentamos recibir el número de afiliado
        // desde la pantalla anterior.
        val afiliadoRecibido = intent.getStringExtra("SOCIO_AFILIADO")

        // Buscamos ese socio en el array central.
        val socio = if (afiliadoRecibido != null) {
            DatosClub.socios.find {
                it.numeroAfiliado == afiliadoRecibido
            }
        } else {
            null
        }

        // Si encontramos el socio usamos sus datos.
        // Si no, usamos el primer socio del array como prueba.
        val socioSeleccionado = socio ?: DatosClub.socios[0]

        // =====================================================
        // MOSTRAR DATOS
        // =====================================================

        etNombre.setText(socioSeleccionado.nombre)
        etAfiliado.setText(socioSeleccionado.numeroAfiliado)

        // Los datos del socio no se pueden modificar.
        etNombre.isFocusable = false
        etNombre.isClickable = false
        etNombre.isCursorVisible = false

        etAfiliado.isFocusable = false
        etAfiliado.isClickable = false
        etAfiliado.isCursorVisible = false

        // =====================================================
        // COBRAR CUOTA
        // =====================================================

        btnCobrarCuota.setOnClickListener {

            val intent = Intent(
                this,
                CobroCuotaActivity::class.java
            )

            // Pasamos los datos del mismo socio
            // a la pantalla de cobro.
            intent.putExtra(
                "USUARIO_NOMBRE",
                socioSeleccionado.nombre
            )

            intent.putExtra(
                "USUARIO_DNI",
                socioSeleccionado.dni
            )

            startActivity(intent)
        }

        // =====================================================
        // ENVIAR ALERTA
        // =====================================================

        btnEnviarAlerta.setOnClickListener {

            Toast.makeText(
                this,
                "Alerta enviada al socio ${socioSeleccionado.nombre}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================================
        // VOLVER
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }
}