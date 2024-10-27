package com.example.pruebamedicamento;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "farmacias.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Consulta para obtener precios y detalles de medicamentos por sede
    @SuppressLint("Range")
    public List<PrecioMedicamento> getPreciosMedicamentosPorSede(long sedeId) {
        List<PrecioMedicamento> precios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT pm.*, m.marca, m.compuesto, s.direccion " +
                "FROM precio_medicamento pm " +
                "INNER JOIN medicamentos m ON pm.medicamento_id = m.id " +
                "INNER JOIN sedes s ON pm.sede_id = s.id " +
                "WHERE pm.sede_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sedeId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") PrecioMedicamento precio = new PrecioMedicamento(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getLong(cursor.getColumnIndex("medicamento_id")),
                        cursor.getLong(cursor.getColumnIndex("sede_id")),
                        cursor.getDouble(cursor.getColumnIndex("precio"))
                );

                // Establecer información adicional
                @SuppressLint("Range") String nombreMedicamento = cursor.getString(cursor.getColumnIndex("marca")) +
                        " (" + cursor.getString(cursor.getColumnIndex("compuesto")) + ")";
                precio.setNombreMedicamento(nombreMedicamento);
                precio.setDireccionSede(cursor.getString(cursor.getColumnIndex("direccion")));

                precios.add(precio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return precios;
    }

    // Obtener todas las sedes
    public List<Sede> getAllSedes() {
        List<Sede> sedes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM sedes";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Sede sede = new Sede(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("direccion")),
                        cursor.getDouble(cursor.getColumnIndex("latitud")),
                        cursor.getDouble(cursor.getColumnIndex("longitud")),
                        cursor.getLong(cursor.getColumnIndex("franquicia_id")),
                        cursor.getLong(cursor.getColumnIndex("ciudad_id"))
                );
                sedes.add(sede);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sedes;
    }
    private void createSedes(SQLiteDatabase db){
        db.execSQL("CREATE TABLE sedes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "direccion TEXT NOT NULL," +
                "latitud REAL NOT NULL," +
                "longitud REAL NOT NULL," +
                "franquicia_id INTEGER," +
                "ciudad_id INTEGER," +
                "FOREIGN KEY (franquicia_id) REFERENCES franquicias(id)," +
                "FOREIGN KEY (ciudad_id) REFERENCES ciudades(id))");
    }
    private void createPrecioMedicamento(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE precio_medicamento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "medicamento_id INTEGER NOT NULL," +
                "sede_id INTEGER NOT NULL," +
                "precio REAL NOT NULL," +
                "FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)," +
                "FOREIGN KEY (sede_id) REFERENCES sedes(id))");
    }
    private void createFranquicias(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE franquicias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "descripcion TEXT," +
                "imagen_url TEXT)");
    }
    private void createTiposMedicamento(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tipos_medicamento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "descripcion TEXT)");
    }
    private void createMedicamentos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE medicamentos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "marca TEXT NOT NULL," +
                "compuesto TEXT NOT NULL," +
                "descripcion TEXT," +
                "foto_url TEXT," +
                "laboratorio TEXT NOT NULL," +
                "tipo_id INTEGER NOT NULL," +
                "receta BOOLEAN NOT NULL," +
                "FOREIGN KEY (tipo_id) REFERENCES tipos_medicamento(id))");
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        createSedes(sqLiteDatabase);
//        createPrecioMedicamento(sqLiteDatabase);
//        createFranquicias(sqLiteDatabase);
//        createTiposMedicamento(sqLiteDatabase);
//        createMedicamentos(sqLiteDatabase);
//
//        insertarDatosSedes(sqLiteDatabase);
//        insertarDatosPrecioMedicamento(sqLiteDatabase);
//        insertarDatosMedicamentos(sqLiteDatabase);
//        insertarDatosFranquicias(sqLiteDatabase);
//        insertarDatosTiposMedicamento(sqLiteDatabase);
    }
    private void insertarDatosSedes(SQLiteDatabase db) {
        // Array de sentencias SQL para insertar datos de ejemplo
        String[] insertQueries = {
                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Libertador 1234', -34.5833, -58.4000, 1, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Calle Corrientes 5678', -34.6037, -58.3816, 1, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Santa Fe 910', -34.5953, -58.3889, 2, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. 9 de Julio 1122', -34.6037, -58.3811, 2, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Cabildo 3344', -34.5553, -58.4633, 3, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Córdoba 4455', -31.4167, -64.1833, 3, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Colón 5566', -31.4135, -64.1811, 4, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Vélez Sarsfield 6677', -31.4172, -64.1856, 4, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Rafael Núñez 7788', -32.9468, -60.6393, 5, 3)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Pellegrini 8899', -32.9571, -60.6435, 5, 3)"
        };

        // Ejecutar cada sentencia INSERT dentro de una transacción
        try {
            db.beginTransaction();

            for (String query : insertQueries) {
                db.execSQL(query);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    private void insertarDatosPrecioMedicamento(SQLiteDatabase db) {
        // Array de sentencias SQL para insertar datos de ejemplo
        String[] insertQueries = {
                // Precios para el medicamento 1 en diferentes sedes
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (1, 1, 1250.50)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (1, 2, 1280.00)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (1, 3, 1245.75)",

                // Precios para el medicamento 2 en diferentes sedes
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (2, 1, 850.25)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (2, 2, 875.50)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (2, 4, 860.00)",

                // Precios para el medicamento 3 en diferentes sedes
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (3, 1, 2100.00)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (3, 3, 2150.75)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (3, 5, 2080.25)",

                // Precios para el medicamento 4 en diferentes sedes
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (4, 2, 3450.50)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (4, 4, 3425.75)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (4, 6, 3400.00)",

                // Precios para el medicamento 5 en diferentes sedes
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (5, 1, 945.25)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (5, 3, 960.50)",
                "INSERT INTO precio_medicamento (medicamento_id, sede_id, precio) VALUES (5, 5, 955.75)"
        };

        // Ejecutar cada sentencia INSERT dentro de una transacción
        try {
            db.beginTransaction();

            for (String query : insertQueries) {
                db.execSQL(query);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    private void insertarDatosMedicamentos(SQLiteDatabase db) {
        String[] insertQueries = {
                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Ibupirac', 'Ibuprofeno', 'Analgésico y antiinflamatorio', 'ibupirac.jpg', 'Pfizer', 1, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Amoxidal', 'Amoxicilina', 'Antibiótico de amplio espectro', 'amoxidal.jpg', 'Roemmers', 2, 1)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Voltaren', 'Diclofenac', 'Antiinflamatorio potente', 'voltaren.jpg', 'Novartis', 3, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Allegra', 'Fexofenadina', 'Antihistamínico de última generación', 'allegra.jpg', 'Sanofi', 4, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Losacor', 'Losartán', 'Antihipertensivo efectivo', 'losacor.jpg', 'Roemmers', 5, 1)"
        };

        executeTransactionBatch(db, insertQueries);
    }
    private void insertarDatosTiposMedicamento(SQLiteDatabase db) {
        String[] insertQueries = {
                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Analgésico', 'Medicamentos para aliviar el dolor')",

                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Antibiótico', 'Medicamentos para combatir infecciones bacterianas')",

                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Antiinflamatorio', 'Medicamentos para reducir la inflamación')",

                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Antialérgico', 'Medicamentos para tratar alergias')",

                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Antihipertensivo', 'Medicamentos para la presión arterial')"
        };

        executeTransactionBatch(db, insertQueries);
    }
    private void insertarDatosFranquicias(SQLiteDatabase db) {
        String[] insertQueries = {
                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('Farmacia Central', 'Red líder de farmacias con más de 50 años de experiencia', 'franquicia_central.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('FarmaPlus', 'Cadena moderna de farmacias con servicio 24/7', 'farmaplus_logo.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('MediFarma', 'Farmacias especializadas en medicamentos de alta complejidad', 'medifarma.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('FarmaSalud', 'Tu farmacia de confianza en todo el país', 'farmasalud_icon.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('BioFarma', 'Especialistas en productos naturales y medicamentos', 'biofarma.jpg')"
        };

        executeTransactionBatch(db, insertQueries);
    }
    private void executeTransactionBatch(SQLiteDatabase db, String[] queries) {
        try {
            db.beginTransaction();
            for (String query : queries) {
                db.execSQL(query);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
        @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}