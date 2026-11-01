package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SocioDBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
       //variables de usuario
        private const val DATABASE_NAME = "club_deportivo.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS= "usuarios"
        const val COLUM_ID="id"
        const val COLUM_DNI="DNI"
        const val COLUM_NOMBRE="nombre"
        const val COLUM_APELLIDO="apellido"
        const val COLUM_MAIL="email"
        const val COLUM_TELEFONO="telefono"
        const val COLUM_TIPO="tipo"
        const val COLUM_APTO_FISICO = "apto_fisico" // 0 o 1 (Boolean)
        const val COLUM_NUM_AFILIADO = "numero_afiliado"
        const val COLUM_CUOTA_MONTO = "cuota_monto"
        const val COLUM_CUOTA_AL_DIA = "cuota_al_dia" // 0 o 1 (Boolean)
        const val COLUM_VENCIMIENTO = "vencimiento"

        //variables de pagos
        const val TABLE_PAGOS = "pagos"
        const val COLUMN_PAGO_ID = "pago_id"
        const val COLUMN_PAGO_DNI = "dni_socio"
        const val COLUMN_MONTO = "monto"
        const val COLUMN_FECHA = "fecha"

        //variables de actividades
        const val TABLE_ACTIVIDADES = "actividades"
        const val COLUMN_ACT_ID = "act_id"
        const val COLUMN_ACT_NOMBRE = "nombre_actividad"
        const val COLUMN_ACT_PROFESOR = "profesor"
        const val COLUMN_ACT_COSTO = "costo"

        const val COLUMN_ACT_HORARIO = "horario"

        const val TABLE_ADMINISTRADORES = "administradores"
        const val COLUMN_ADMIN_ID = "admin_id"
        const val COLUMN_ADMIN_NOMBRE = "nombre"
        const val COLUMN_ADMIN_PASS = "contrasena"
        const val COLUMN_ADMIN_DNI = "dni"
        const val COLUMN_ADMIN_MAIL = "mail"
        const val COLUMN_ADMIN_TEL = "tel"
        const val COLUMN_ADMIN_ACTIVO = "activo"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsuarios =(
        "CREATE TABLE $TABLE_USUARIOS("
                + "$COLUM_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUM_DNI TEXT UNIQUE NOT NULL,"
                + "$COLUM_NOMBRE TEXT NOT NULL,"
                + "$COLUM_APELLIDO TEXT NOT NULL, "
                + "$COLUM_TELEFONO TEXT, "
                + "$COLUM_MAIL TEXT, "
                + "$COLUM_TIPO TEXT, "
                + "$COLUM_APTO_FISICO INTEGER, "
                + "$COLUM_NUM_AFILIADO TEXT, "
                + "$COLUM_CUOTA_MONTO TEXT, "
                + "$COLUM_CUOTA_AL_DIA INTEGER, "
                + "$COLUM_VENCIMIENTO TEXT)".trimIndent()
        )
        val createTablePagos = ("CREATE TABLE $TABLE_PAGOS ("
                + "$COLUMN_PAGO_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_PAGO_DNI TEXT, "
                + "$COLUMN_MONTO REAL, "
                + "$COLUMN_FECHA TEXT)")

        val createTableActividades = ("CREATE TABLE $TABLE_ACTIVIDADES ("
                + "$COLUMN_ACT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_ACT_NOMBRE TEXT NOT NULL, "
                + "$COLUMN_ACT_COSTO REAL, "
                + "$COLUMN_ACT_HORARIO TEXT, "
                + "$COLUMN_ACT_PROFESOR TEXT)")

        val createTableAdministradores = ("CREATE TABLE $TABLE_ADMINISTRADORES ("
                + "$COLUMN_ADMIN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_ADMIN_NOMBRE TEXT, "
                + "$COLUMN_ADMIN_PASS TEXT, "
                + "$COLUMN_ADMIN_DNI TEXT UNIQUE, "
                + "$COLUMN_ADMIN_MAIL TEXT, "
                + "$COLUMN_ADMIN_TEL TEXT, "
                + "$COLUMN_ADMIN_ACTIVO INTEGER)")


        db?.execSQL(createTableUsuarios)
        db?.execSQL(createTablePagos)
        db?.execSQL(createTableActividades)
        db?.execSQL(createTableAdministradores)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PAGOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVIDADES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMINISTRADORES")
        onCreate(db)
    }

    fun insertarUsuario(nombre: String,
                        apellido: String,
                        dni: String,
                        telefono: String,
                        email: String,
                        tipo: String,
                        aptoFisico: Int,         // 1 si tiene apto, 0 si no
                        numeroAfiliado: String,
                        cuotaMonto: String,
                        cuotaAlDia: Int,         // 1 si está al día, 0 si debe
                        vencimiento: String): Boolean{
        val db= this.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("telefono", telefono)
            put("email", email)
            put("tipo", tipo)
            put("apto_fisico", aptoFisico)
            put("numero_afiliado", numeroAfiliado)
            put("cuota_monto", cuotaMonto)
            put("cuota_al_dia", cuotaAlDia)
            put("vencimiento", vencimiento)
        }

        val resultado = db.insert("usuarios",null,valores)
        db.close()
        return resultado != -1L

    }
    fun buscarClientePorCriterio(criterio: String): ClienteClub? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COLUM_DNI = ? OR $COLUM_NUM_AFILIADO = ?",
            arrayOf(criterio, criterio)
        )

        var cliente: ClienteClub? = null

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUM_NOMBRE))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUM_APELLIDO))
            val dni = cursor.getString(cursor.getColumnIndexOrThrow(COLUM_DNI))
            val numAfiliado = cursor.getString(cursor.getColumnIndexOrThrow(COLUM_NUM_AFILIADO)) ?: ""

            cliente = ClienteClub(
                dni = dni,
                nombre = nombre,
                apellido = apellido,
                telefono = "",
                email = "",
                esSocio = true,
                aptoFisico = true,
                numeroAfiliado = numAfiliado,
                cuotaAlDia = true,
                cuotaMonto = "",
                vencimiento = "",
                actividades = mutableListOf()
            )
        }

        cursor.close()
        db.close()
        return cliente
    }
    }
