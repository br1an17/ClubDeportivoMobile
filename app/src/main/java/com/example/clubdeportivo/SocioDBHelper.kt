
package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class DetalleFacturaInput(
    val concepto: String,
    val descripcion: String,
    val precioUnitario: Double,
    val cantidad: Int
)

class SocioDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "club_deportivo.db"
        private const val DATABASE_VERSION = 3

        // =====================================================
        // USUARIOS
        // =====================================================

        const val TABLE_USUARIOS = "usuarios"

        const val COLUM_ID = "id"
        const val COLUM_DNI = "DNI"
        const val COLUM_NOMBRE = "nombre"
        const val COLUM_APELLIDO = "apellido"
        const val COLUM_MAIL = "email"
        const val COLUM_TELEFONO = "telefono"
        const val COLUM_TIPO = "tipo"
        const val COLUM_APTO_FISICO = "apto_fisico"
        const val COLUM_NUM_AFILIADO = "numero_afiliado"
        const val COLUM_CUOTA_MONTO = "cuota_monto"
        const val COLUM_CUOTA_AL_DIA = "cuota_al_dia"
        const val COLUM_VENCIMIENTO = "vencimiento"

        // =====================================================
        // PAGOS
        // =====================================================

        const val TABLE_PAGOS = "pagos"

        const val COLUMN_PAGO_ID = "pago_id"
        const val COLUMN_PAGO_DNI = "dni_socio"
        const val COLUMN_MONTO = "monto"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_PAGO_FACTURA_ID = "factura_id"
        const val COLUMN_MEDIO_PAGO = "medio_pago"
        const val COLUMN_CUOTAS_PAGO = "cuotas"
        const val COLUMN_VALOR_CUOTA = "valor_cuota"
        const val COLUMN_ESTADO_PAGO = "estado"

        // =====================================================
        // ACTIVIDADES
        // =====================================================

        const val TABLE_ACTIVIDADES = "actividades"

        const val COLUMN_ACT_ID = "act_id"
        const val COLUMN_ACT_CODIGO = "codigo"
        const val COLUMN_ACT_NOMBRE = "nombre_actividad"
        const val COLUMN_ACT_PROFESOR = "profesor"
        const val COLUMN_ACT_COSTO = "costo"
        const val COLUMN_ACT_HORARIO = "horario"
        const val COLUMN_ACT_ICONO = "icono_res_id"

        // =====================================================
        // RELACIÓN USUARIO - ACTIVIDADES
        // =====================================================

        const val TABLE_USUARIO_ACTIVIDADES = "usuario_actividades"

        const val COLUMN_REL_DNI = "dni_socio"
        const val COLUMN_REL_ACTIVIDAD = "act_id"
        const val COLUMN_REL_FECHA = "fecha_asignacion"

        // =====================================================
        // FACTURAS
        // =====================================================

        const val TABLE_FACTURAS = "facturas"

        const val COLUMN_FACTURA_ID = "factura_id"
        const val COLUMN_FACTURA_NUMERO = "numero_factura"
        const val COLUMN_FACTURA_DNI = "dni_socio"
        const val COLUMN_FACTURA_FECHA = "fecha_emision"
        const val COLUMN_FACTURA_SUBTOTAL = "subtotal"
        const val COLUMN_FACTURA_TOTAL = "total"
        const val COLUMN_FACTURA_ESTADO = "estado"
        const val COLUMN_FACTURA_MEDIO_PAGO = "forma_pago"
        const val COLUMN_FACTURA_CUOTAS = "cantidad_cuotas"

        // =====================================================
        // DETALLE DE FACTURAS
        // =====================================================

        const val TABLE_DETALLE_FACTURA = "detalle_factura"

        const val COLUMN_DETALLE_ID = "detalle_id"
        const val COLUMN_DETALLE_FACTURA_ID = "factura_id"
        const val COLUMN_DETALLE_CONCEPTO = "concepto"
        const val COLUMN_DETALLE_DESCRIPCION = "descripcion"
        const val COLUMN_DETALLE_PRECIO = "precio_unitario"
        const val COLUMN_DETALLE_CANTIDAD = "cantidad"
        const val COLUMN_DETALLE_SUBTOTAL = "subtotal"

        // =====================================================
        // ADMINISTRADORES
        // =====================================================

        const val TABLE_ADMINISTRADORES = "administradores"

        const val COLUMN_ADMIN_ID = "admin_id"
        const val COLUMN_ADMIN_NOMBRE = "nombre"
        const val COLUMN_ADMIN_PASS = "contrasena"
        const val COLUMN_ADMIN_DNI = "dni"
        const val COLUMN_ADMIN_MAIL = "mail"
        const val COLUMN_ADMIN_TEL = "tel"
        const val COLUMN_ADMIN_ACTIVO = "activo"
    }

    // =====================================================
    // CONFIGURACIÓN DE SQLITE
    // =====================================================

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // =====================================================
    // CREAR BASE DE DATOS
    // =====================================================

    override fun onCreate(db: SQLiteDatabase) {

        crearTablaUsuarios(db)
        crearTablaActividades(db)
        crearTablaAdministradores(db)
        crearTablaFacturas(db)
        crearTablaDetalleFactura(db)
        crearTablaUsuarioActividades(db)
        crearTablaPagos(db)

        crearIndices(db)
    }


