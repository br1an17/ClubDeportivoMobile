package com.example.clubdeportivo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    // Variables globales para el manejo de la foto del apto físico
    private var fotoAptoFisico: android.graphics.Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1

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
        val btnCapturarApto = findViewById<Button>(R.id.btncamara)
        val tvEstadoApto = findViewById<TextView>(R.id.tvEstadoApto)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        cbSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbNoSocio.isChecked = false
        }
        cbNoSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbSocio.isChecked = false
        }


        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            etDni.text.clear()
            etTelefono.text.clear()
            etEmail.text.clear()
            cbSocio.isChecked = false
            cbNoSocio.isChecked = false
            fotoAptoFisico = null
            tvEstadoApto.text = "X Sin apto físico"
            tvEstadoApto.setTextColor(android.graphics.Color.parseColor("#FF5252"))
        }
        btnCapturarApto.setOnClickListener {
            // Creamos el Intent pidiéndole al sistema que abra la cámara
            val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Lanzamos la cámara esperando un resultado de vuelta (el ticket 1)
            startActivityForResult(intentCamara, REQUEST_IMAGE_CAPTURE)
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if(nombre.isEmpty()){
               etNombre.error = "El nombre es obligatorio"
               etNombre.requestFocus()
               return@setOnClickListener
            }
            if (nombre.length < 3) {
                etNombre.error = "El nombre debe tener al menos 3 caracteres"
                etNombre.requestFocus()
                return@setOnClickListener
            }


            if(apellido.isEmpty()){
                etApellido.error = "El apellido es obligatorio"
                etApellido.requestFocus()
                return@setOnClickListener
            }
            if (apellido.length < 3) {
                etApellido.error = "El apellido debe tener al menos 3 caracteres"
                etApellido.requestFocus()
                return@setOnClickListener
            }

            if(dni.isEmpty()){
                etDni.error = "El DNI es obligatorio"
                etDni.requestFocus()
                return@setOnClickListener
            }
            if (!dni.all { it.isDigit() } || dni.length < 7 || dni.length >9 ){
                etDni.error ="ingrese un DNI valido"
                etDni.requestFocus()
                return@setOnClickListener
            }

            if (telefono.isNotEmpty() && !telefono.all { it.isDigit() }) {
                etTelefono.error = "Ingrese un teléfono válido"
                etTelefono.requestFocus()
                return@setOnClickListener
            }
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Ingrese un correo electrónico válido"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if(!cbSocio.isChecked && !cbNoSocio.isChecked){
                Toast.makeText(this,"Seleccione si es socio o no socio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fotoAptoFisico == null) {
                Toast.makeText(this, "Debe tomar la foto del apto físico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val aptoFisico = fotoAptoFisico != null
            registrarCliente(nombre, apellido, dni, telefono, email, cbSocio.isChecked, aptoFisico)
        }


        btnVolver.setOnClickListener {
            finish()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val tvEstadoApto = findViewById<TextView>(R.id.tvEstadoApto)

        // Verificamos que la respuesta sea la de nuestra cámara (ticket 1) y que haya salido bien (RESULT_OK)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Obtenemos la foto en formato Bitmap desde los datos que nos devolvió el sistema
            val extras = data?.extras
            fotoAptoFisico = extras?.get("data") as? android.graphics.Bitmap

            // Si la foto llegó correctamente, actualizamos el texto indicador a verde
            if (fotoAptoFisico != null) {
                tvEstadoApto.text = "✔️ Apto físico cargado"
                tvEstadoApto.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Verde éxito
            }
        }
    }
    private fun registrarCliente(
        nombre: String,
        apellido: String,
        dni: String,
        telefono: String,
        email: String,
        esSocio: Boolean,
        aptoFisico: Boolean){
        val existe = DatosClub.clientes.any{it.dni == dni }
        if (existe){
            Toast.makeText(this,"Ya existe un Cliente con el DNI $dni" , Toast.LENGTH_LONG).show()
            return
        }
    val prefijo = if (esSocio)"SOC-" else "NS-"
    val numeroClienteGenerado = prefijo +(DatosClub.clientes.size +1200)
        val montoCuota = if (esSocio)"150000" else "0"

        val  nuevoCliente = ClienteClub(
            dni = dni,
            nombre = nombre,
            apellido = apellido,
            telefono = telefono,
            email = email,
            esSocio = esSocio,
            aptoFisico = aptoFisico,
            numeroAfiliado = numeroClienteGenerado,
            cuotaAlDia = true,
            cuotaMonto = montoCuota,
            vencimiento = "10/11/2026",
            actividades = mutableListOf()
        )
        DatosClub.clientes.add(nuevoCliente)

        val tipoClienteStr = if (esSocio) "Socio" else "No Socio"
        Toast.makeText(this, "¡$tipoClienteStr $nombre $apellido registrado con éxito!", Toast.LENGTH_LONG).show()
        finish()

    }
}