package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class ComprobantePagoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_comprobante_pago)

        // Referencias a los elementos del XML
        val tvNumero = findViewById<TextView>(R.id.tvNumero)
        val tvCliente = findViewById<TextView>(R.id.tvCliente)
        val tvTipo = findViewById<TextView>(R.id.tvTipo)
        val tvConcepto = findViewById<TextView>(R.id.tvConcepto)
        val tvActividades = findViewById<TextView>(R.id.tvActividades)
        val tvMonto = findViewById<TextView>(R.id.tvMonto)
        val tvFormaPago = findViewById<TextView>(R.id.tvFormaPago)
        val tvCuotas = findViewById<TextView>(R.id.tvCuotas)
        val tvValorCuota = findViewById<TextView>(R.id.tvValorCuota)

        val btnImprimir = findViewById<Button>(R.id.btnImprimir)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Número real de factura generado al registrar el pago
        val numeroFactura = intent.getStringExtra("NUMERO_FACTURA")
            ?.takeIf { it.isNotBlank() }
            ?: intent.getStringExtra("NUMERO")
                ?.takeIf { it.startsWith("FAC-", ignoreCase = true) }
            ?: "S/N"

        // Datos recibidos desde la pantalla de cobro
        val cliente = intent.getStringExtra("CLIENTE")
            ?.takeIf { it.isNotBlank() }
            ?: intent.getStringExtra("USUARIO_NOMBRE")
                ?.takeIf { it.isNotBlank() }
            ?: "Sin nombre"

        val tipo = intent.getStringExtra("TIPO")
            ?.takeIf { it.isNotBlank() }
            ?: "Cuota"

        val concepto = intent.getStringExtra("CONCEPTO")
            ?.takeIf { it.isNotBlank() }
            ?: "Pago de actividades"

        val actividades = intent.getStringExtra("ACTIVIDADES")
            ?.takeIf { it.isNotBlank() }
            ?: "Sin actividades asignadas"

        val monto = intent.getStringExtra("MONTO")
            ?.takeIf { it.isNotBlank() }
            ?: "0.00"

        val formaPago = intent.getStringExtra("FORMA_PAGO")
            ?.takeIf { it.isNotBlank() }
            ?: "No seleccionado"

        val cuotasSeleccionadas = intent.getIntExtra(
            "CUOTAS_SELECCIONADAS",
            1
        ).coerceAtLeast(1)

        // Mostrar información
        tvNumero.text = numeroFactura
        tvCliente.text = cliente
        tvTipo.text = tipo
        tvConcepto.text = concepto
        tvActividades.text = actividades
        tvMonto.text = monto
        tvFormaPago.text = formaPago
        tvCuotas.text = cuotasSeleccionadas.toString()

        // Permitir que las actividades ocupen varias líneas
        tvActividades.apply {
            isSingleLine = false
            maxLines = 5
            ellipsize = null
            setHorizontallyScrolling(false)

            layoutParams = layoutParams.apply {
                height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }

        // Calcular valor de cada cuota
        val montoNumero = monto.toDoubleOrNull()

        tvValorCuota.text = if (montoNumero != null) {
            String.format(
                Locale.US,
                "%.2f",
                montoNumero / cuotasSeleccionadas
            )
        } else {
            "0.00"
        }

        // Botón imprimir
        btnImprimir.setOnClickListener {
            Toast.makeText(
                this,
                "Comprobante listo para imprimir",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Botón volver
        btnVolver.setOnClickListener {
            finish()
        }
    }
}