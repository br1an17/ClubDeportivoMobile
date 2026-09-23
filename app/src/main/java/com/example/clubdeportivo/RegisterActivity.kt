package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etEmail = findViewById<EditText>(R.id.etEmail)

        val cbSocio = findViewById<CheckBox>(R.id.cbSocio)
        val cbNoSocio = findViewById<CheckBox>(R.id.cbNoSocio)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnMenu = findViewById<Button>(R.id.btnMenu)

        // Control para que no marque Socio y No Socio al mismo tiempo
        cbSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbNoSocio.isChecked = false
        }
        cbNoSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbSocio.isChecked = false
        }

        // Botón Limpiar: resetea todos los campos
        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            etDni.text.clear()
            etTelefono.text.clear()
            etEmail.text.clear()
            cbSocio.isChecked = false
            cbNoSocio.isChecked = false
            cbAptoFisico.isChecked = false
        }

        // Botón Registrar: valida datos y muestra confirmación
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Complete los campos obligatorios (Nombre, Apellido, DNI)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Usuario $nombre $apellido registrado con éxito", Toast.LENGTH_LONG).show()
                finish() // Regresa tras registrar
            }
        }

        // Botón Menú: vuelve a la pantalla anterior
        btnMenu.setOnClickListener {
            finish()
        }
    }
}