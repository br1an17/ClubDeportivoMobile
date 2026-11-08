
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

class CobroCuotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etMonto: EditText

    private var medioPago = ""

    private var socioSeleccionado: ClienteClub? = null

    private var detallesCobro: List<DetalleFacturaInput> = emptyList()

    private var facturaIdRegistrada: Long? = null
    private var numeroFacturaRegistrada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cobro_cuota)

        etNombre = findViewById(R.id.etNombre)
        etDni = findViewById(R.id.etDni)
        etMonto = findViewById(R.id.etMonto)

        val btnTarjeta = findViewById<Button>(R.id.btnTarjeta)
        val btnEfectivo = findViewById<Button>(R.id.btnEfectivo)
        val btnCobrar = findViewById<Button>(R.id.btnCobrar)
        val btnComprobante = findViewById<Button>(R.id.btnComprobante)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // =====================================================
        // RECIBIR DATOS DEL CLIENTE
        // =====================================================

        val nombreUsuario =
            intent.getStringExtra("USUARIO_NOMBRE")

        val dniUsuario =
            intent.getStringExtra("USUARIO_DNI")

        val idUsuario =
            intent.getStringExtra("USUARIO_ID")

        // =====================================================
        // BUSCAR CLIENTE Y ACTIVIDADES EN SQLITE
        // =====================================================

        val dbHelper = SocioDBHelper(this)

        try {

            socioSeleccionado = when {

                !dniUsuario.isNullOrBlank() ->
                    dbHelper.obtenerClientePorDni(dniUsuario)

                !idUsuario.isNullOrBlank() ->
                    dbHelper.buscarClientePorCriterio(idUsuario)

                else -> null
            }

            socioSeleccionado?.let { socio ->

                detallesCobro =
                    dbHelper.obtenerDetallesActividadesDeSocio(socio.dni)
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al consultar SQLite: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

        } finally {
            dbHelper.close()
        }

        // =====================================================
        // MOSTRAR DATOS DEL CLIENTE
        // =====================================================

        val socio = socioSeleccionado

        if (socio != null) {

            etNombre.setText(
                "${socio.nombre} ${socio.apellido}".trim()
            )

            etDni.setText(socio.dni)

            val totalActividades = detallesCobro.sumOf {
                it.precioUnitario * it.cantidad
            }

            if (detallesCobro.isEmpty()) {

                etMonto.setText("")

                Toast.makeText(
                    this,
                    "El cliente no tiene actividades asignadas",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                etMonto.setText(
                    String.format(
                        Locale.US,
                        "%.2f",
                        totalActividades
                    )
                )
            }

        } else {

            etNombre.setText(nombreUsuario ?: "")
            etDni.setText(dniUsuario ?: "")
            etMonto.setText("")

            Toast.makeText(
                this,
                "No se encontró el cliente en SQLite",
                Toast.LENGTH_LONG
            ).show()
        }

        // =====================================================
        // PROTEGER NOMBRE Y DNI
        // =====================================================

        etNombre.isFocusable = false
        etNombre.isClickable = false
        etNombre.isCursorVisible = false

        etDni.isFocusable = false
        etDni.isClickable = false
        etDni.isCursorVisible = false

        // =====================================================
        // SELECCIONAR TARJETA
        // =====================================================

        btnTarjeta.setOnClickListener {

            val socioActual = socioSeleccionado

            if (socioActual == null || detallesCobro.isEmpty()) {

                Toast.makeText(
                    this,
                    "No hay actividades para cobrar",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val monto = etMonto.text.toString()
                .trim()
                .replace(",", ".")

            val montoNumero = monto.toDoubleOrNull()

            if (montoNumero == null || montoNumero <= 0) {

                etMonto.error = "El monto no es válido"
                return@setOnClickListener
            }

            medioPago = "Tarjeta"

            val datos = Intent(
                this,
                CantidadCuotasActivity::class.java
            ).apply {

                putExtra("USUARIO_NOMBRE", etNombre.text.toString())
                putExtra("USUARIO_DNI", socioActual.dni)
                putExtra("NUMERO", socioActual.numeroAfiliado ?: "S/N")

                putExtra(
                    "CLIENTE",
                    "${socioActual.nombre} ${socioActual.apellido}".trim()
                )

                putExtra(
                    "TIPO",
                    if (socioActual.esSocio) "Cuota" else "Actividades"
                )

                putExtra(
                    "CONCEPTO",
                    if (socioActual.esSocio) {
                        "Cuota mensual"
                    } else {
                        "Pago de actividades"
                    }
                )

                putExtra(
                    "ACTIVIDADES",
                    detallesCobro.joinToString(", ") {
                        it.descripcion
                    }
                )

                putExtra("MONTO", monto)
                putExtra("FORMA_PAGO", "Tarjeta")
            }

            startActivity(datos)
        }

        // =====================================================
        // SELECCIONAR EFECTIVO
        // =====================================================

        btnEfectivo.setOnClickListener {

            medioPago = "Efectivo"

            Toast.makeText(
                this,
                "Medio de pago seleccionado: Efectivo",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================================
        // CONFIRMAR COBRO
        // =====================================================

        btnCobrar.setOnClickListener {
            realizarCobro()
        }

        // =====================================================
        // VER COMPROBANTE
        // =====================================================

        btnComprobante.setOnClickListener {
            abrirComprobante()
        }

        // =====================================================
        // VOLVER A HOME
        // =====================================================

        btnVolver.setOnClickListener {
            volverAHome()
        }
    }

    // =====================================================
    // REALIZAR COBRO Y GUARDAR FACTURA
    // =====================================================

    private fun realizarCobro() {

        val socio = socioSeleccionado

        if (socio == null) {

            Toast.makeText(
                this,
                "No se encontró el cliente",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (medioPago.isEmpty()) {

            Toast.makeText(
                this,
                "Seleccione un medio de pago",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (medioPago == "Tarjeta") {

            Toast.makeText(
                this,
                "Complete el proceso de tarjeta y selección de cuotas",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (detallesCobro.isEmpty()) {

            Toast.makeText(
                this,
                "El cliente no tiene actividades para facturar",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val monto = etMonto.text.toString()
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()

        if (monto == null || monto <= 0) {

            etMonto.error = "Ingrese un monto válido"
            return
        }

        // Evitar registrar dos veces el mismo cobro
        if (facturaIdRegistrada != null) {

            Toast.makeText(
                this,
                "Este cobro ya fue registrado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // GENERAR NÚMERO Y FECHA
        // =====================================================

        val numeroFactura =
            "FAC-${System.currentTimeMillis()}"

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        // =====================================================
        // GUARDAR FACTURA Y PAGO EN SQLITE
        // =====================================================

        val dbHelper = SocioDBHelper(this)

        val resultado = try {

            dbHelper.registrarFacturaConPago(
                numeroFactura = numeroFactura,
                dniSocio = socio.dni,
                fecha = fecha,
                formaPago = medioPago,
                cantidadCuotas = 1,
                detalles = detallesCobro
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al guardar el cobro: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            -1L

        } finally {
            dbHelper.close()
        }

        if (resultado != -1L) {

            facturaIdRegistrada = resultado
            numeroFacturaRegistrada = numeroFactura

            findViewById<Button>(R.id.btnCobrar).isEnabled = false

            Toast.makeText(
                this,
                "Cobro registrado correctamente en SQLite",
                Toast.LENGTH_LONG
            ).show()

        } else {

            Toast.makeText(
                this,
                "No se pudo registrar la factura y el pago",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =====================================================
    // ABRIR COMPROBANTE
    // =====================================================

    private fun abrirComprobante() {

        val socio = socioSeleccionado

        if (socio == null) {

            Toast.makeText(
                this,
                "No se encontró el cliente",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (facturaIdRegistrada == null) {

            Toast.makeText(
                this,
                "Primero debe confirmar el cobro",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intentComprobante = Intent(
            this,
            ComprobantePagoActivity::class.java
        ).apply {

            putExtra("FACTURA_ID", facturaIdRegistrada!!)
            putExtra("NUMERO_FACTURA", numeroFacturaRegistrada)

            putExtra("USUARIO_NOMBRE", etNombre.text.toString())
            putExtra("USUARIO_DNI", socio.dni)

            putExtra("NUMERO", socio.numeroAfiliado ?: "S/N")

            putExtra(
                "CLIENTE",
                "${socio.nombre} ${socio.apellido}".trim()
            )

            putExtra(
                "TIPO",
                if (socio.esSocio) "Cuota" else "Actividades"
            )

            putExtra(
                "CONCEPTO",
                if (socio.esSocio) {
                    "Cuota mensual"
                } else {
                    "Pago de actividades"
                }
            )

            putExtra(
                "ACTIVIDADES",
                detallesCobro.joinToString(", ") {
                    it.descripcion
                }
            )

            putExtra(
                "MONTO",
                etMonto.text.toString()
            )

            putExtra("FORMA_PAGO", medioPago)
            putExtra("CUOTAS_SELECCIONADAS", 1)
        }

        startActivity(intentComprobante)
    }

    // =====================================================
    // VOLVER A HOME
    // =====================================================

    private fun volverAHome() {

        val intentHome = Intent(
            this,
            HomeActivity::class.java
        )

        intentHome.putExtra(
            "USUARIO_NOMBRE",
            etNombre.text.toString()
        )

        intentHome.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intentHome)
        finish()
    }
}
