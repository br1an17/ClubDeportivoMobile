package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CobroCuotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etMonto: EditText

    private var medioPago = ""

    // Socio seleccionado desde DatosClub
    private var socioSeleccionado: Socio? = null

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
        // DATOS RECIBIDOS DESDE LA PANTALLA ANTERIOR
        // =====================================================

        val nombreUsuario =
            intent.getStringExtra("USUARIO_NOMBRE")

        val dniUsuario =
            intent.getStringExtra("USUARIO_DNI")

        // =====================================================
        // BUSCAR EL SOCIO EN DATOSCLUB
        // =====================================================

        socioSeleccionado = DatosClub.socios.find { socio ->

            if (!dniUsuario.isNullOrEmpty()) {
                socio.dni == dniUsuario
            } else if (!nombreUsuario.isNullOrEmpty()) {
                socio.nombre.equals(
                    nombreUsuario,
                    ignoreCase = true
                )
            } else {
                false
            }
        }

        // =====================================================
        // MOSTRAR DATOS DEL SOCIO
        // =====================================================

        if (socioSeleccionado != null) {

            etNombre.setText(
                socioSeleccionado!!.nombre
            )

            etDni.setText(
                socioSeleccionado!!.dni
            )

            etMonto.setText(
                socioSeleccionado!!.cuota
            )

        } else {

            etNombre.setText(
                nombreUsuario ?: ""
            )

            etDni.setText(
                dniUsuario ?: ""
            )

            etMonto.setText("")
        }

        // =====================================================
        // LOS DATOS DEL SOCIO NO SE PUEDEN MODIFICAR
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

            medioPago = "Tarjeta"

            val posicionSocio =
                DatosClub.socios.indexOfFirst { socio ->
                    socio.dni == etDni.text.toString().trim()
                }

            val posicionComprobante =
                if (
                    posicionSocio in DatosClub.comprobantes.indices
                ) {
                    posicionSocio
                } else {
                    0
                }

            val intent = Intent(
                this,
                CantidadCuotasActivity::class.java
            )

            intent.putExtra(
                "POSICION_COMPROBANTE",
                posicionComprobante
            )

            intent.putExtra(
                "USUARIO_NOMBRE",
                etNombre.text.toString()
            )

            intent.putExtra(
                "USUARIO_DNI",
                etDni.text.toString()
            )

            intent.putExtra(
                "FORMA_PAGO",
                "Tarjeta"
            )

            startActivity(intent)
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
        // COMPROBANTE
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

        val nombre =
            etNombre.text.toString().trim()

        val dni =
            etDni.text.toString().trim()

        val monto =
            etMonto.text.toString().trim()

        // =====================================================
        // VALIDAR DATOS
        // =====================================================

        if (
            nombre.isEmpty() ||
            dni.isEmpty() ||
            monto.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Complete los datos antes de ver el comprobante",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // BUSCAR SOCIO
        // =====================================================

        val posicionSocio =
            DatosClub.socios.indexOfFirst { socio ->

                socio.dni == dni
            }

        // =====================================================
        // BUSCAR COMPROBANTE
        // =====================================================

        val posicionComprobante =
            if (
                posicionSocio in DatosClub.comprobantes.indices
            ) {
                posicionSocio
            } else {
                0
            }

        // =====================================================
        // ABRIR COMPROBANTE
        // =====================================================

        val intent = Intent(
            this,
            ComprobantePagoActivity::class.java
        )

        intent.putExtra(
            "POSICION_COMPROBANTE",
            posicionComprobante
        )

        intent.putExtra(
            "FORMA_PAGO",
            if (medioPago.isEmpty()) {
                "No seleccionado"
            } else {
                medioPago
            }
        )

        intent.putExtra(
            "USUARIO_NOMBRE",
            nombre
        )

        intent.putExtra(
            "USUARIO_DNI",
            dni
        )

        intent.putExtra(
            "MONTO",
            monto
        )

        startActivity(intent)
    }

    // =====================================================
    // VOLVER A HOME
    // =====================================================

    private fun volverAHome() {

        val intent = Intent(
            this,
            HomeActivity::class.java
        )

        intent.putExtra(
            "USUARIO_NOMBRE",
            etNombre.text.toString()
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)

        finish()
    }

    // =====================================================
    // REALIZAR COBRO
    // =====================================================

    private fun realizarCobro() {

        val nombre =
            etNombre.text.toString().trim()

        val dni =
            etDni.text.toString().trim()

        val monto =
            etMonto.text.toString().trim()

        // =====================================================
        // VALIDAR NOMBRE
        // =====================================================

        if (nombre.isEmpty()) {

            Toast.makeText(
                this,
                "No se seleccionó ningún usuario",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // VALIDAR DNI
        // =====================================================

        if (dni.isEmpty()) {

            Toast.makeText(
                this,
                "El usuario seleccionado no tiene DNI",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // VALIDAR MONTO
        // =====================================================

        if (monto.isEmpty()) {

            etMonto.error =
                "Ingrese el monto"

            etMonto.requestFocus()

            return
        }

        val montoNumero =
            monto.toDoubleOrNull()

        if (
            montoNumero == null ||
            montoNumero <= 0
        ) {

            etMonto.error =
                "Ingrese un monto válido"

            etMonto.requestFocus()

            return
        }

        // =====================================================
        // VALIDAR MEDIO DE PAGO
        // =====================================================

        if (medioPago.isEmpty()) {

            Toast.makeText(
                this,
                "Seleccione Tarjeta o Efectivo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // COBRO CORRECTO
        // =====================================================

        Toast.makeText(
            this,
            "Cobro realizado correctamente",
            Toast.LENGTH_LONG
        ).show()
    }
}