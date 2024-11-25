package com.example.pruebamedicamento;

import android.annotation.SuppressLint;
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

    // Consulta para obtener precios y detalles de medicamentos por franquicia
    @SuppressLint("Range")
    public List<PrecioMedicamento> getPreciosMedicamentosPorSede(long sedeId) {
        List<PrecioMedicamento> precios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Primero obtenemos la franquicia_id de la sede
        Sede sede = getSedeById(sedeId);
        if (sede == null) return precios;

        String query = "SELECT pm.*, m.marca, m.compuesto, f.nombre as franquicia_nombre " +
                "FROM precio_medicamento pm " +
                "INNER JOIN medicamentos m ON pm.medicamento_id = m.id " +
                "INNER JOIN franquicias f ON pm.franquicia_id = f.id " +
                "WHERE pm.franquicia_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sede.getFranquiciaId())});

        if (cursor.moveToFirst()) {
            do {
                PrecioMedicamento precio = new PrecioMedicamento(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getLong(cursor.getColumnIndex("medicamento_id")),
                        sedeId, // Guardamos el sedeId original
                        cursor.getDouble(cursor.getColumnIndex("precio"))
                );

                String nombreMedicamento = cursor.getString(cursor.getColumnIndex("marca")) +
                        " (" + cursor.getString(cursor.getColumnIndex("compuesto")) + ")";
                precio.setNombreMedicamento(nombreMedicamento);
                precio.setNombreFranquicia(cursor.getString(cursor.getColumnIndex("franquicia_nombre")));

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
    @SuppressLint("Range")
    public Sede getSedeById(long sedeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Sede sede = null;

        Cursor cursor = db.query("sedes", null, "id = ?",
                new String[]{String.valueOf(sedeId)}, null, null, null);

        if (cursor.moveToFirst()) {
            sede = new Sede(
                    cursor.getLong(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("direccion")),
                    cursor.getDouble(cursor.getColumnIndex("latitud")),
                    cursor.getDouble(cursor.getColumnIndex("longitud")),
                    cursor.getLong(cursor.getColumnIndex("franquicia_id")),
                    cursor.getLong(cursor.getColumnIndex("ciudad_id"))
            );
        }
        cursor.close();
        return sede;
    }

    @SuppressLint("Range")
    public Franquicia getFranquiciaById(long franquiciaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Franquicia franquicia = null;

        Cursor cursor = db.query("franquicias", null, "id = ?",
                new String[]{String.valueOf(franquiciaId)}, null, null, null);

        if (cursor.moveToFirst()) {
            franquicia = new Franquicia(
                    cursor.getLong(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("descripcion")),
                    cursor.getString(cursor.getColumnIndex("imagen_url"))
            );
        }
        cursor.close();
        return franquicia;
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
                "franquicia_id INTEGER NOT NULL," + // Cambiado de sede_id a franquicia_id
                "precio REAL NOT NULL," +
                "FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)," +
                "FOREIGN KEY (franquicia_id) REFERENCES franquicias(id))"); // Nueva foreign key
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
        //initBd(sqLiteDatabase);
    }

    private void initBd(SQLiteDatabase sqLiteDatabase) {
        createSedes(sqLiteDatabase);
        createPrecioMedicamento(sqLiteDatabase);
        createFranquicias(sqLiteDatabase);
        createTiposMedicamento(sqLiteDatabase);
        createMedicamentos(sqLiteDatabase);
        createCiudades(sqLiteDatabase);

        insertarDatosSedes(sqLiteDatabase);
        insertarDatosPrecioMedicamento(sqLiteDatabase);
        insertarDatosMedicamentos(sqLiteDatabase);
        insertarDatosFranquicias(sqLiteDatabase);
        insertarDatosTiposMedicamento(sqLiteDatabase);
        insertarDatosCiudades(sqLiteDatabase);
    }

    private void insertarDatosSedes(SQLiteDatabase db) {
        // Array de sentencias SQL para insertar datos de ejemplo
        String[] insertQueries = {
                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Gregorio Cordovez 675', -29.9032, -71.2477, 2, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Balmaceda N°, 4168', -29.9388, -71.2621, 3, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Gabriela Mistral 3251, Local 1804', -29.9364, -71.2501, 2, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av Guillermo Ulriksen 3128, Local # 2', -29.94343100723389, -71.24180929545324, 1, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Domeyko N° 55, local 1', -29.96632416261157, -71.33256984927114, 1, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Avenida Varela 1524', -29.9577, -71.3367, 2, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Pedro Nolasco Videla 348', -29.96330165166066, -71.33577627201232, 4, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Av. Balmaceda 3463', -29.932964990411914, -71.26007742532717, 1, 1)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Diego Portales 331', -29.957061905709587, -71.33921970109203, 4, 2)",

                "INSERT INTO sedes (direccion, latitud, longitud, franquicia_id, ciudad_id) " +
                        "VALUES ('Esquina - José Santiago Aldunate, Garriga 1308', -29.95566309442654, -71.33888585427798, 2, 2)"
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
                // Ahora los precios se asocian con franquicias en lugar de sedes
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (1, 1, 1250.50)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (1, 2, 1280.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (1, 4, 1045.75)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (2, 1, 850.25)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (2, 3, 860.00)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (2, 4, 760.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (3, 1, 2100.00)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (3, 2, 2150.75)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (3, 4, 1880.25)",


                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (4, 2, 3425.75)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (4, 3, 3400.00)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (4, 4, 3200.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (5, 1, 945.25)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (5, 2, 960.50)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (5, 4, 855.75)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (6, 1, 500.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (6, 3, 510.25)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (6, 4, 480.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (7, 1, 300.25)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (7, 3, 305.75)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (7, 4, 290.75)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (8, 1, 1500.00)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (8, 2, 1525.50)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (8, 4, 1450.00)",


                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (9, 2, 1225.75)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (9, 3, 1190.00)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (9, 4, 1150.00)",

                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (10, 1, 950.25)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (10, 2, 970.50)",
                "INSERT INTO precio_medicamento (medicamento_id, franquicia_id, precio) VALUES (10, 3, 960.75)",

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
                        "VALUES ('Amoxidal', 'Amoxicilina', 'Antibiótico de amplio espectro', 'amoxidal.jpg', 'Laboratorio Chile', 2, 1)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Voltaren', 'Diclofenac', 'Antiinflamatorio potente', 'voltaren.jpg', 'Laboratorio Chile', 3, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Allegra', 'Fexofenadina', 'Antihistamínico de última generación', 'allegra.jpg', 'Pfizer', 4, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Losacor', 'Losartán', 'Antihipertensivo efectivo', 'losacor.jpg', 'Saval', 5, 1)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Aspirina', 'Ácido acetilsalicílico', 'Analgésico y anticoagulante', 'aspirina.jpg', 'Bayer', 1, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Kitadol', 'Paracetamol', 'Analgésico y antipirético', 'paracetamol.jpg', 'Laboratorio Chile', 1, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Ciproflex', 'Ciprofloxacino', 'Antibiótico para infecciones bacterianas', 'ciproflex.jpg', 'Saval', 2, 1)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Cetrizin', 'Cetirizina', 'Antihistamínico para alergias', 'cetirizin.jpg', 'Saval', 4, 0)",

                "INSERT INTO medicamentos (marca, compuesto, descripcion, foto_url, laboratorio, tipo_id, receta) " +
                        "VALUES ('Enpril', 'Enalapril', 'Antihipertensivo para la presión arterial', 'enpril.jpg', 'Andrómaco', 5, 1)"

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
                        "VALUES ('Antihistaminicos', 'Medicamentos para tratar alergias')",

                "INSERT INTO tipos_medicamento (nombre, descripcion) " +
                        "VALUES ('Antihipertensivo', 'Medicamentos para la presión arterial')"
        };

        executeTransactionBatch(db, insertQueries);
    }
    private void createCiudades(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ciudades (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL)");
    }

    private void insertarDatosCiudades(SQLiteDatabase db) {
        String[] insertQueries = {
                "INSERT INTO ciudades (nombre) VALUES ('La Serena')",

                "INSERT INTO ciudades (nombre) VALUES ('Coquimbo')",

                "INSERT INTO ciudades (nombre) VALUES ('Santiago')"

        };

        executeTransactionBatch(db, insertQueries);
    }

    private void insertarDatosFranquicias(SQLiteDatabase db) {
        String[] insertQueries = {
                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('Farmacias Ahumada ', 'Red líder de farmacias con más de 50 años de experiencia', 'ahumada.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('CruzVerde', 'Cadena moderna de farmacias con servicio 24/7', 'cruzverde.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('Salcobrand', 'Farmacias especializadas en medicamentos de alta complejidad', 'salcobrand.jpg')",

                "INSERT INTO franquicias (nombre, descripcion, imagen_url) " +
                        "VALUES ('Dr. Simi', 'Tu farmacia mas económica', 'simi.jpg')"
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


    @SuppressLint("Range")
    public List<String> getAllTiposMedicamento() {
        List<String> tipos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("tipos_medicamento",
                new String[]{"nombre"},
                null, null, null, null, "nombre ASC");

        if (cursor.moveToFirst()) {
            do {
                tipos.add(cursor.getString(cursor.getColumnIndex("nombre")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tipos;
    }
    @SuppressLint("Range")
    public Ciudad getCiudadById(long ciudadId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Ciudad ciudad = null;

        Cursor cursor = db.query("ciudades", null, "id = ?",
                new String[]{String.valueOf(ciudadId)}, null, null, null);

        if (cursor.moveToFirst()) {
            ciudad = new Ciudad(
                    cursor.getLong(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("nombre"))
            );
        }
        cursor.close();
        return ciudad;
    }

    @SuppressLint("Range")
    public List<String> getAllCiudades() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> ciudades = new ArrayList<>();

        Cursor cursor = db.query("ciudades", null, null,
                null, null, null, "nombre ASC"); // Ordenadas alfabéticamente por nombre

        if (cursor.moveToFirst()) {
            do {
                ciudades.add(cursor.getString(cursor.getColumnIndex("nombre")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ciudades;
    }


    @SuppressLint("Range")
    public List<String> getAllLaboratorios() {
        List<String> laboratorios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, "medicamentos",
                new String[]{"laboratorio"},
                null, null, "laboratorio", null, "laboratorio ASC", null);

        if (cursor.moveToFirst()) {
            do {
                laboratorios.add(cursor.getString(cursor.getColumnIndex("laboratorio")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return laboratorios;
    }

    @SuppressLint("Range")
    public List<PrecioMedicamento> getPreciosMedicamentosFiltered(FilterHelper filterHelper) {
        List<PrecioMedicamento> precios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // Construimos la consulta empezando desde franquicias
        query.append("SELECT DISTINCT s.id as sede_id, s.direccion, s.latitud, s.longitud, ");
        query.append("pm.id, pm.medicamento_id, pm.precio, ");
        query.append("m.marca, m.compuesto, m.laboratorio, ");
        query.append("tm.nombre as tipo, c.nombre as ciudad, f.nombre as franquicia ");
        query.append("FROM franquicias f ");
        query.append("INNER JOIN sedes s ON f.id = s.franquicia_id ");
        query.append("LEFT JOIN precio_medicamento pm ON f.id = pm.franquicia_id ");
        query.append("LEFT JOIN medicamentos m ON pm.medicamento_id = m.id ");
        query.append("LEFT JOIN tipos_medicamento tm ON m.tipo_id = tm.id ");
        query.append("INNER JOIN ciudades c ON s.ciudad_id = c.id ");

        List<String> whereConditions = new ArrayList<>();

        if (!filterHelper.getSearchQuery().isEmpty()) {
            whereConditions.add("(LOWER(m.marca) LIKE ? OR LOWER(m.compuesto) LIKE ?)");
            String searchPattern = "%" + filterHelper.getSearchQuery().toLowerCase() + "%";
            selectionArgs.add(searchPattern);
            selectionArgs.add(searchPattern);
        }

        if (!filterHelper.getSelectedTipos().isEmpty()) {
            whereConditions.add("tm.nombre IN (" + generatePlaceholders(filterHelper.getSelectedTipos().size()) + ")");
            selectionArgs.addAll(filterHelper.getSelectedTipos());
        }

        if (!filterHelper.getSelectedLaboratorios().isEmpty()) {
            whereConditions.add("m.laboratorio IN (" + generatePlaceholders(filterHelper.getSelectedLaboratorios().size()) + ")");
            selectionArgs.addAll(filterHelper.getSelectedLaboratorios());
        }

        if (!filterHelper.getSelectedCiudades().isEmpty()) {
            whereConditions.add("c.nombre IN (" + generatePlaceholders(filterHelper.getSelectedCiudades().size()) + ")");
            selectionArgs.addAll(filterHelper.getSelectedCiudades());
        }

        if (!whereConditions.isEmpty()) {
            query.append("WHERE ").append(String.join(" AND ", whereConditions));
        }

        query.append(" ORDER BY s.id ASC");

        Cursor cursor = db.rawQuery(query.toString(), selectionArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(cursor.getColumnIndex("precio"))) {
                    PrecioMedicamento precio = new PrecioMedicamento(
                            cursor.getLong(cursor.getColumnIndex("id")),
                            cursor.getLong(cursor.getColumnIndex("medicamento_id")),
                            cursor.getLong(cursor.getColumnIndex("sede_id")),
                            cursor.getDouble(cursor.getColumnIndex("precio"))
                    );

                    String nombreMedicamento = cursor.getString(cursor.getColumnIndex("marca")) +
                            " (" + cursor.getString(cursor.getColumnIndex("compuesto")) + ")";
                    precio.setNombreMedicamento(nombreMedicamento);
                    precio.setDireccionSede(cursor.getString(cursor.getColumnIndex("direccion")));
                    precio.setNombreFranquicia(cursor.getString(cursor.getColumnIndex("franquicia")));


                    precios.add(precio);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return precios;
    }

    private String generatePlaceholders(int count) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        return placeholders.toString();
    }
}