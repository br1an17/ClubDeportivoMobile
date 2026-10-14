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

    // Posición del comprobante que viene de la pantalla anterior
    private var posicionComprobante = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cantidad_cuotas)

        // =====================================================
        // CAMPOS DEL FORMULARIO
        // =====================================================

        etTarjeta = findViewById(R.id.etTarjeta)
        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta)
        etCodigoVerificacion = findViewById(R.id.etCodigoVerificacion)

        // =====================================================
        // BOTONES DE CUOTAS
        // =====================================================

        val btnUnPago = findViewById<Button>(R.id.btnUnPago)
        val btnTresCuotas = findViewById<Button>(R.id.btnTresCuotas)
        val btnSeisCuotas = findViewById<Button>(R.id.btnSeisCuotas)
        val btnNueveCuotas = findViewById<Button>(R.id.btnNueveCuotas)
        val btnDoceCuotas = findViewById<Button>(R.id.btnDoceCuotas)

        // =====================================================
        // BOTÓN VOLVER
        // =====================================================

        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // RECIBIR POSICIÓN DEL COMPROBANTE
        // =====================================================

        posicionComprobante = intent.getIntExtra(
            "POSICION_COMPROBANTE",
            0
        )

        // Verificar que la posición exista en DatosClub
        if (posicionComprobante !in DatosClub.comprobantes.indices) {
            posicionComprobante = 0
        }

        // =====================================================
        // 1 PAGO
        // =====================================================

        btnUnPago.setOnClickListener {
            seleccionarCuotas(1)
        }

        // =====================================================
        // 3 CUOTAS
        // =====================================================

        btnTresCuotas.setOnClickListener {
            seleccionarCuotas(3)
        }

        // =====================================================
        // 6 CUOTAS
        // =====================================================

        btnSeisCuotas.setOnClickListener {
            seleccionarCuotas(6)
        }

        // =====================================================
        // 9 CUOTAS
        // =====================================================

        btnNueveCuotas.setOnClickListener {
            seleccionarCuotas(9)
        }

        // =====================================================
        // 12 CUOTAS
        // =====================================================

        btnDoceCuotas.setOnClickListener {
            seleccionarCuotas(12)
        }

        // =====================================================
        // VOLVER A COBRO DE CUOTA
        // =====================================================

        btnVolver.setOnClickListener {

            val intent = Intent(
                this,
                CobroCuotaActivity::class.java
            )

            // Mantener el socio seleccionado
            val nombreUsuario =
                intent.getStringExtra("USUARIO_NOMBRE")

            val dniUsuario =
                intent.getStringExtra("USUARIO_DNI")

            if (nombreUsuario != null) {
                intent.putExtra(
                    "USUARIO_NOMBRE",
                    nombreUsuario
                )
            }

            if (dniUsuario != null) {
                intent.putExtra(
                    "USUARIO_DNI",
                    dniUsuario
                )
            }

            startActivity(intent)

            finish()
        }
    }

    private fun seleccionarCuotas(cantidad: Int) {

        // =====================================================
        // OBTENER DATOS INGRESADOS
        // =====================================================

        val tarjeta =
            etTarjeta.text.toString().trim()

        val numeroTarjeta =
            etNumeroTarjeta.text.toString().trim()

        val codigo =
            etCodigoVerificacion.text.toString().trim()

        // =====================================================
        // VALIDAR TARJETA
        // =====================================================

        if (tarjeta.isEmpty()) {

            etTarjeta.error =
                "Ingrese la tarjeta"

            etTarjeta.requestFocus()

            return
        }

        // =====================================================
        // VALIDAR NÚMERO DE TARJETA
        // =====================================================

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

        // =====================================================
        // VALIDAR CÓDIGO
        // =====================================================

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

        // =====================================================
        // GUARDAR CUOTAS
        // =====================================================

        cuotasSeleccionadas = cantidad

        // =====================================================
        // OBTENER COMPROBANTE CENTRAL
        // =====================================================

        val comprobante =
            DatosClub.comprobantes[posicionComprobante]

        // =====================================================
        // ABRIR COMPROBANTE
        // =====================================================

        val intent = Intent(
            this,
            ComprobantePagoActivity::class.java
        )

        // Mantener el mismo comprobante
        intent.putExtra(
            "POSICION_COMPROBANTE",
            posicionComprobante
        )

        // Cantidad de cuotas seleccionada
        intent.putExtra(
            "CUOTAS_SELECCIONADAS",
            cuotasSeleccionadas
        )

        // Forma de pago
        intent.putExtra(
            "FORMA_PAGO",
            "Tarjeta"
        )

        // Datos adicionales del comprobante
        intent.putExtra(
            "NUMERO",
            comprobante.numero
        )

        intent.putExtra(
            "CLIENTE",
            comprobante.cliente
        )

        intent.putExtra(
            "TIPO",
            comprobante.tipo
        )

        intent.putExtra(
            "CONCEPTO",
            comprobante.concepto
        )

        intent.putExtra(
            "ACTIVIDADES",
            comprobante.actividades
        )

        intent.putExtra(
            "MONTO",
            comprobante.monto
        )

        startActivity(intent)
    }
}