// =====================================================
// ACTUALIZAR BASE SIN BORRAR DATOS
// =====================================================

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        // MIGRACIÓN DE LA VERSIÓN 1 A LA 2
        if (oldVersion < 2) {

            agregarColumnaSiNoExiste(
                db,
                TABLE_ACTIVIDADES,
                COLUMN_ACT_CODIGO,
                "TEXT"
            )

            agregarColumnaSiNoExiste(
                db,
                TABLE_ACTIVIDADES,
                COLUMN_ACT_ICONO,
                "INTEGER"
            )

            crearTablaFacturas(db)
            crearTablaDetalleFactura(db)
            crearTablaUsuarioActividades(db)

            agregarColumnaSiNoExiste(
                db,
                TABLE_PAGOS,
                COLUMN_PAGO_FACTURA_ID,
                "INTEGER"
            )

            agregarColumnaSiNoExiste(
                db,
                TABLE_PAGOS,
                COLUMN_MEDIO_PAGO,
                "TEXT"
            )

            agregarColumnaSiNoExiste(
                db,
                TABLE_PAGOS,
                COLUMN_CUOTAS_PAGO,
                "INTEGER DEFAULT 1"
            )

            agregarColumnaSiNoExiste(
                db,
                TABLE_PAGOS,
                COLUMN_VALOR_CUOTA,
                "REAL"
            )

            agregarColumnaSiNoExiste(
                db,
                TABLE_PAGOS,
                COLUMN_ESTADO_PAGO,
                "TEXT DEFAULT 'REGISTRADO'"
            )

            crearIndices(db)
        }

        // MIGRACIÓN DE LA VERSIÓN 2 A LA 3
        if (oldVersion < 3) {

            crearTablaUsuarioActividades(db)

            migrarActividadesAsignadasAntiguas(db)
        }
    }

// =====================================================
// MIGRAR ACTIVIDADES DE LA TABLA ANTIGUA
// =====================================================

    private fun migrarActividadesAsignadasAntiguas(
        db: SQLiteDatabase
    ) {

        // Comprobar si existe la tabla antigua
        val cursor = db.rawQuery(
            """
        SELECT 1
        FROM sqlite_master
        WHERE type = 'table'
          AND name = ?
        """.trimIndent(),
            arrayOf("usuario_actividad")
        )

        val tablaExiste = try {
            cursor.moveToFirst()
        } finally {
            cursor.close()
        }

        if (!tablaExiste) return

        // Copiar las asignaciones antiguas a la tabla nueva
        db.execSQL(
            """
        INSERT OR IGNORE INTO $TABLE_USUARIO_ACTIVIDADES (
            $COLUMN_REL_DNI,
            $COLUMN_REL_ACTIVIDAD,
            $COLUMN_REL_FECHA
        )
        SELECT
            antigua.dni_usuario,
            antigua.id_actividad,
            NULL
        FROM usuario_actividad antigua
        INNER JOIN $TABLE_USUARIOS socio
            ON socio.$COLUM_DNI = antigua.dni_usuario
        INNER JOIN $TABLE_ACTIVIDADES actividad
            ON actividad.$COLUMN_ACT_ID = antigua.id_actividad
        WHERE antigua.dni_usuario IS NOT NULL
          AND antigua.id_actividad IS NOT NULL
        """.trimIndent()
        )
    }

