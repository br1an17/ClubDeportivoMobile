package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class ComprobantePagoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_comprobante_pago)

        // =====================================================
        // REFERENCIAS A LOS ELEMENTOS DEL XML
        // =====================================================

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

        // =====================================================
        // OBTENER LA POSICIÓN DEL COMPROBANTE
        // =====================================================

        val posicion = intent.getIntExtra(
            "POSICION_COMPROBANTE",
            0
        )

        // Verificamos que la posición exista
        val indiceSeguro = if (
            posicion in DatosClub.comprobantes.indices
        ) {
            posicion
        } else {
            0
        }

        // =====================================================
        // OBTENER COMPROBANTE DESDE DATOSCLUB
        // =====================================================

        val comprobante =
            DatosClub.comprobantes[indiceSeguro]

        val montoParaMostrar =
            intent.getStringExtra("MONTO")
                ?: comprobante.monto

        // =====================================================
        // MOSTRAR DATOS
        // =====================================================

        tvNumero.text = comprobante.numero
        tvCliente.text = comprobante.cliente
        tvTipo.text = comprobante.tipo
        tvConcepto.text = comprobante.concepto
        tvActividades.text = comprobante.actividades
        tvMonto.text = montoParaMostrar
        tvFormaPago.text = comprobante.formaPago
        tvCuotas.text = comprobante.cuotas
        tvValorCuota.text = comprobante.valorCuota

        // =====================================================
        // FORMA DE PAGO RECIBIDA
        // =====================================================

        val formaPagoRecibida =
            intent.getStringExtra("FORMA_PAGO")

        if (formaPagoRecibida != null) {
            tvFormaPago.text = formaPagoRecibida
        }

        // =====================================================
        // CUOTAS RECIBIDAS
        // =====================================================

        val cuotasSeleccionadas =
            intent.getIntExtra(
                "CUOTAS_SELECCIONADAS",
                0
            )

        if (cuotasSeleccionadas > 0) {

            tvCuotas.text =
                cuotasSeleccionadas.toString()

            val montoNumero =
                montoParaMostrar.toDoubleOrNull()

            if (montoNumero != null) {

                val valorCuota =
                    montoNumero / cuotasSeleccionadas

                tvValorCuota.text =
                    String.format("%.2f", valorCuota)
            }
        }

        // =====================================================
        // BOTÓN IMPRIMIR
        // =====================================================

        btnImprimir.setOnClickListener {

            Toast.makeText(
                this,
                "Comprobante listo para imprimir",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================================
        // BOTÓN VOLVER
        // =====================================================

        btnVolver.setOnClickListener {

            val regreso = Intent(
                this,
                CobroCuotaActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            startActivity(regreso)
        }
    }
}