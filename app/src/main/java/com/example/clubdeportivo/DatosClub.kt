package com.example.clubdeportivo

import kotlin.String

data class Administrador (
        val nombre: String,
        val contraseña: String,
        val dni: String,
        val mail: String,
        val tel: String,
        val activo: Boolean = true
        )


data class Actividad(
    val id: String,
    val nombre: String,
    val costo: Double,
    val horario: String = "20:00",
    val profesor: String = "Sin Asignar",
    val iconoResId: Int? = null
)


data class ClienteClub(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val email: String,
    val aptoFisico: Boolean,
    val esSocio: Boolean,
    val numeroAfiliado: String? = null,
    val cuotaMonto: String = "100000.00",
    val cuotaAlDia: Boolean = false,
    val vencimiento: String? = null,
    val actividades: MutableList<String> = mutableListOf()
)


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

object DatosClub {


    val administradores = listOf(
        Administrador("Brian","12345678","35974xxx","brianzairpaez@gmail.com","116834xxxx",true),
        Administrador("Hernan","Clave123","4444xxxxx","herna.schwindt1@gmail.com","",true),
        Administrador("Micaela","Michi123","xxxxxxxx","@gmail.com","",true),
        Administrador("Dora","Dora1234","xxxxxxxx","@gmail.com","",true)
    )

    val listaActividades = listOf(
        Actividad("ACT01", "Natación", 12000.0,"09:00", "Georgina Bardach", R.drawable.ic_natacion),
        Actividad("ACT02", "G. artistica", 10000.0,"17:00", "Nadia Comaneci",R.drawable.ic_gim),
        Actividad("ACT03", "Basquet", 9000.0,"19:00", "Manu Ginóbili", R.drawable.ic_basketball),
        Actividad("ACT04", "Karate", 8500.0, "18:00", "Miyagi Sensei",R.drawable.ic_karate),
        Actividad("ACT05", "Tennis", 18000.0,"16:00", "Guillermo Coria", R.drawable.ic_tennis),
        Actividad("ACT06", "Fútbol", 15000.0,"20:00", "Lionel Messi", R.drawable.ic_futbol),
        Actividad("ACT09", "Voley", 9500.0, "15:00", "Hugo Conte",R.drawable.ic_voley)
    )
    fun calcularTotalActividades(cliente: ClienteClub): Double? {
        var total = 0.0

        for (nombreActividad in cliente.actividades) {
            val actividad = listaActividades.firstOrNull {
                it.nombre.equals(
                    nombreActividad.trim(),
                    ignoreCase = true
                )
            } ?: return null

            total += actividad.costo
        }

        return total
    }


    val clientes = mutableListOf(
        ClienteClub(
            dni = "40123456",
            nombre = "Juan Pablo",
            apellido = "Pérez",
            telefono = "1144556677",
            email = "juanpablo@gmail.com",
            esSocio = true,
            aptoFisico = true,
            numeroAfiliado = "001245",
            cuotaAlDia = true,
            cuotaMonto = "150000.00",
            vencimiento = "02/10/2026",
            actividades = mutableListOf("Fútbol", "Musculación")
        ),
        ClienteClub(
            dni = "38987654",
            nombre = "Pedro",
            apellido = "López",
            telefono = "1122334455",
            email = "pedro.lopez@gmail.com",
            esSocio = true,
            aptoFisico = true,
            numeroAfiliado = "001246",
            cuotaAlDia = false,
            cuotaMonto = "150000.00",
            vencimiento = "02/10/2026",
            actividades = mutableListOf("Natación")
        ),
        ClienteClub(
            dni = "42111222",
            nombre = "María",
            apellido = "Gómez",
            telefono = "1199887766",
            email = "maria.gomez@gmail.com",
            esSocio = true,
            aptoFisico = true,
            numeroAfiliado = "001247",
            cuotaAlDia = true,
            cuotaMonto = "150000.00",
            vencimiento = "05/10/2026",
            actividades = mutableListOf("Tennis", "Yoga")
        ),
        ClienteClub(
            dni = "35444555",
            nombre = "Carlos",
            apellido = "Rodríguez",
            telefono = "1155443322",
            email = "carlos.rodriguez@gmail.com",
            esSocio = true,
            aptoFisico = true,
            numeroAfiliado = "001248",
            cuotaAlDia = true,
            cuotaMonto = "150000.00",
            vencimiento = "15/10/2026",
            actividades = mutableListOf("Paddle")
        ),
        ClienteClub(
            dni = "39888999",
            nombre = "Ana",
            apellido = "Martínez",
            telefono = "1166778899",
            email = "ana.martinez@gmail.com",
            esSocio = true,
            aptoFisico = false,
            numeroAfiliado = "001249",
            cuotaAlDia = false,
            cuotaMonto = "150000.00",
            vencimiento = "28/09/2026",
            actividades = mutableListOf("Pilates")
        )
    )

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

    fun buscarSocio(criterio: String): ClienteClub? {
        val busqueda = criterio.trim()
        return clientes.find { it.dni == busqueda || it.numeroAfiliado == busqueda }
    }

}