// =====================================================
// AGREGAR COLUMNAS SIN DUPLICARLAS
// =====================================================

    private fun agregarColumnaSiNoExiste(
        db: SQLiteDatabase,
        tabla: String,
        columna: String,
        definicion: String
    ) {

        val cursor = db.rawQuery(
            "PRAGMA table_info($tabla)",
            null
        )

        var existe = false

        try {
            while (cursor.moveToNext()) {

                val nombreColumna =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                    )

                if (nombreColumna.equals(columna, ignoreCase = true)) {
                    existe = true
                    break
                }
            }
        } finally {
            cursor.close()
        }

        if (!existe) {
            db.execSQL(
                "ALTER TABLE $tabla ADD COLUMN $columna $definicion"
            )
        }
    }


    // =====================================================
    // CREAR TABLA USUARIOS
    // =====================================================

    private fun crearTablaUsuarios(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_USUARIOS (
                $COLUM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUM_DNI TEXT UNIQUE NOT NULL,
                $COLUM_NOMBRE TEXT NOT NULL,
                $COLUM_APELLIDO TEXT NOT NULL,
                $COLUM_TELEFONO TEXT,
                $COLUM_MAIL TEXT,
                $COLUM_TIPO TEXT,
                $COLUM_APTO_FISICO INTEGER DEFAULT 0,
                $COLUM_NUM_AFILIADO TEXT,
                $COLUM_CUOTA_MONTO TEXT,
                $COLUM_CUOTA_AL_DIA INTEGER DEFAULT 0,
                $COLUM_VENCIMIENTO TEXT
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA ACTIVIDADES
    // =====================================================

    private fun crearTablaActividades(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVIDADES (
                $COLUMN_ACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACT_CODIGO TEXT UNIQUE,
                $COLUMN_ACT_NOMBRE TEXT NOT NULL,
                $COLUMN_ACT_COSTO REAL NOT NULL,
                $COLUMN_ACT_HORARIO TEXT,
                $COLUMN_ACT_PROFESOR TEXT,
                $COLUMN_ACT_ICONO INTEGER
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA ADMINISTRADORES
    // =====================================================

    private fun crearTablaAdministradores(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_ADMINISTRADORES (
                $COLUMN_ADMIN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADMIN_NOMBRE TEXT,
                $COLUMN_ADMIN_PASS TEXT,
                $COLUMN_ADMIN_DNI TEXT UNIQUE,
                $COLUMN_ADMIN_MAIL TEXT,
                $COLUMN_ADMIN_TEL TEXT,
                $COLUMN_ADMIN_ACTIVO INTEGER DEFAULT 1
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA FACTURAS
    // =====================================================

    private fun crearTablaFacturas(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_FACTURAS (
                $COLUMN_FACTURA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FACTURA_NUMERO TEXT UNIQUE NOT NULL,
                $COLUMN_FACTURA_DNI TEXT NOT NULL,
                $COLUMN_FACTURA_FECHA TEXT NOT NULL,
                $COLUMN_FACTURA_SUBTOTAL REAL NOT NULL,
                $COLUMN_FACTURA_TOTAL REAL NOT NULL,
                $COLUMN_FACTURA_ESTADO TEXT NOT NULL DEFAULT 'EMITIDA',
                $COLUMN_FACTURA_MEDIO_PAGO TEXT,
                $COLUMN_FACTURA_CUOTAS INTEGER DEFAULT 1,
                FOREIGN KEY ($COLUMN_FACTURA_DNI)
                    REFERENCES $TABLE_USUARIOS($COLUM_DNI)
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA DETALLE FACTURA
    // =====================================================

    private fun crearTablaDetalleFactura(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_DETALLE_FACTURA (
                $COLUMN_DETALLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DETALLE_FACTURA_ID INTEGER NOT NULL,
                $COLUMN_DETALLE_CONCEPTO TEXT NOT NULL,
                $COLUMN_DETALLE_DESCRIPCION TEXT,
                $COLUMN_DETALLE_PRECIO REAL NOT NULL,
                $COLUMN_DETALLE_CANTIDAD INTEGER NOT NULL,
                $COLUMN_DETALLE_SUBTOTAL REAL NOT NULL,
                FOREIGN KEY ($COLUMN_DETALLE_FACTURA_ID)
                    REFERENCES $TABLE_FACTURAS($COLUMN_FACTURA_ID)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA USUARIO - ACTIVIDADES
    // =====================================================

    private fun crearTablaUsuarioActividades(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_USUARIO_ACTIVIDADES (
                $COLUMN_REL_DNI TEXT NOT NULL,
                $COLUMN_REL_ACTIVIDAD INTEGER NOT NULL,
                $COLUMN_REL_FECHA TEXT,
                PRIMARY KEY ($COLUMN_REL_DNI, $COLUMN_REL_ACTIVIDAD),
                FOREIGN KEY ($COLUMN_REL_DNI)
                    REFERENCES $TABLE_USUARIOS($COLUM_DNI)
                    ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_REL_ACTIVIDAD)
                    REFERENCES $TABLE_ACTIVIDADES($COLUMN_ACT_ID)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR TABLA PAGOS
    // =====================================================

    private fun crearTablaPagos(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PAGOS (
                $COLUMN_PAGO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PAGO_DNI TEXT NOT NULL,
                $COLUMN_MONTO REAL NOT NULL,
                $COLUMN_FECHA TEXT NOT NULL,
                $COLUMN_PAGO_FACTURA_ID INTEGER,
                $COLUMN_MEDIO_PAGO TEXT,
                $COLUMN_CUOTAS_PAGO INTEGER DEFAULT 1,
                $COLUMN_VALOR_CUOTA REAL,
                $COLUMN_ESTADO_PAGO TEXT DEFAULT 'REGISTRADO',
                FOREIGN KEY ($COLUMN_PAGO_DNI)
                    REFERENCES $TABLE_USUARIOS($COLUM_DNI),
                FOREIGN KEY ($COLUMN_PAGO_FACTURA_ID)
                    REFERENCES $TABLE_FACTURAS($COLUMN_FACTURA_ID)
            )
            """.trimIndent()
        )
    }

    // =====================================================
    // CREAR ÍNDICES
    // =====================================================

    private fun crearIndices(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS idx_actividad_codigo " +
                    "ON $TABLE_ACTIVIDADES($COLUMN_ACT_CODIGO)"
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_facturas_dni " +
                    "ON $TABLE_FACTURAS($COLUMN_FACTURA_DNI)"
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_pagos_dni " +
                    "ON $TABLE_PAGOS($COLUMN_PAGO_DNI)"
        )
    }

    // =====================================================
    // INSERTAR USUARIO
    // =====================================================

    fun insertarUsuario(
        nombre: String,
        apellido: String,
        dni: String,
        telefono: String,
        email: String,
        tipo: String,
        aptoFisico: Int,
        numeroAfiliado: String,
        cuotaMonto: String,
        cuotaAlDia: Int,
        vencimiento: String
    ): Boolean {

        val db = writableDatabase

        val valores = ContentValues().apply {

            put(COLUM_NOMBRE, nombre)
            put(COLUM_APELLIDO, apellido)
            put(COLUM_DNI, dni)
            put(COLUM_TELEFONO, telefono)
            put(COLUM_MAIL, email)
            put(COLUM_TIPO, tipo)
            put(COLUM_APTO_FISICO, aptoFisico)
            put(COLUM_NUM_AFILIADO, numeroAfiliado)
            put(COLUM_CUOTA_MONTO, cuotaMonto)
            put(COLUM_CUOTA_AL_DIA, cuotaAlDia)
            put(COLUM_VENCIMIENTO, vencimiento)
        }

        return try {
            db.insert(TABLE_USUARIOS, null, valores) != -1L
        } finally {
            db.close()
        }
    }

    // =====================================================
    // BUSCAR CLIENTE
    // =====================================================

    fun buscarClientePorCriterio(criterio: String): ClienteClub? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COLUM_DNI = ?
               OR $COLUM_NUM_AFILIADO = ?
               OR $COLUM_NOMBRE LIKE ?
               OR $COLUM_APELLIDO LIKE ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(
                criterio,
                criterio,
                "%$criterio%",
                "%$criterio%"
            )
        )

        val cliente = try {

            if (cursor.moveToFirst()) {
                cursorACliente(cursor, db)
            } else {
                null
            }

        } finally {
            cursor.close()
            db.close()
        }

        return cliente
    }

    // =====================================================
    // BUSCAR CLIENTE POR DNI
    // =====================================================

    fun obtenerClientePorDni(dni: String): ClienteClub? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COLUM_DNI = ?",
            arrayOf(dni)
        )

        val cliente = try {

            if (cursor.moveToFirst()) {
                cursorACliente(cursor, db)
            } else {
                null
            }

        } finally {
            cursor.close()
            db.close()
        }

        return cliente
    }

    // =====================================================
    // CONVERTIR CURSOR EN CLIENTE
    // =====================================================

    private fun cursorACliente(
        cursor: android.database.Cursor,
        db: SQLiteDatabase
    ): ClienteClub {

        val dni = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUM_DNI)
        )

        val tipo = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUM_TIPO)
        ) ?: ""

        return ClienteClub(

            dni = dni,

            nombre = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_NOMBRE)
            ),

            apellido = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_APELLIDO)
            ),

            telefono = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_TELEFONO)
            ) ?: "",

            email = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_MAIL)
            ) ?: "",

            esSocio = tipo.equals("Socio", ignoreCase = true),

            aptoFisico = cursor.getInt(
                cursor.getColumnIndexOrThrow(COLUM_APTO_FISICO)
            ) == 1,

            numeroAfiliado = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_NUM_AFILIADO)
            ),

            cuotaMonto = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_CUOTA_MONTO)
            ) ?: "0.00",

            cuotaAlDia = cursor.getInt(
                cursor.getColumnIndexOrThrow(COLUM_CUOTA_AL_DIA)
            ) == 1,

            vencimiento = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUM_VENCIMIENTO)
            ),

            actividades = obtenerActividadesAsignadas(
                db,
                dni
            )
        )
    }

    // =====================================================
    // SINCRONIZAR CATÁLOGO DE ACTIVIDADES
    // =====================================================

    fun sincronizarCatalogoActividades(
        actividades: List<Actividad>
    ) {

        val db = writableDatabase

        db.beginTransaction()

        try {

            for (actividad in actividades) {

                var idExistente: Long? = null

                // Buscar por código
                val cursorCodigo = db.rawQuery(
                    """
                    SELECT $COLUMN_ACT_ID
                    FROM $TABLE_ACTIVIDADES
                    WHERE $COLUMN_ACT_CODIGO = ?
                    LIMIT 1
                    """.trimIndent(),
                    arrayOf(actividad.id)
                )

                if (cursorCodigo.moveToFirst()) {
                    idExistente = cursorCodigo.getLong(0)
                }

                cursorCodigo.close()

                // Si no existe el código, buscar por nombre
                if (idExistente == null) {

                    val cursorNombre = db.rawQuery(
                        """
                        SELECT $COLUMN_ACT_ID
                        FROM $TABLE_ACTIVIDADES
                        WHERE $COLUMN_ACT_NOMBRE = ? COLLATE NOCASE
                        LIMIT 1
                        """.trimIndent(),
                        arrayOf(actividad.nombre)
                    )

                    if (cursorNombre.moveToFirst()) {
                        idExistente = cursorNombre.getLong(0)
                    }

                    cursorNombre.close()
                }

                val valores = ContentValues().apply {

                    put(COLUMN_ACT_CODIGO, actividad.id)
                    put(COLUMN_ACT_NOMBRE, actividad.nombre)
                    put(COLUMN_ACT_COSTO, actividad.costo)
                    put(COLUMN_ACT_HORARIO, actividad.horario)
                    put(COLUMN_ACT_PROFESOR, actividad.profesor)

                    if (actividad.iconoResId != null) {
                        put(COLUMN_ACT_ICONO, actividad.iconoResId)
                    } else {
                        putNull(COLUMN_ACT_ICONO)
                    }
                }

                if (idExistente != null) {

                    db.update(
                        TABLE_ACTIVIDADES,
                        valores,
                        "$COLUMN_ACT_ID = ?",
                        arrayOf(idExistente.toString())
                    )

                } else {

                    db.insert(
                        TABLE_ACTIVIDADES,
                        null,
                        valores
                    )
                }
            }

            db.setTransactionSuccessful()

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // =====================================================
    // OBTENER CATÁLOGO DE ACTIVIDADES
    // =====================================================

    fun obtenerCatalogoActividades(): List<Actividad> {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ACTIVIDADES ORDER BY $COLUMN_ACT_NOMBRE",
            null
        )

        val lista = mutableListOf<Actividad>()

        try {

            while (cursor.moveToNext()) {

                val idReal = cursor.getLong(
                    cursor.getColumnIndexOrThrow(COLUMN_ACT_ID)
                )

                val codigo = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_ACT_CODIGO)
                ) ?: idReal.toString()

                val iconoIndex =
                    cursor.getColumnIndexOrThrow(COLUMN_ACT_ICONO)

                val icono = if (cursor.isNull(iconoIndex)) {
                    null
                } else {
                    cursor.getInt(iconoIndex)
                }

                lista.add(
                    Actividad(
                        id = codigo,
                        nombre = cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_ACT_NOMBRE)
                        ),
                        costo = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(COLUMN_ACT_COSTO)
                        ),
                        horario = cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_ACT_HORARIO)
                        ) ?: "20:00",
                        profesor = cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_ACT_PROFESOR)
                        ) ?: "Sin Asignar",
                        iconoResId = icono
                    )
                )
            }

        } finally {
            cursor.close()
            db.close()
        }

        return lista
    }

    // =====================================================
    // VERIFICAR SI EL CLIENTE YA TIENE UNA ACTIVIDAD
    // =====================================================

    fun tieneActividadAsignada(
        dniSocio: String,
        nombreActividad: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 1
            FROM $TABLE_USUARIO_ACTIVIDADES ua
            INNER JOIN $TABLE_ACTIVIDADES a
                ON ua.$COLUMN_REL_ACTIVIDAD = a.$COLUMN_ACT_ID
            WHERE ua.$COLUMN_REL_DNI = ?
              AND a.$COLUMN_ACT_NOMBRE = ? COLLATE NOCASE
            LIMIT 1
            """.trimIndent(),
            arrayOf(dniSocio, nombreActividad)
        )

        val existe = try {
            cursor.moveToFirst()
        } finally {
            cursor.close()
            db.close()
        }

        return existe
    }

    // =====================================================
    // ASIGNAR ACTIVIDAD A CLIENTE
    // =====================================================

    fun asignarActividadASocio(
        dniSocio: String,
        nombreActividad: String
    ): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {

            // Buscar la actividad en el catálogo
            val cursor = db.rawQuery(
                """
                SELECT $COLUMN_ACT_ID
                FROM $TABLE_ACTIVIDADES
                WHERE $COLUMN_ACT_NOMBRE = ? COLLATE NOCASE
                   OR $COLUMN_ACT_CODIGO = ?
                LIMIT 1
                """.trimIndent(),
                arrayOf(nombreActividad, nombreActividad)
            )

            if (!cursor.moveToFirst()) {
                cursor.close()
                return false
            }

            val actividadId = cursor.getLong(0)
            cursor.close()

            val valores = ContentValues().apply {
                put(COLUMN_REL_DNI, dniSocio)
                put(COLUMN_REL_ACTIVIDAD, actividadId)
                put(COLUMN_REL_FECHA, System.currentTimeMillis().toString())
            }

            val resultado = db.insert(
                TABLE_USUARIO_ACTIVIDADES,
                null,
                valores
            )

            if (resultado == -1L) {
                return false
            }

            db.setTransactionSuccessful()

            return true

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // =====================================================
    // OBTENER ACTIVIDADES DE UN CLIENTE
    // =====================================================

    private fun obtenerActividadesAsignadas(
        db: SQLiteDatabase,
        dniSocio: String
    ): MutableList<String> {

        val cursor = db.rawQuery(
            """
            SELECT a.$COLUMN_ACT_NOMBRE
            FROM $TABLE_USUARIO_ACTIVIDADES ua
            INNER JOIN $TABLE_ACTIVIDADES a
                ON ua.$COLUMN_REL_ACTIVIDAD = a.$COLUMN_ACT_ID
            WHERE ua.$COLUMN_REL_DNI = ?
            ORDER BY a.$COLUMN_ACT_NOMBRE
            """.trimIndent(),
            arrayOf(dniSocio)
        )

        val actividades = mutableListOf<String>()

        try {
            while (cursor.moveToNext()) {
                actividades.add(cursor.getString(0))
            }
        } finally {
            cursor.close()
        }

        return actividades
    }

    // =====================================================
    // QUITAR ACTIVIDAD DE CLIENTE
    // =====================================================

    fun quitarActividadASocio(
        dniSocio: String,
        nombreActividad: String
    ): Boolean {

        val db = writableDatabase

        return try {

            val resultado = db.delete(
                TABLE_USUARIO_ACTIVIDADES,
                """
                $COLUMN_REL_DNI = ?
                AND $COLUMN_REL_ACTIVIDAD = (
                    SELECT $COLUMN_ACT_ID
                    FROM $TABLE_ACTIVIDADES
                    WHERE $COLUMN_ACT_NOMBRE = ? COLLATE NOCASE
                )
                """.trimIndent(),
                arrayOf(dniSocio, nombreActividad)
            )

            resultado > 0

        } finally {
            db.close()
        }
    }

    // =====================================================
    // CREAR FACTURA CON SUS DETALLES
    // =====================================================

    fun insertarFactura(
        numeroFactura: String,
        dniSocio: String,
        fechaEmision: String,
        subtotal: Double,
        total: Double,
        estado: String,
        formaPago: String,
        cantidadCuotas: Int,
        detalles: List<DetalleFacturaInput>
    ): Long {

        val db = writableDatabase

        db.beginTransaction()

        try {

            val valoresFactura = ContentValues().apply {

                put(COLUMN_FACTURA_NUMERO, numeroFactura)
                put(COLUMN_FACTURA_DNI, dniSocio)
                put(COLUMN_FACTURA_FECHA, fechaEmision)
                put(COLUMN_FACTURA_SUBTOTAL, subtotal)
                put(COLUMN_FACTURA_TOTAL, total)
                put(COLUMN_FACTURA_ESTADO, estado)
                put(COLUMN_FACTURA_MEDIO_PAGO, formaPago)
                put(COLUMN_FACTURA_CUOTAS, cantidadCuotas)
            }

            val facturaId = db.insert(
                TABLE_FACTURAS,
                null,
                valoresFactura
            )

            if (facturaId == -1L) {
                return -1L
            }

            // Guardar cada concepto de la factura
            for (detalle in detalles) {

                val valoresDetalle = ContentValues().apply {

                    put(COLUMN_DETALLE_FACTURA_ID, facturaId)
                    put(COLUMN_DETALLE_CONCEPTO, detalle.concepto)
                    put(COLUMN_DETALLE_DESCRIPCION, detalle.descripcion)
                    put(COLUMN_DETALLE_PRECIO, detalle.precioUnitario)
                    put(COLUMN_DETALLE_CANTIDAD, detalle.cantidad)

                    put(
                        COLUMN_DETALLE_SUBTOTAL,
                        detalle.precioUnitario * detalle.cantidad
                    )
                }

                val resultadoDetalle = db.insert(
                    TABLE_DETALLE_FACTURA,
                    null,
                    valoresDetalle
                )

                if (resultadoDetalle == -1L) {
                    return -1L
                }
            }

            db.setTransactionSuccessful()

            return facturaId

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // =====================================================
    // REGISTRAR PAGO
    // =====================================================

    fun registrarPago(
        dniSocio: String,
        monto: Double,
        fecha: String,
        facturaId: Long?,
        medioPago: String,
        cuotas: Int,
        valorCuota: Double
    ): Boolean {

        val db = writableDatabase

        val valores = ContentValues().apply {

            put(COLUMN_PAGO_DNI, dniSocio)
            put(COLUMN_MONTO, monto)
            put(COLUMN_FECHA, fecha)

            if (facturaId != null) {
                put(COLUMN_PAGO_FACTURA_ID, facturaId)
            } else {
                putNull(COLUMN_PAGO_FACTURA_ID)
            }

            put(COLUMN_MEDIO_PAGO, medioPago)
            put(COLUMN_CUOTAS_PAGO, cuotas)
            put(COLUMN_VALOR_CUOTA, valorCuota)
            put(COLUMN_ESTADO_PAGO, "REGISTRADO")
        }

        return try {
            db.insert(TABLE_PAGOS, null, valores) != -1L
        } finally {
            db.close()
        }
    }
    // =====================================================
// CARGAR DATOS INICIALES SIN DUPLICAR CLIENTES
// =====================================================

    fun cargarDatosInicialesSiFaltan() {

        // Cargar el catálogo de actividades
        sincronizarCatalogoActividades(
            DatosClub.listaActividades
        )

        // Importar clientes de prueba que todavía no existan
        for (cliente in DatosClub.clientes) {

            val clienteExistente =
                obtenerClientePorDni(cliente.dni)

            if (clienteExistente == null) {

                val insertado = insertarUsuario(
                    nombre = cliente.nombre,
                    apellido = cliente.apellido,
                    dni = cliente.dni,
                    telefono = cliente.telefono,
                    email = cliente.email,
                    tipo = if (cliente.esSocio) "Socio" else "No socio",
                    aptoFisico = if (cliente.aptoFisico) 1 else 0,
                    numeroAfiliado = cliente.numeroAfiliado.orEmpty(),
                    cuotaMonto = cliente.cuotaMonto,
                    cuotaAlDia = if (cliente.cuotaAlDia) 1 else 0,
                    vencimiento = cliente.vencimiento.orEmpty()
                )

                if (!insertado) {
                    continue
                }
            }

            // Recuperar las actividades asignadas
            for (actividad in cliente.actividades) {

                if (!tieneActividadAsignada(
                        cliente.dni,
                        actividad
                    )
                ) {
                    asignarActividadASocio(
                        cliente.dni,
                        actividad
                    )
                }
            }
        }
    }

    // =====================================================
    // OBTENER DETALLE DE ACTIVIDADES DE UN SOCIO
    // =====================================================

    fun obtenerDetallesActividadesDeSocio(
        dniSocio: String
    ): List<DetalleFacturaInput> {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                a.$COLUMN_ACT_NOMBRE,
                a.$COLUMN_ACT_COSTO
            FROM $TABLE_USUARIO_ACTIVIDADES ua
            INNER JOIN $TABLE_ACTIVIDADES a
                ON ua.$COLUMN_REL_ACTIVIDAD = a.$COLUMN_ACT_ID
            WHERE ua.$COLUMN_REL_DNI = ?
            ORDER BY a.$COLUMN_ACT_NOMBRE
            """.trimIndent(),
            arrayOf(dniSocio)
        )

        val detalles = mutableListOf<DetalleFacturaInput>()

        try {

            while (cursor.moveToNext()) {

                val nombre = cursor.getString(0)
                val precio = cursor.getDouble(1)

                detalles.add(
                    DetalleFacturaInput(
                        concepto = "Actividad",
                        descripcion = nombre,
                        precioUnitario = precio,
                        cantidad = 1
                    )
                )
            }

        } finally {
            cursor.close()
            db.close()
        }

        return detalles
    }

    // =====================================================
    // REGISTRAR FACTURA Y PAGO EN UNA TRANSACCIÓN
    // =====================================================

    fun registrarFacturaConPago(
        numeroFactura: String,
        dniSocio: String,
        fecha: String,
        formaPago: String,
        cantidadCuotas: Int,
        detalles: List<DetalleFacturaInput>
    ): Long {

        if (
            numeroFactura.isBlank() ||
            dniSocio.isBlank() ||
            formaPago.isBlank() ||
            cantidadCuotas <= 0 ||
            detalles.isEmpty()
        ) {
            return -1L
        }

        if (detalles.any {
                it.precioUnitario <= 0 || it.cantidad <= 0
            }
        ) {
            return -1L
        }

        val total = detalles.sumOf {
            it.precioUnitario * it.cantidad
        }

        if (total <= 0) {
            return -1L
        }

        val db = writableDatabase

        db.beginTransaction()

        try {

            // =============================================
            // INSERTAR FACTURA
            // =============================================

            val valoresFactura = ContentValues().apply {

                put(COLUMN_FACTURA_NUMERO, numeroFactura)
                put(COLUMN_FACTURA_DNI, dniSocio)
                put(COLUMN_FACTURA_FECHA, fecha)
                put(COLUMN_FACTURA_SUBTOTAL, total)
                put(COLUMN_FACTURA_TOTAL, total)
                put(COLUMN_FACTURA_ESTADO, "PAGADA")
                put(COLUMN_FACTURA_MEDIO_PAGO, formaPago)
                put(COLUMN_FACTURA_CUOTAS, cantidadCuotas)
            }

            val facturaId = db.insert(
                TABLE_FACTURAS,
                null,
                valoresFactura
            )

            if (facturaId == -1L) {
                return -1L
            }

            // =============================================
            // INSERTAR DETALLES
            // =============================================

            for (detalle in detalles) {

                val valoresDetalle = ContentValues().apply {

                    put(
                        COLUMN_DETALLE_FACTURA_ID,
                        facturaId
                    )

                    put(
                        COLUMN_DETALLE_CONCEPTO,
                        detalle.concepto
                    )

                    put(
                        COLUMN_DETALLE_DESCRIPCION,
                        detalle.descripcion
                    )

                    put(
                        COLUMN_DETALLE_PRECIO,
                        detalle.precioUnitario
                    )

                    put(
                        COLUMN_DETALLE_CANTIDAD,
                        detalle.cantidad
                    )

                    put(
                        COLUMN_DETALLE_SUBTOTAL,
                        detalle.precioUnitario * detalle.cantidad
                    )
                }

                val resultadoDetalle = db.insert(
                    TABLE_DETALLE_FACTURA,
                    null,
                    valoresDetalle
                )

                if (resultadoDetalle == -1L) {
                    return -1L
                }
            }

            // =============================================
            // INSERTAR PAGO
            // =============================================

            val valoresPago = ContentValues().apply {

                put(COLUMN_PAGO_DNI, dniSocio)
                put(COLUMN_MONTO, total)
                put(COLUMN_FECHA, fecha)
                put(COLUMN_PAGO_FACTURA_ID, facturaId)
                put(COLUMN_MEDIO_PAGO, formaPago)
                put(COLUMN_CUOTAS_PAGO, cantidadCuotas)

                put(
                    COLUMN_VALOR_CUOTA,
                    total / cantidadCuotas
                )

                put(COLUMN_ESTADO_PAGO, "REGISTRADO")
            }

            val pagoId = db.insert(
                TABLE_PAGOS,
                null,
                valoresPago
            )

            if (pagoId == -1L) {
                return -1L
            }

            // Confirmar todas las operaciones juntas
            db.setTransactionSuccessful()

            return facturaId

        } finally {

            db.endTransaction()
            db.close()
        }
    }

}
