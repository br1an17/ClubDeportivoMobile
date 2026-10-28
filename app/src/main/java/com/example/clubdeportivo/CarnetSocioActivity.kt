package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

class CarnetSocioActivity : AppCompatActivity() {

    // Variables para las vistas
    private lateinit var tvNumAfiliado: TextView
    private lateinit var tvNombreSocio: TextView
    private lateinit var tvDocumentoSocio: TextView
    private lateinit var tvEstadoSocio: TextView
    private lateinit var btnVolver: Button
    private lateinit var ivCodigoQR: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet_socio)

        // 1. Inicializar referencias de la interfaz
        tvNumAfiliado = findViewById(R.id.tvNumAfiliado)
        tvNombreSocio = findViewById(R.id.tvNombreSocio)
        tvDocumentoSocio = findViewById(R.id.tvDocumentoSocio)
        tvEstadoSocio = findViewById(R.id.tvEstadoSocio)
        btnVolver = findViewById(R.id.btnVolver)
        ivCodigoQR = findViewById(R.id.ivCodigoQR)

        // 2. Cargar los datos recibidos del socio
        cargarDatosSocio()

        // 3. Configurar el botón "Volver" para ir al menú principal (HomeActivity)
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosSocio() {
        val numAfiliado = intent.getStringExtra("NUM_AFILIADO") ?: "N/A"
        val nombre = intent.getStringExtra("NOMBRE_SOCIO") ?: "Socio No Registrado"
        val documento = intent.getStringExtra("DOCUMENTO_SOCIO") ?: "N/A"
        val estado = intent.getStringExtra("ESTADO_SOCIO") ?: "Inactivo"


        tvNumAfiliado.text = numAfiliado
        tvNombreSocio.text = nombre
        tvDocumentoSocio.text = documento
        tvEstadoSocio.text = estado


        val textoParaQR = """
        CLUB DEPORTIVO
        Afiliado: $numAfiliado
        Nombre: $nombre
        DNI: $documento
        Estado: $estado
        Vencimiento: 28/11/2026
    """.trimIndent()

        val bitmapQR = generarCodigoQR(textoParaQR)


        if (bitmapQR != null) {
            ivCodigoQR.setImageBitmap(bitmapQR)
        } else {
            ivCodigoQR.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun generarCodigoQR(texto: String): Bitmap? {
        val ancho = 500
        val alto = 500
        try {

            val bitMatrix = MultiFormatWriter().encode(texto, BarcodeFormat.QR_CODE, ancho, alto)


            val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565)


            for (x in 0 until ancho) {
                for (y in 0 until alto) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }
}