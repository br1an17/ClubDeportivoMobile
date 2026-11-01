package com.example.clubdeportivo

import java.util.Locale
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

        val dniUsuario =
            intent.getStringExtra("USUARIO_DNI").orEmpty()

        val socio = DatosClub.clientes.firstOrNull {
            it.dni == dniUsuario
        }

        val posicion = intent.getIntExtra(
            "POSICION_COMPROBANTE",
            -1
        )

        val comprobanteBase =
            DatosClub.comprobantes.getOrNull(posicion)

        val numeroBase = if (socio != null) {
            socio.numeroAfiliado ?: "S/N"
        } else {
            comprobanteBase?.numero ?: "S/N"
        }

        val clienteBase = if (socio != null) {
            "${socio.nombre} ${socio.apellido}"
        } else {
            comprobanteBase?.cliente
                ?: intent.getStringExtra("USUARIO_NOMBRE").orEmpty()
        }

        val tipoBase = if (socio != null) {
            if (socio.esSocio) "Cuota" else "Actividades"
        } else {
            comprobanteBase?.tipo ?: "Cuota"
        }

        val conceptoBase = if (socio != null) {
            if (socio.esSocio) "Cuota mensual" else "Pago de actividades"
        } else {
            comprobanteBase?.concepto ?: "Cuota mensual"
        }

        val actividadesBase = if (socio != null) {
            socio.actividades.joinToString(", ")
        } else {
            comprobanteBase?.actividades.orEmpty()
        }

        val montoCalculado = socio
            ?.let { DatosClub.calcularTotalActividades(it) }
            ?.let { String.format(Locale.US, "%.2f", it) }

        val montoBase = if (socio != null) {
            montoCalculado ?: "0.00"
        } else {
            comprobanteBase?.monto ?: "0.00"
        }

        val numero = intent.getStringExtra("NUMERO")
            ?.takeIf { it.isNotBlank() } ?: numeroBase

        val cliente = intent.getStringExtra("CLIENTE")
            ?.takeIf { it.isNotBlank() } ?: clienteBase

        val tipo = intent.getStringExtra("TIPO")
            ?.takeIf { it.isNotBlank() } ?: tipoBase

        val concepto = intent.getStringExtra("CONCEPTO")
            ?.takeIf { it.isNotBlank() } ?: conceptoBase

        val actividades = intent.getStringExtra("ACTIVIDADES")
            ?.takeIf { it.isNotBlank() }
            ?: actividadesBase.ifBlank { "Sin actividades asignadas" }

        val monto = intent.getStringExtra("MONTO")
            ?.takeIf { it.isNotBlank() } ?: montoBase

        val formaPago = intent.getStringExtra("FORMA_PAGO")
            ?.takeIf { it.isNotBlank() }
            ?: if (socio == null) {
                comprobanteBase?.formaPago ?: "No seleccionado"
            } else {
                "No seleccionado"
            }

        val cuotasSeleccionadas = intent.getIntExtra(
            "CUOTAS_SELECCIONADAS",
            1
        ).coerceAtLeast(1)

        tvNumero.text = numero
        tvCliente.text = cliente
        tvTipo.text = tipo
        tvConcepto.text = concepto
        tvActividades.text = actividades
        tvMonto.text = monto
        tvFormaPago.text = formaPago
        tvCuotas.text = cuotasSeleccionadas.toString()

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