package com.example.clubdeportivo

data class Socio(
    val idSocio: String,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val cuotaAlDia: Boolean = true,
    val actividades: List<String> = emptyList()
)

object RepositorioUsuarios {
    val listaSocios = mutableListOf(
        Socio("1001", "12345678", "Juan", "Pérez", true, listOf("Musculación", "Spinning")),
        Socio("1002", "87654321", "María", "Gómez", false, listOf("Pileta")),
        Socio("1003", "11223344", "Carlos", "López", true, listOf("Crossfit", "Musculación"))
    )

    fun buscarPorDniOSocio(criterio: String): Socio? {
        return listaSocios.find { it.dni == criterio || it.idSocio == criterio }
    }
}