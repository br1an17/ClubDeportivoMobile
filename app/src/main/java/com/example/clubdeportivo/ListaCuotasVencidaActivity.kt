package com.example.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ListaCuotasVencidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_deudores) // Nombre del layout de la Vista 2

        val contenedor = findViewById<LinearLayout>(R.id.contenedorSocios)
        val btnMenu = findViewById<Button>(R.id.btnMenu)

        // Cargar los botones de los socios
        cargarSocios(contenedor)

        btnMenu.setOnClickListener {
            finish() // Vuelve al menú principal
        }
    }

    private fun cargarSocios(contenedor: LinearLayout) {
        contenedor.removeAllViews()

        // Podés filtrar de DatosClub o mostrar la lista de socios
        val sociosLista = DatosClub.socios

        for (socio in sociosLista) {
            val botonSocio = MaterialButton(this).apply {
                text = socio.nombre.lowercase()
                setTextColor(Color.BLACK)
                textSize = 16f
                setTypeface(null, Typeface.BOLD)


                setBackgroundColor(Color.parseColor("#15D400"))
                cornerRadius = 30

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120
                ).apply {
                    setMargins(0, 0, 0, 20)
                }


                setOnClickListener {
                    val intent = Intent(context, DetalleCuotaVencidaActivity::class.java)
                    intent.putExtra("USUARIO_NOMBRE", socio.nombre)
                    intent.putExtra("USUARIO_DNI", socio.dni)
                    intent.putExtra("USUARIO_ID", socio.numeroAfiliado)
                    intent.putExtra("FECHA_VENCIMIENTO", socio.vencimiento ?: "10/05/2026")
                    startActivity(intent)
                }
            }
            contenedor.addView(botonSocio)
        }
    }
}