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

        val usuario = "Usuario"
        val password = "Clave123"
        val campoUsuario = findViewById<EditText>(R.id.etUsuario)
        val campoPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresarLogin)
        val btnSalir = findViewById<Button>(R.id.btnSalir)


        btnIngresar.setOnClickListener {
            val usuarioIngresado = campoUsuario.text.toString().trim()
            val contrasenaIngresada = campoPassword.text.toString().trim()

            if (usuarioIngresado.isEmpty() || contrasenaIngresada.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuarioIngresado == usuario && contrasenaIngresada == password) {
                Toast.makeText(this, "Bienvenido $usuarioIngresado", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USUARIO_NOMBRE", usuarioIngresado)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        btnSalir.setOnClickListener {
            finish()
        }
    }
}