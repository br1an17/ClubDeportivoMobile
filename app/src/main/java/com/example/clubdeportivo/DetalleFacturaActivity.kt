
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
import java.util.Locale

class DetalleFacturaActivity : AppCompatActivity() {

    private lateinit var tvNumero: TextView
    private lateinit var tvCliente: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvFormaPago: TextView
    private lateinit var tvCuotas: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvTotal: TextView
    private lateinit var contenedorDetalles: LinearLayout

    private var numeroFactura = ""
    private var dniSocio = ""
    private var nombreCliente = ""
    private var fechaFactura = ""
    private var formaPago = ""
    private var cantidadCuotas = 1
    private var totalFactura = 0.0
    private var estadoFactura = ""

    private var actividadesFactura = ""

    private val formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_factura)

        // ELEMENTOS DE LA PANTALLA

        tvNumero = findViewById(R.id.tvNumeroDetalle)
        tvCliente = findViewById(R.id.tvClienteDetalle)
        tvFecha = findViewById(R.id.tvFechaDetalle)
        tvFormaPago = findViewById(R.id.tvFormaPagoDetalle)
        tvCuotas = findViewById(R.id.tvCuotasDetalle)
        tvEstado = findViewById(R.id.tvEstadoDetalle)
        tvTotal = findViewById(R.id.tvTotalDetalle)

        contenedorDetalles =
            findViewById(R.id.contenedorDetallesFactura)

        val btnComprobante =
            findViewById<Button>(R.id.btnVerComprobanteDetalle)

        val btnVolver =
            findViewById<Button>(R.id.btnVolverDetalle)

        btnVolver.setOnClickListener {
            finish()
        }

        val facturaId =
            intent.getLongExtra("FACTURA_ID", -1L)

        if (facturaId <= 0L) {
            mostrarError("No se recibió una factura válida")
            btnComprobante.isEnabled = false
            return
        }

        val facturaEncontrada =
            cargarDetalleFactura(facturaId)

        if (!facturaEncontrada) {
            btnComprobante.isEnabled = false
            return
        }

        btnComprobante.setOnClickListener {
            abrirComprobante()
        }
    }

    // =====================================================
    // CONSULTAR FACTURA Y DETALLES EN SQLITE
    // =====================================================

    private fun cargarDetalleFactura(facturaId: Long): Boolean {

        val dbHelper = SocioDBHelper(this)

        return try {

            val db = dbHelper.readableDatabase

            // DATOS GENERALES DE LA FACTURA

            val consultaFactura = """
                SELECT
                    f.${SocioDBHelper.COLUMN_FACTURA_NUMERO},
                    f.${SocioDBHelper.COLUMN_FACTURA_DNI},
                    f.${SocioDBHelper.COLUMN_FACTURA_FECHA},
                    f.${SocioDBHelper.COLUMN_FACTURA_TOTAL},
                    f.${SocioDBHelper.COLUMN_FACTURA_MEDIO_PAGO},
                    f.${SocioDBHelper.COLUMN_FACTURA_CUOTAS},
                    f.${SocioDBHelper.COLUMN_FACTURA_ESTADO},
                    u.${SocioDBHelper.COLUM_NOMBRE},
                    u.${SocioDBHelper.COLUM_APELLIDO}
                FROM ${SocioDBHelper.TABLE_FACTURAS} f
                LEFT JOIN ${SocioDBHelper.TABLE_USUARIOS} u
                    ON f.${SocioDBHelper.COLUMN_FACTURA_DNI}
                    = u.${SocioDBHelper.COLUM_DNI}
                WHERE f.${SocioDBHelper.COLUMN_FACTURA_ID} = ?
                LIMIT 1
            """.trimIndent()

            val cursorFactura = db.rawQuery(
                consultaFactura,
                arrayOf(facturaId.toString())
            )

            cursorFactura.use {

                if (!it.moveToFirst()) {
                    mostrarError("No se encontró la factura")
                    return false
                }

                numeroFactura = it.getString(0) ?: "S/N"
                dniSocio = it.getString(1) ?: ""
                fechaFactura = it.getString(2) ?: ""
                totalFactura = it.getDouble(3)
                formaPago = it.getString(4) ?: "Sin especificar"
                cantidadCuotas = it.getInt(5).coerceAtLeast(1)
                estadoFactura = it.getString(6) ?: "Sin estado"

                val nombre = it.getString(7).orEmpty()
                val apellido = it.getString(8).orEmpty()

                nombreCliente =
                    "$nombre $apellido".trim().ifBlank {
                        "Cliente no identificado"
                    }
            }

            // DETALLES DE LA FACTURA

            val consultaDetalles = """
                SELECT
                    ${SocioDBHelper.COLUMN_DETALLE_CONCEPTO},
                    ${SocioDBHelper.COLUMN_DETALLE_DESCRIPCION},
                    ${SocioDBHelper.COLUMN_DETALLE_PRECIO},
                    ${SocioDBHelper.COLUMN_DETALLE_CANTIDAD},
                    ${SocioDBHelper.COLUMN_DETALLE_SUBTOTAL}
                FROM ${SocioDBHelper.TABLE_DETALLE_FACTURA}
                WHERE ${SocioDBHelper.COLUMN_DETALLE_FACTURA_ID} = ?
                ORDER BY ${SocioDBHelper.COLUMN_DETALLE_ID} ASC
            """.trimIndent()

            val cursorDetalles = db.rawQuery(
                consultaDetalles,
                arrayOf(facturaId.toString())
            )

            val nombresActividades = mutableListOf<String>()

            cursorDetalles.use {

                if (it.count == 0) {
                    agregarMensaje(
                        "Esta factura no tiene conceptos detallados."
                    )
                }

                while (it.moveToNext()) {

                    val concepto = it.getString(0) ?: ""
                    val descripcion = it.getString(1) ?: ""
                    val precioUnitario = it.getDouble(2)
                    val cantidad = it.getInt(3)
                    val subtotal = it.getDouble(4)

                    agregarConcepto(
                        concepto,
                        descripcion,
                        precioUnitario,
                        cantidad,
                        subtotal
                    )

                    if (descripcion.isNotBlank()) {
                        nombresActividades.add(descripcion)
                    }
                }
            }

            actividadesFactura =
                nombresActividades.joinToString(", ")

            // MOSTRAR DATOS GENERALES

            tvNumero.text = numeroFactura

            tvCliente.text =
                "$nombreCliente\nDNI: $dniSocio"

            tvFecha.text = fechaFactura
            tvFormaPago.text = formaPago

            tvCuotas.text =
                "$cantidadCuotas (${formatoMoneda.format(
                    totalFactura / cantidadCuotas
                )} cada una)"

            tvEstado.text = estadoFactura
            tvTotal.text = formatoMoneda.format(totalFactura)

            true

        } catch (e: Exception) {

            mostrarError(
                "Error al consultar la factura: ${e.message}"
            )

            false

        } finally {
            dbHelper.close()
        }
    }

    // =====================================================
    // MOSTRAR CADA CONCEPTO
    // =====================================================

    private fun agregarConcepto(
        concepto: String,
        descripcion: String,
        precioUnitario: Double,
        cantidad: Int,
        subtotal: Double
    ) {

        val texto = """
            $descripcion

            Concepto: $concepto
            Precio unitario: ${formatoMoneda.format(precioUnitario)}
            Cantidad: $cantidad
            Subtotal: ${formatoMoneda.format(subtotal)}
        """.trimIndent()

        val tarjeta = TextView(this).apply {

            text = texto
            textSize = 15f
            setTextColor(Color.WHITE)

            setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
            )

            setBackgroundColor(
                Color.rgb(35, 48, 35)
            )

            setLineSpacing(
                dp(3).toFloat(),
                1f
            )
        }

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        parametros.bottomMargin = dp(12)

        contenedorDetalles.addView(
            tarjeta,
            parametros
        )
    }

    // =====================================================
    // ABRIR COMPROBANTE ORIGINAL
    // =====================================================

    private fun abrirComprobante() {

        val tipo = "Actividades"
        val concepto = "Pago de actividades"

        val montoTexto = String.format(
            Locale.US,
            "%.2f",
            totalFactura
        )

        val intentComprobante = Intent(
            this,
            ComprobantePagoActivity::class.java
        ).apply {

            putExtra("USUARIO_NOMBRE", nombreCliente)
            putExtra("USUARIO_DNI", dniSocio)

            putExtra("NUMERO_FACTURA", numeroFactura)
            putExtra("CLIENTE", nombreCliente)
            putExtra("TIPO", tipo)
            putExtra("CONCEPTO", concepto)
            putExtra("ACTIVIDADES", actividadesFactura)
            putExtra("MONTO", montoTexto)
            putExtra("FORMA_PAGO", formaPago)
            putExtra("CUOTAS_SELECCIONADAS", cantidadCuotas)
        }

        startActivity(intentComprobante)
    }

    // =====================================================
    // MOSTRAR MENSAJES
    // =====================================================

    private fun agregarMensaje(mensaje: String) {

        val texto = TextView(this).apply {
            text = mensaje
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        contenedorDetalles.addView(texto)
    }

    private fun mostrarError(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}
