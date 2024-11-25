package com.example.pruebamedicamento.dbhelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class FirebaseSyncHelper {
    private final DatabaseHelper dbHelper;
    private final DatabaseReference firebaseDb;
    private static final String TAG = "FirebaseSyncHelper";

    public FirebaseSyncHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.firebaseDb = FirebaseDatabase.getInstance().getReference();
    }

    // Método principal para subir toda la base de datos SQLite a Firebase
    public void uploadAllDataToFirebase(final OnSyncCompleteListener listener) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Subir datos de cada tabla
            uploadTableToFirebase(db, "ciudades", listener);
            uploadTableToFirebase(db, "franquicias", listener);
            uploadTableToFirebase(db, "tipos_medicamento", listener);
            uploadTableToFirebase(db, "medicamentos", listener);
            uploadTableToFirebase(db, "sedes", listener);
            uploadTableToFirebase(db, "precio_medicamento", listener);

        } catch (Exception e) {
            if (listener != null) {
                listener.onSyncError("Error al subir datos: " + e.getMessage());
            }
        }
    }

    // Método para subir una tabla específica a Firebase
    private void uploadTableToFirebase(SQLiteDatabase db, String tableName, OnSyncCompleteListener listener) {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        List<Map<String, Object>> records = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> record = new HashMap<>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String columnName = cursor.getColumnName(i);
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                record.put(columnName, cursor.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                record.put(columnName, cursor.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                record.put(columnName, cursor.getString(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                record.put(columnName, null);
                                break;
                        }
                    }
                    records.add(record);
                } while (cursor.moveToNext());
            }

            // Subir los registros a Firebase
            DatabaseReference tableRef = firebaseDb.child(tableName);
            tableRef.setValue(records, (error, ref) -> {
                if (error != null) {
                    if (listener != null) {
                        listener.onSyncError("Error al subir " + tableName + ": " + error.getMessage());
                    }
                } else {
                    if (listener != null) {
                        listener.onTableSynced(tableName);
                    }
                }
            });

        } finally {
            cursor.close();
        }
    }

    // Método para descargar datos de Firebase a SQLite
    public void downloadDataFromFirebase(final OnSyncCompleteListener listener) {
        String[] tables = {"ciudades", "franquicias", "tipos_medicamento",
                "medicamentos", "sedes", "precio_medicamento"};

        for (String tableName : tables) {
            firebaseDb.child(tableName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    try {
                        db.beginTransaction();

                        // Limpiar tabla existente
                        db.delete(tableName, null, null);

                        // Insertar nuevos datos
                        for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                            ContentValues values = new ContentValues();
                            for (DataSnapshot fieldSnapshot : recordSnapshot.getChildren()) {
                                String key = fieldSnapshot.getKey();
                                Object value = fieldSnapshot.getValue();

                                if (value instanceof Long) {
                                    values.put(key, (Long) value);
                                } else if (value instanceof Double) {
                                    values.put(key, (Double) value);
                                } else if (value instanceof String) {
                                    values.put(key, (String) value);
                                } else if (value instanceof Boolean) {
                                    values.put(key, (Boolean) value ? 1 : 0);
                                }
                            }
                            db.insert(tableName, null, values);
                        }

                        db.setTransactionSuccessful();
                        if (listener != null) {
                            listener.onTableSynced(tableName);
                        }

                    } catch (Exception e) {
                        if (listener != null) {
                            listener.onSyncError("Error al sincronizar " + tableName + ": " + e.getMessage());
                        }
                    } finally {
                        db.endTransaction();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (listener != null) {
                        listener.onSyncError("Error en Firebase: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    // Interfaz para manejar callbacks de sincronización
    public interface OnSyncCompleteListener {
        void onTableSynced(String tableName);
        void onSyncError(String error);
    }
}