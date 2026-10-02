package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SerchUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serch_user)

        val etDni = findViewById<EditText>(R.id.etDni)
        val etSocio = findViewById<EditText>(R.id.etNSocio)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnBuscar.setOnClickListener {
            val dniIngresado = etDni.text.toString().trim()
            val socioIngresado = etSocio.text.toString().trim()

            val criterioBusqueda = if (dniIngresado.isNotEmpty()) dniIngresado else socioIngresado

            if (criterioBusqueda.isEmpty()) {
                Toast.makeText(this, "Ingrese DNI o N° de Socio para buscar", Toast.LENGTH_SHORT).show()
            } else {
                val usuarioEncontrado = RepositorioUsuarios.buscarPorDniOSocio(criterioBusqueda)

                if (usuarioEncontrado != null) {
                    Toast.makeText(this, "Usuario encontrado: ${usuarioEncontrado.nombre}", Toast.LENGTH_SHORT).show()

                    // Pasa los datos a ActionsUserActivity
                    val intent = Intent(this, ActionsUserActivity::class.java)
                    intent.putExtra("USUARIO_NOMBRE", "${usuarioEncontrado.nombre} ${usuarioEncontrado.apellido}")
                    intent.putExtra("USUARIO_DNI", usuarioEncontrado.dni)
                    intent.putExtra("USUARIO_ID", usuarioEncontrado.idSocio)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se encontró ningún usuario registrado", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}