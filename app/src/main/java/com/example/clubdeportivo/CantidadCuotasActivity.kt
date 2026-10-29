
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CantidadCuotasActivity : AppCompatActivity() {

    private lateinit var etTarjeta: EditText
    private lateinit var etNumeroTarjeta: EditText
    private lateinit var etCodigoVerificacion: EditText

    private var cuotasSeleccionadas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cantidad_cuotas)

        // =====================================================
        // CAMPOS DEL FORMULARIO
        // =====================================================

        etTarjeta = findViewById(R.id.etTarjeta)
        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta)
        etCodigoVerificacion =
            findViewById(R.id.etCodigoVerificacion)

        // =====================================================
        // BOTONES DE CUOTAS
        // =====================================================

        val btnUnPago =
            findViewById<Button>(R.id.btnUnPago)

        val btnTresCuotas =
            findViewById<Button>(R.id.btnTresCuotas)

        val btnSeisCuotas =
            findViewById<Button>(R.id.btnSeisCuotas)

        val btnNueveCuotas =
            findViewById<Button>(R.id.btnNueveCuotas)

        val btnDoceCuotas =
            findViewById<Button>(R.id.btnDoceCuotas)

        val btnVolver =
            findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // SELECCIÓN DE CANTIDAD DE CUOTAS
        // =====================================================

        btnUnPago.setOnClickListener {
            seleccionarCuotas(1)
        }

        btnTresCuotas.setOnClickListener {
            seleccionarCuotas(3)
        }

        btnSeisCuotas.setOnClickListener {
            seleccionarCuotas(6)
        }

        btnNueveCuotas.setOnClickListener {
            seleccionarCuotas(9)
        }

        btnDoceCuotas.setOnClickListener {
            seleccionarCuotas(12)
        }

        // =====================================================
        // VOLVER A COBRO DE CUOTA
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // =====================================================
    // VALIDAR TARJETA Y ABRIR COMPROBANTE
    // =====================================================

    private fun seleccionarCuotas(cantidad: Int) {

        // Obtener datos ingresados
        val tarjeta =
            etTarjeta.text.toString().trim()

        val numeroTarjeta =
            etNumeroTarjeta.text.toString().trim()

        val codigo =
            etCodigoVerificacion.text.toString().trim()

        // Validar titular de tarjeta
        if (tarjeta.isEmpty()) {
            etTarjeta.error = "Ingrese el titular de la tarjeta"
            etTarjeta.requestFocus()
            return
        }

        // Validar número de tarjeta
        if (numeroTarjeta.isEmpty()) {
            etNumeroTarjeta.error =
                "Ingrese el número de tarjeta"
            etNumeroTarjeta.requestFocus()
            return
        }

        if (!numeroTarjeta.all { it.isDigit() }) {
            etNumeroTarjeta.error =
                "El número debe contener solo números"
            etNumeroTarjeta.requestFocus()
            return
        }

        if (numeroTarjeta.length < 13) {
            etNumeroTarjeta.error =
                "Ingrese un número de tarjeta válido"
            etNumeroTarjeta.requestFocus()
            return
        }

        // Validar código de verificación
        if (codigo.isEmpty()) {
            etCodigoVerificacion.error =
                "Ingrese el código de verificación"
            etCodigoVerificacion.requestFocus()
            return
        }

        if (!codigo.all { it.isDigit() }) {
            etCodigoVerificacion.error =
                "El código debe contener solo números"
            etCodigoVerificacion.requestFocus()
            return
        }

        // Guardar cantidad elegida
        cuotasSeleccionadas = cantidad

        // =====================================================
        // RECUPERAR DATOS DEL CLIENTE
        // =====================================================

        val datosOrigen = this.intent

        val nombre =
            datosOrigen.getStringExtra("USUARIO_NOMBRE")
                .orEmpty()

        val dni =
            datosOrigen.getStringExtra("USUARIO_DNI")
                .orEmpty()

        val numero =
            datosOrigen.getStringExtra("NUMERO")
                ?: "S/N"

        val cliente =
            datosOrigen.getStringExtra("CLIENTE")
                ?.takeIf { it.isNotBlank() }
                ?: nombre

        val tipo =
            datosOrigen.getStringExtra("TIPO")
                ?: "Cuota"

        val concepto =
            datosOrigen.getStringExtra("CONCEPTO")
                ?: "Cuota mensual"

        val actividades =
            datosOrigen.getStringExtra("ACTIVIDADES")
                .orEmpty()

        val monto =
            datosOrigen.getStringExtra("MONTO")
                .orEmpty()

        // =====================================================
        // ABRIR COMPROBANTE CON LOS DATOS DEL CLIENTE ACTUAL
        // =====================================================

        val intentComprobante = Intent(
            this,
            ComprobantePagoActivity::class.java
        )

        intentComprobante.putExtra(
            "USUARIO_NOMBRE",
            nombre
        )

        intentComprobante.putExtra(
            "USUARIO_DNI",
            dni
        )

        intentComprobante.putExtra(
            "NUMERO",
            numero
        )

        intentComprobante.putExtra(
            "CLIENTE",
            cliente
        )

        intentComprobante.putExtra(
            "TIPO",
            tipo
        )

        intentComprobante.putExtra(
            "CONCEPTO",
            concepto
        )

        intentComprobante.putExtra(
            "ACTIVIDADES",
            actividades
        )

        intentComprobante.putExtra(
            "MONTO",
            monto
        )

        intentComprobante.putExtra(
            "FORMA_PAGO",
            "Tarjeta"
        )

        intentComprobante.putExtra(
            "CUOTAS_SELECCIONADAS",
            cuotasSeleccionadas
        )

        startActivity(intentComprobante)
    }
}
