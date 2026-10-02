package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ListaCuotaVencerHoyActivity : AppCompatActivity() {

    // =====================================================
    // MODELO PARA MOSTRAR LOS CLIENTES
    // =====================================================

    data class ClienteVencimiento(
        val nombre: String,
        val dni: String,
        val numeroCliente: String
    )

    // =====================================================
    // ELEMENTOS DEL XML
    // =====================================================

    private lateinit var etBuscar: EditText
    private lateinit var spLimite: Spinner
    private lateinit var contenedorClientes: LinearLayout
    private lateinit var tvPagina: TextView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var btnMenu: Button

    // =====================================================
    // DATOS CENTRALES DEL PROYECTO
    // =====================================================

    private val clientes = DatosClub.socios.map { socio ->

        ClienteVencimiento(
            nombre = socio.nombre,
            dni = socio.dni,
            numeroCliente = socio.numeroAfiliado
        )
    }

    private var clientesFiltrados = clientes
    private var paginaActual = 1
    private var cantidadPorPagina = 5

    // =====================================================
    // ON CREATE
    // =====================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_lista_cuota_vencer_hoy
        )

        // =================================================
        // REFERENCIAS AL XML
        // =================================================

        etBuscar = findViewById(R.id.etBuscar)

        spLimite = findViewById(R.id.spLimite)

        contenedorClientes =
            findViewById(R.id.contenedorClientes)

        tvPagina = findViewById(R.id.tvPagina)

        btnAnterior =
            findViewById(R.id.btnAnterior)

        btnSiguiente =
            findViewById(R.id.btnSiguiente)

        btnMenu =
            findViewById(R.id.btnMenu)

        // =================================================
        // CONFIGURACIONES
        // =================================================

        configurarSpinner()

        configurarBuscador()

        configurarPaginacion()

        actualizarLista()

        // =================================================
        // VOLVER A HOME
        // =================================================

        btnMenu.setOnClickListener {

            volverAHome()
        }
    }

    // =====================================================
    // VOLVER A HOME
    // =====================================================

    private fun volverAHome() {

        val intent = Intent(
            this,
            HomeActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)

        finish()
    }

    // =====================================================
    // SPINNER
    // =====================================================

    private fun configurarSpinner() {

        val opciones = arrayOf(
            "3",
            "5",
            "10"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spLimite.adapter = adapter

        // Comenzar mostrando 5 clientes
        spLimite.setSelection(1)

        spLimite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cantidadPorPagina =
                        opciones[position].toInt()

                    paginaActual = 1

                    actualizarLista()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    // =====================================================
    // BUSCADOR
    // =====================================================

    private fun configurarBuscador() {

        etBuscar.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val texto = s
                        ?.toString()
                        ?.trim()
                        ?.lowercase()
                        ?: ""

                    clientesFiltrados =
                        if (texto.isEmpty()) {

                            clientes

                        } else {

                            clientes.filter { cliente ->

                                cliente.nombre
                                    .lowercase()
                                    .contains(texto) ||

                                        cliente.dni
                                            .contains(texto) ||

                                        cliente.numeroCliente
                                            .contains(texto)
                            }
                        }

                    paginaActual = 1

                    actualizarLista()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    // =====================================================
    // PAGINACIÓN
    // =====================================================

    private fun configurarPaginacion() {

        btnAnterior.setOnClickListener {

            if (paginaActual > 1) {

                paginaActual--

                actualizarLista()
            }
        }

        btnSiguiente.setOnClickListener {

            val totalPaginas =
                obtenerTotalPaginas()

            if (paginaActual < totalPaginas) {

                paginaActual++

                actualizarLista()
            }
        }
    }

    // =====================================================
    // TOTAL DE PÁGINAS
    // =====================================================

    private fun obtenerTotalPaginas(): Int {

        if (clientesFiltrados.isEmpty()) {
            return 1
        }

        return (
                clientesFiltrados.size +
                        cantidadPorPagina -
                        1
                ) / cantidadPorPagina
    }

    // =====================================================
    // ACTUALIZAR LISTA
    // =====================================================

    private fun actualizarLista() {

        contenedorClientes.removeAllViews()

        // =================================================
        // SIN RESULTADOS
        // =================================================

        if (clientesFiltrados.isEmpty()) {

            val mensaje = TextView(this)

            mensaje.text =
                "No se encontraron clientes"

            mensaje.textSize = 16f

            mensaje.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            mensaje.gravity =
                Gravity.CENTER

            mensaje.setPadding(
                0,
                30,
                0,
                30
            )

            contenedorClientes.addView(
                mensaje
            )

            tvPagina.text =
                "Página 1 de 1"

            btnAnterior.isEnabled = false

            btnSiguiente.isEnabled = false

            return
        }

        // =================================================
        // CALCULAR PÁGINA
        // =================================================

        val totalPaginas =
            obtenerTotalPaginas()

        if (paginaActual > totalPaginas) {

            paginaActual =
                totalPaginas
        }

        val inicio =
            (paginaActual - 1) *
                    cantidadPorPagina

        val fin = minOf(
            inicio + cantidadPorPagina,
            clientesFiltrados.size
        )

        val clientesPagina =
            clientesFiltrados.subList(
                inicio,
                fin
            )

        // =================================================
        // MOSTRAR CLIENTES
        // =================================================

        for (cliente in clientesPagina) {

            val item = TextView(this)

            item.text = buildString {

                append(cliente.nombre)

                append("\n")

                append("DNI: ")

                append(cliente.dni)

                append("  |  Cliente: ")

                append(cliente.numeroCliente)
            }

            item.textSize = 15f

            item.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )

            item.setPadding(
                18,
                12,
                18,
                12
            )

            item.background =
                ContextCompat.getDrawable(
                    this,
                    R.drawable.bg_input
                )

            // =================================================
            // AL TOCAR EL CLIENTE
            // =================================================

            item.setOnClickListener {

                abrirSocioCuotaVenceHoy(
                    cliente
                )
            }

            val params =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                0,
                0,
                0,
                12
            )

            contenedorClientes.addView(
                item,
                params
            )
        }

        // =================================================
        // ACTUALIZAR PÁGINA
        // =================================================

        tvPagina.text =
            "Página $paginaActual de $totalPaginas"

        btnAnterior.isEnabled =
            paginaActual > 1

        btnSiguiente.isEnabled =
            paginaActual < totalPaginas
    }

    // =====================================================
    // ABRIR SOCIO CUOTA VENCE HOY
    // =====================================================

    private fun abrirSocioCuotaVenceHoy(
        cliente: ClienteVencimiento
    ) {

        val intent = Intent(
            this,
            SocioCuotaVenceHoyActivity::class.java
        )

        // Enviamos el número de afiliado.
        // SocioCuotaVenceHoy lo buscará en DatosClub.
        intent.putExtra(
            "SOCIO_AFILIADO",
            cliente.numeroCliente
        )

        // También enviamos el nombre como respaldo.
        intent.putExtra(
            "SOCIO_NOMBRE",
            cliente.nombre
        )

        startActivity(intent)
    }
}