
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CantidadCuotasActivity : AppCompatActivity() {

    private lateinit var etTarjeta: EditText
    private lateinit var etNumeroTarjeta: EditText
    private lateinit var etCodigoVerificacion: EditText

    private var cuotasSeleccionadas = 0
    private var procesando = false

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

        val btnUnPago = findViewById<Button>(R.id.btnUnPago)
        val btnTresCuotas = findViewById<Button>(R.id.btnTresCuotas)
        val btnSeisCuotas = findViewById<Button>(R.id.btnSeisCuotas)
        val btnNueveCuotas = findViewById<Button>(R.id.btnNueveCuotas)
        val btnDoceCuotas = findViewById<Button>(R.id.btnDoceCuotas)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // SELECCIÓN DE CUOTAS
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
        // VOLVER A COBRO
        // =====================================================

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // =====================================================
    // VALIDAR Y REGISTRAR PAGO CON TARJETA
    // =====================================================

    private fun seleccionarCuotas(cantidad: Int) {

        if (procesando) return

        // =====================================================
        // VALIDAR TITULAR
        // =====================================================

        val tarjeta = etTarjeta.text.toString().trim()

        if (tarjeta.isBlank()) {
            etTarjeta.error = "Ingrese el titular de la tarjeta"
            etTarjeta.requestFocus()
            return
        }

        // =====================================================
        // VALIDAR NÚMERO DE TARJETA
        // =====================================================

        val numeroTarjeta =
            etNumeroTarjeta.text.toString().trim()

        if (numeroTarjeta.isBlank()) {
            etNumeroTarjeta.error = "Ingrese el número de tarjeta"
            etNumeroTarjeta.requestFocus()
            return
        }

        if (!numeroTarjeta.all { it.isDigit() }) {
            etNumeroTarjeta.error =
                "El número debe contener solamente números"
            etNumeroTarjeta.requestFocus()
            return
        }

        if (numeroTarjeta.length !in 13..19) {
            etNumeroTarjeta.error =
                "Ingrese un número de tarjeta válido"
            etNumeroTarjeta.requestFocus()
            return
        }

        // =====================================================
        // VALIDAR CÓDIGO DE VERIFICACIÓN
        // =====================================================

        val codigo =
            etCodigoVerificacion.text.toString().trim()

        if (codigo.isBlank()) {
            etCodigoVerificacion.error =
                "Ingrese el código de verificación"
            etCodigoVerificacion.requestFocus()
            return
        }

        if (!codigo.all { it.isDigit() } ||
            codigo.length !in 3..4
        ) {
            etCodigoVerificacion.error =
                "El código debe tener 3 o 4 números"
            etCodigoVerificacion.requestFocus()
            return
        }

        // =====================================================
        // RECUPERAR DATOS DEL CLIENTE
        // =====================================================

        cuotasSeleccionadas = cantidad

        val dni = intent.getStringExtra("USUARIO_DNI")
            .orEmpty()
            .trim()

        if (dni.isBlank()) {
            Toast.makeText(
                this,
                "No se recibió el DNI del cliente",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        procesando = true

        // =====================================================
        // REGISTRAR FACTURA Y PAGO EN SQLITE
        // =====================================================

        val dbHelper = SocioDBHelper(this)

        try {

            // Obtener cliente desde SQLite
            val socio = dbHelper.obtenerClientePorDni(dni)

            if (socio == null) {
                procesando = false

                Toast.makeText(
                    this,
                    "No se encontró el cliente en SQLite",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // Obtener actividades y precios desde SQLite
            val detalles =
                dbHelper.obtenerDetallesActividadesDeSocio(dni)

            if (detalles.isEmpty()) {
                procesando = false

                Toast.makeText(
                    this,
                    "El cliente no tiene actividades para cobrar",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // Calcular el total real
            val montoTotal = detalles.sumOf {
                it.precioUnitario * it.cantidad
            }

            if (montoTotal <= 0) {
                procesando = false

                Toast.makeText(
                    this,
                    "El monto del cobro no es válido",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // Generar número único de factura
            val numeroFactura =
                "FAC-${System.currentTimeMillis()}"

            // Obtener fecha de emisión
            val fechaActual = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            ).format(Date())

            // Registrar factura, detalles y pago
            // dentro de una única transacción SQLite
            val facturaId = dbHelper.registrarFacturaConPago(
                numeroFactura = numeroFactura,
                dniSocio = dni,
                fecha = fechaActual,
                formaPago = "Tarjeta",
                cantidadCuotas = cuotasSeleccionadas,
                detalles = detalles
            )

            if (facturaId <= 0L) {
                procesando = false

                Toast.makeText(
                    this,
                    "No se pudo registrar el pago en SQLite",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            // =================================================
            // PREPARAR DATOS DEL COMPROBANTE
            // =================================================

            val nombreCliente =
                "${socio.nombre} ${socio.apellido}".trim()

            val numeroSocio =
                socio.numeroAfiliado ?: "S/N"

            val tipo = intent.getStringExtra("TIPO")
                ?: if (socio.esSocio) "Cuota" else "Actividades"

            val concepto = intent.getStringExtra("CONCEPTO")
                ?: if (socio.esSocio) {
                    "Cuota mensual"
                } else {
                    "Pago de actividades"
                }

            val actividades = detalles.joinToString(", ") {
                it.descripcion
            }

            val montoTexto = String.format(
                Locale.US,
                "%.2f",
                montoTotal
            )

            // =================================================
            // ABRIR COMPROBANTE
            // =================================================

            val intentComprobante = Intent(
                this,
                ComprobantePagoActivity::class.java
            ).apply {

                putExtra("USUARIO_NOMBRE", nombreCliente)
                putExtra("USUARIO_DNI", dni)

                // Número de factura
                putExtra("NUMERO_FACTURA", numeroFactura)
                putExtra("FACTURA_ID", facturaId)

                // Número de socio
                putExtra("NUMERO", numeroSocio)

                putExtra("CLIENTE", nombreCliente)
                putExtra("TIPO", tipo)
                putExtra("CONCEPTO", concepto)
                putExtra("ACTIVIDADES", actividades)
                putExtra("MONTO", montoTexto)
                putExtra("FORMA_PAGO", "Tarjeta")
                putExtra(
                    "CUOTAS_SELECCIONADAS",
                    cuotasSeleccionadas
                )
            }

            Toast.makeText(
                this,
                "Pago con tarjeta registrado correctamente",
                Toast.LENGTH_LONG
            ).show()

            startActivity(intentComprobante)
            finish()

        } catch (e: Exception) {

            procesando = false

            Toast.makeText(
                this,
                "Error al registrar el pago: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

        } finally {
            dbHelper.close()
        }
    }
}
