
package com.example.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialPagosActivity : AppCompatActivity() {

    private lateinit var contenedorHistorial: LinearLayout
    private lateinit var tvCantidad: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historial_pagos)

        val tvNombre =
            findViewById<TextView>(R.id.tvNombreHistorial)

        tvCantidad =
            findViewById(R.id.tvCantidadHistorial)

        contenedorHistorial =
            findViewById(R.id.contenedorHistorial)

        val btnVolver =
            findViewById<Button>(R.id.btnVolverHistorial)

        val nombre =
            intent.getStringExtra("USUARIO_NOMBRE").orEmpty()

        val dni =
            intent.getStringExtra("USUARIO_DNI").orEmpty()

        tvNombre.text = "$nombre\nDNI: $dni"

        btnVolver.setOnClickListener {
            finish()
        }

        if (dni.isBlank()) {
            mostrarMensaje("No se recibió el DNI del socio")
            return
        }

        cargarHistorial(dni)
    }

    // =====================================================
    // CARGAR HISTORIAL DESDE SQLITE
    // =====================================================

    private fun cargarHistorial(dni: String) {

        val dbHelper = SocioDBHelper(this)

        var cantidadRegistros = 0

        try {

            val db = dbHelper.readableDatabase

            val columnas = arrayOf(
                SocioDBHelper.COLUMN_FACTURA_ID,
                SocioDBHelper.COLUMN_FACTURA_NUMERO,
                SocioDBHelper.COLUMN_FACTURA_FECHA,
                SocioDBHelper.COLUMN_FACTURA_TOTAL,
                SocioDBHelper.COLUMN_FACTURA_MEDIO_PAGO,
                SocioDBHelper.COLUMN_FACTURA_CUOTAS,
                SocioDBHelper.COLUMN_FACTURA_ESTADO
            )

            val cursor = db.query(
                SocioDBHelper.TABLE_FACTURAS,
                columnas,
                "${SocioDBHelper.COLUMN_FACTURA_DNI} = ?",
                arrayOf(dni),
                null,
                null,
                "${SocioDBHelper.COLUMN_FACTURA_ID} DESC"
            )

            cursor.use {

                if (it.count == 0) {
                    mostrarMensaje(
                        "Este socio todavía no tiene pagos registrados."
                    )
                    tvCantidad.text = "0 pagos encontrados"
                    return
                }

                while (it.moveToNext()) {

                    val facturaId = it.getLong(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_ID
                        )
                    )

                    val numero = it.getString(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_NUMERO
                        )
                    ) ?: "S/N"

                    val fecha = it.getString(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_FECHA
                        )
                    ) ?: ""

                    val total = it.getDouble(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_TOTAL
                        )
                    )

                    val medioPago = it.getString(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_MEDIO_PAGO
                        )
                    ) ?: "Sin especificar"

                    val cuotas = it.getInt(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_CUOTAS
                        )
                    ).coerceAtLeast(1)

                    val estado = it.getString(
                        it.getColumnIndexOrThrow(
                            SocioDBHelper.COLUMN_FACTURA_ESTADO
                        )
                    ) ?: "Sin estado"

                    agregarFactura(
                        facturaId,
                        numero,
                        fecha,
                        total,
                        medioPago,
                        cuotas,
                        estado
                    )

                    cantidadRegistros++
                }
            }

            tvCantidad.text =
                "$cantidadRegistros pagos encontrados"

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al consultar el historial: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

        } finally {
            dbHelper.close()
        }
    }

    // =====================================================
    // AGREGAR FACTURA SELECCIONABLE
    // =====================================================

    private fun agregarFactura(
        facturaId: Long,
        numero: String,
        fecha: String,
        total: Double,
        medioPago: String,
        cuotas: Int,
        estado: String
    ) {

        val formatoMoneda =
            NumberFormat.getCurrencyInstance(Locale("es", "AR"))

        val montoTexto = formatoMoneda.format(total)
        val valorCuota = formatoMoneda.format(total / cuotas)
        val fechaTexto = formatearFecha(fecha)

        val texto = """
            FACTURA: $numero

            Fecha: $fechaTexto
            Importe total: $montoTexto
            Medio de pago: $medioPago
            Cuotas: $cuotas
            Valor por cuota: $valorCuota
            Estado: $estado

            Tocar para ver el detalle
        """.trimIndent()

        val tarjeta = TextView(this).apply {

            this.text = texto
            setTextColor(Color.WHITE)
            textSize = 15f

            setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
            )

            setBackgroundColor(
                Color.rgb(35, 48, 35)
            )

            setLineSpacing(
                dp(3).toFloat(),
                1f
            )

            isClickable = true
            isFocusable = true

            setOnClickListener {

                val intent = Intent(
                    this@HistorialPagosActivity,
                    DetalleFacturaActivity::class.java
                )

                intent.putExtra("FACTURA_ID", facturaId)

                startActivity(intent)
            }
        }

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        parametros.bottomMargin = dp(14)

        contenedorHistorial.addView(
            tarjeta,
            parametros
        )
    }

    // =====================================================
    // FORMATEAR FECHA
    // =====================================================

    private fun formatearFecha(fecha: String): String {

        return try {

            val entrada = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            )

            val salida = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )

            val fechaConvertida = entrada.parse(fecha)

            fechaConvertida?.let {
                salida.format(it)
            } ?: fecha

        } catch (e: Exception) {
            fecha
        }
    }

    // =====================================================
    // MENSAJE SIN RESULTADOS
    // =====================================================

    private fun mostrarMensaje(mensaje: String) {

        val texto = TextView(this).apply {
            text = mensaje
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(dp(16), dp(20), dp(16), dp(20))
        }

        contenedorHistorial.addView(texto)
    }

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}
