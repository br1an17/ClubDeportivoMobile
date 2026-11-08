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

        val createTableUsuarioActividad = """
            CREATE TABLE usuario_actividad (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dni_usuario TEXT,
                id_actividad INTEGER,
                FOREIGN KEY(dni_usuario) REFERENCES $TABLE_USUARIOS($COLUM_DNI),
                FOREIGN KEY(id_actividad) REFERENCES $TABLE_ACTIVIDADES($COLUMN_ACT_ID)
            )
        """

        db?.execSQL(createTableUsuarios)
        db?.execSQL(createTablePagos)
        db?.execSQL(createTableActividades)
        db?.execSQL(createTableAdministradores)
        db?.execSQL(createTableUsuarioActividad)

        db?.execSQL("""
            INSERT INTO $TABLE_ACTIVIDADES 
            ($COLUMN_ACT_NOMBRE, $COLUMN_ACT_COSTO, $COLUMN_ACT_HORARIO, $COLUMN_ACT_PROFESOR) 
            VALUES 
            ('Natación', 12000.0, '09:00', 'Georgina Bardach'),
            ('G. artistica', 10000.0, '17:00', 'Nadia Comaneci'),
            ('Basquet', 9000.0, '19:00', 'Manu Ginóbili'),
            ('Karate', 8500.0, '18:00', 'Miyagi Sensei'),
            ('Tennis', 18000.0, '16:00', 'Guillermo Coria'),
            ('Fútbol', 15000.0, '20:00', 'Lionel Messi'),
            ('Voley', 9500.0, '15:00', 'Hugo Conte')
        """)
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

    fun asignarActividadUsuario(dniUsuario: String, idActividad: Int): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("dni_usuario", dniUsuario)
            put("id_actividad", idActividad)
        }
        val resultado = db.insert("usuario_actividad", null, valores)
        db.close()
        return resultado != -1L
    }

    fun obtenerActividadesDeUsuario(dniUsuario: String): ArrayList<String>{
        val listaActividades = ArrayList<String>()
        val db = this.readableDatabase
        val query = """
            SELECT a.$COLUMN_ACT_NOMBRE
            FROM $TABLE_ACTIVIDADES a
            INNER JOIN usuario_actividad ua ON a.$COLUMN_ACT_ID = ua.id_actividad
            WHERE ua.dni_usuario = ?
        """
        val cursor = db.rawQuery(query, arrayOf(dniUsuario))

        if (cursor.moveToFirst()){
            do {
                val nombreActividad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACT_NOMBRE))
                listaActividades.add(nombreActividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaActividades
    }

    fun obtenerIdActividadPorNombre(nombreActividad: String): Int {
        var idActividad = -1
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ACT_ID FROM $TABLE_ACTIVIDADES WHERE $COLUMN_ACT_NOMBRE LIKE ?",
            arrayOf(nombreActividad)
        )
        if (cursor.moveToFirst()) {
            idActividad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACT_ID))
        }
        cursor.close()
        db.close()
        return idActividad
    }

    fun desasignarActividadDeUsuario(dniUsuario: String, idActividad: Int): Boolean {
        val db = this.writableDatabase
        val resultado = db.delete(
            "usuario_actividad",
            "dni_usuario = ? AND id_actividad = ?",
            arrayOf(dniUsuario, idActividad.toString())
        )
        db.close()
        return resultado > 0
    }


}
