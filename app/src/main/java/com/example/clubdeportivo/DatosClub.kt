package com.example.clubdeportivo

data class Socio(
    val numeroAfiliado: String,
    val nombre: String,
    val dni: String,
    val actividad: String,
    val cuota: String,
    val vencimiento: String
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

    val socios = arrayOf(

        Socio(
            numeroAfiliado = "001245",
            nombre = "Juan Pablo",
            dni = "40123456",
            actividad = "Fútbol",
            cuota = "15000",
            vencimiento = "02/10/2026"
        ),

        Socio(
            numeroAfiliado = "001246",
            nombre = "Pedro López",
            dni = "38987654",
            actividad = "Natación",
            cuota = "12000",
            vencimiento = "02/10/2026"
        ),

        Socio(
            numeroAfiliado = "001247",
            nombre = "María Gómez",
            dni = "42111222",
            actividad = "Tenis",
            cuota = "18000",
            vencimiento = "05/10/2026"
        )
    )

    val comprobantes = arrayOf(

        DatosComprobante(
            numero = "001245",
            cliente = "Juan Pablo",
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
            actividades = "Tenis",
            monto = "18000",
            formaPago = "Tarjeta",
            cuotas = "6",
            valorCuota = "3000"
        )
    )
}