package com.example.clubdeportivo
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val campoUsuario = findViewById<EditText>(R.id.etUsuario)
        val campoPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresarLogin)
        val btnVolver = findViewById<Button>(R.id.btnVolver)


        btnIngresar.setOnClickListener {
            val usuarioIngresado = campoUsuario.text.toString().trim()
            val contrasenaIngresada = campoPassword.text.toString().trim()

            if (usuarioIngresado.isEmpty() || contrasenaIngresada.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val adminEncontrado = DatosClub.administradores.find { admin ->
                admin.nombre.equals(usuarioIngresado, ignoreCase = true) &&
                        admin.contraseña == contrasenaIngresada
            }

            if (adminEncontrado != null) {
                Toast.makeText(this, "Bienvenido/a ${adminEncontrado.nombre}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USUARIO_NOMBRE", adminEncontrado.nombre)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}