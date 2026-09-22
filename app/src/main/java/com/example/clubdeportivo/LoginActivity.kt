package com.example.clubdeportivo
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btnIngresar = findViewById<Button>(R.id.btnIngresarLogin)
        val btnSalir = findViewById<Button>(R.id.btnSalir)

        btnIngresar.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSalir.setOnClickListener{
            finish()
        }
    }
}