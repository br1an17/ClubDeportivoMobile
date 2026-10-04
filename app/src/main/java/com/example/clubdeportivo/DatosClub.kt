package com.example.clubdeportivo

// 1. Modelo para las Actividades del Club
data class Actividad(
    val id: String,
    val nombre: String,
    val costo: Double,
    val iconoResId: Int? = null
)

// 2. Modelo unificado de Socio/Partner
data class Socio(
    val numeroAfiliado: String,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val cuotaAlDia: Boolean = true,
    val cuotaMonto: String = "15000",
    val vencimiento: String = "10/10/2026",
    val actividades: MutableList<String> = mutableListOf()
)

// 3. Modelo de Comprobante
data class DatosComprobante(
    val numero: String,
    val cliente: String,
    val tipo: String,
    val concepto: String,
    val actividades: String,
    val monto: String,
    val formaPago: String,
    val cuotas: String,
    val valorCuota: String
)

// 4. Repositorio Central del Club
object DatosClub {

    // Lista oficial de Actividades del Club
    val listaActividades = listOf(
        Actividad("ACT01", "Natación", 12000.0, R.drawable.ic_natacion),
        Actividad("ACT02", "G. artistica", 10000.0, R.drawable.ic_gim),
        Actividad("ACT03", "Basquet", 9000.0, R.drawable.ic_basketball),
        Actividad("ACT04", "Karate", 8500.0, R.drawable.ic_karate),
        Actividad("ACT05", "Tennis", 18000.0, R.drawable.ic_tennis),
        Actividad("ACT06", "Fútbol", 15000.0, R.drawable.ic_futbol),
        Actividad("ACT09", "Voley", 9500.0, R.drawable.ic_voley)
    )

    // Lista unificada de Socios
    val socios = mutableListOf(
        Socio(
            numeroAfiliado = "001245",
            dni = "40123456",
            nombre = "Juan Pablo",
            apellido = "Pérez",
            cuotaAlDia = true,
            cuotaMonto = "15000",
            vencimiento = "02/10/2026",
            actividades = mutableListOf("Fútbol", "Musculación")
        ),
        Socio(
            numeroAfiliado = "001246",
            dni = "38987654",
            nombre = "Pedro",
            apellido = "López",
            cuotaAlDia = false,
            cuotaMonto = "12000",
            vencimiento = "02/10/2026",
            actividades = mutableListOf("Natación")
        ),
        Socio(
            numeroAfiliado = "001247",
            dni = "42111222",
            nombre = "María",
            apellido = "Gómez",
            cuotaAlDia = true,
            cuotaMonto = "18000",
            vencimiento = "05/10/2026",
            actividades = mutableListOf("Tennis", "Yoga")
        ),
        Socio(
            numeroAfiliado = "001248",
            dni = "35444555",
            nombre = "Carlos",
            apellido = "Rodríguez",
            cuotaAlDia = true,
            cuotaMonto = "14000",
            vencimiento = "15/10/2026",
            actividades = mutableListOf("Paddle")
        ),
        Socio(
            numeroAfiliado = "001249",
            dni = "39888999",
            nombre = "Ana",
            apellido = "Martínez",
            cuotaAlDia = false,
            cuotaMonto = "11000",
            vencimiento = "28/09/2026",
            actividades = mutableListOf("Pilates")
        )
    )

    // Historial de Comprobantes
    val comprobantes = mutableListOf(
        DatosComprobante(
            numero = "001245",
            cliente = "Juan Pablo Pérez",
            tipo = "Cuota",
            concepto = "Cuota mensual",
            actividades = "Fútbol",
            monto = "15000",
            formaPago = "Tarjeta",
            cuotas = "3",
            valorCuota = "5000"
        ),
        DatosComprobante(
            numero = "001246",
            cliente = "Pedro López",
            tipo = "Cuota",
            concepto = "Cuota mensual",
            actividades = "Natación",
            monto = "12000",
            formaPago = "Efectivo",
            cuotas = "1",
            valorCuota = "12000"
        ),
        DatosComprobante(
            numero = "001247",
            cliente = "María Gómez",
            tipo = "Cuota",
            concepto = "Cuota mensual",
            actividades = "Tennis",
            monto = "18000",
            formaPago = "Tarjeta",
            cuotas = "6",
            valorCuota = "3000"
        )
    )

    // Búsqueda centralizada por DNI o N° de Afiliado
    fun buscarSocio(criterio: String): Socio? {
        val busqueda = criterio.trim()
        return socios.find { it.dni == busqueda || it.numeroAfiliado == busqueda }
    }
}