
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class CobroCuotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etMonto: EditText

    private var medioPago = ""

    private var socioSeleccionado: ClienteClub? = null

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

        val nombreUsuario = intent.getStringExtra("USUARIO_NOMBRE")
        val dniUsuario = intent.getStringExtra("USUARIO_DNI")

        // =====================================================
        // BUSCAR CLIENTE POR DNI O NOMBRE
        // =====================================================

        socioSeleccionado = DatosClub.clientes.firstOrNull { socio ->

            if (!dniUsuario.isNullOrBlank()) {
                socio.dni == dniUsuario
            } else if (!nombreUsuario.isNullOrBlank()) {
                socio.nombre.equals(
                    nombreUsuario,
                    ignoreCase = true
                ) || "${socio.nombre} ${socio.apellido}".trim()
                    .equals(nombreUsuario, ignoreCase = true)
            } else {
                false
            }
        }

        // =====================================================
        // MOSTRAR DATOS DEL CLIENTE
        // =====================================================

        val socio = socioSeleccionado

        if (socio != null) {

            val nombreCompleto =
                "${socio.nombre} ${socio.apellido}".trim()

            etNombre.setText(nombreCompleto)
            etDni.setText(socio.dni)

            val totalActividades =
                DatosClub.calcularTotalActividades(socio)

            if (totalActividades == null) {

                etMonto.setText("")

                Toast.makeText(
                    this,
                    "Falta configurar el precio de alguna actividad",
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

            // No inventar un importe si no se encontró al cliente.
            etMonto.setText("")
        }

        // Los datos del cliente no se pueden modificar
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

            val nombre = etNombre.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val monto = etMonto.text.toString().trim()

            if (nombre.isBlank() || dni.isBlank()) {

                Toast.makeText(
                    this,
                    "No se encontraron los datos del cliente",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val montoNumero =
                monto.replace(",", ".").toDoubleOrNull()

            if (montoNumero == null || montoNumero <= 0) {

                etMonto.error = "El monto no es válido"
                etMonto.requestFocus()

                return@setOnClickListener
            }

            medioPago = "Tarjeta"

            val cliente = socioSeleccionado

            val datos = Intent(
                this,
                CantidadCuotasActivity::class.java
            ).apply {

                putExtra("USUARIO_NOMBRE", nombre)
                putExtra("USUARIO_DNI", dni)

                putExtra(
                    "NUMERO",
                    cliente?.numeroAfiliado ?: "S/N"
                )

                putExtra(
                    "CLIENTE",
                    if (cliente != null) {
                        "${cliente.nombre} ${cliente.apellido}".trim()
                    } else {
                        nombre
                    }
                )

                putExtra(
                    "TIPO",
                    if (cliente?.esSocio == true) {
                        "Cuota"
                    } else {
                        "Actividades"
                    }
                )

                putExtra(
                    "CONCEPTO",
                    if (cliente?.esSocio == true) {
                        "Cuota mensual"
                    } else {
                        "Pago de actividades"
                    }
                )

                putExtra(
                    "ACTIVIDADES",
                    cliente?.actividades?.joinToString(", ") ?: ""
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
        // COBRAR
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
    // ABRIR COMPROBANTE
    // =====================================================

    private fun abrirComprobante() {

        val nombre = etNombre.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val monto = etMonto.text.toString().trim()

        if (
            nombre.isBlank() ||
            dni.isBlank() ||
            monto.isBlank()
        ) {

            Toast.makeText(
                this,
                "Complete los datos antes de ver el comprobante",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val montoNumero =
            monto.replace(",", ".").toDoubleOrNull()

        if (montoNumero == null || montoNumero <= 0) {

            etMonto.error = "El monto no es válido"
            return
        }

        val cliente = socioSeleccionado

        val intentComprobante = Intent(
            this,
            ComprobantePagoActivity::class.java
        ).apply {

            putExtra("USUARIO_NOMBRE", nombre)
            putExtra("USUARIO_DNI", dni)

            putExtra(
                "NUMERO",
                cliente?.numeroAfiliado ?: "S/N"
            )

            putExtra(
                "CLIENTE",
                if (cliente != null) {
                    "${cliente.nombre} ${cliente.apellido}".trim()
                } else {
                    nombre
                }
            )

            putExtra(
                "TIPO",
                if (cliente?.esSocio == true) {
                    "Cuota"
                } else {
                    "Actividades"
                }
            )

            putExtra(
                "CONCEPTO",
                if (cliente?.esSocio == true) {
                    "Cuota mensual"
                } else {
                    "Pago de actividades"
                }
            )

            putExtra(
                "ACTIVIDADES",
                cliente?.actividades?.joinToString(", ") ?: ""
            )

            putExtra("MONTO", monto)

            putExtra(
                "FORMA_PAGO",
                medioPago.ifBlank { "No seleccionado" }
            )

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

    // =====================================================
    // REALIZAR COBRO
    // =====================================================

    private fun realizarCobro() {

        val nombre = etNombre.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val monto = etMonto.text.toString().trim()

        if (nombre.isEmpty()) {

            Toast.makeText(
                this,
                "No se seleccionó ningún usuario",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (dni.isEmpty()) {

            Toast.makeText(
                this,
                "El usuario seleccionado no tiene DNI",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val montoNumero =
            monto.replace(",", ".").toDoubleOrNull()

        if (montoNumero == null || montoNumero <= 0) {

            etMonto.error = "Ingrese un monto válido"
            etMonto.requestFocus()

            return
        }

        if (medioPago.isEmpty()) {

            Toast.makeText(
                this,
                "Seleccione Tarjeta o Efectivo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Toast.makeText(
            this,
            "Cobro realizado correctamente",
            Toast.LENGTH_LONG
        ).show()
    }
}
