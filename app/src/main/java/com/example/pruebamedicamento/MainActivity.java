package com.example.pruebamedicamento;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,FilterDialogFragment.OnFilterAppliedListener {

    private MapView mapView;
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private BottomSheetDialog bottomSheetDialog;
    private FilterHelper filterHelper;
    private FirebaseSyncHelper dbSyncHelper;
    private ProgressBar progressBar;
    private FloatingActionButton btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        filterHelper = new FilterHelper();
        //inicializar firebase
        dbSyncHelper = new FirebaseSyncHelper(dbHelper);


        // Inicializar MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inicializar BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_precios, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Agregar botón de filtro
        FloatingActionButton fabFilter = findViewById(R.id.fabFilter);
        fabFilter.setOnClickListener(v -> showFilterDialog());
        //
        // Botones para probar la sincronización y su progressbar
        progressBar = findViewById(R.id.progressBar);
        btnDownload = findViewById(R.id.btnDownload);
        findViewById(R.id.btnSync).setOnClickListener(v -> testSync());
        btnDownload.setOnClickListener(v -> downloadDataManually());

        // Configurar BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_inicio) {
                return true;
            } else if (itemId == R.id.menu_recycler) {
                startActivity(new Intent(this, ListaFarmaciasActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_categoria) {
                // Implementar más tarde
                return true;
            }
            return false;
        });
    }
    private void testSync() {
        dbSyncHelper.uploadAllDataToFirebase(new FirebaseSyncHelper.OnSyncCompleteListener() {

            //en operacion satisfactoria
            @Override
            public void onTableSynced(String tableName) {
                Log.d("Firebase", "Tabla sincronizada: " + tableName);
                Toast.makeText(MainActivity.this,
                        "Tabla sincronizada: " + tableName,
                        Toast.LENGTH_SHORT).show();
            }
            //en caso de error
            @Override
            public void onSyncError(String error) {
                Log.e("Firebase", "Error de sincronización: " + error);
                Toast.makeText(MainActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void downloadDataManually() {
        btnDownload.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        dbSyncHelper.downloadDataFromFirebase(new FirebaseSyncHelper.OnSyncCompleteListener() {
            //en operacion satisfactoria
            @Override
            public void onTableSynced(String tableName) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Tabla descargada: " + tableName,
                            Toast.LENGTH_SHORT).show();
                });

            }
            //en caso de error
            @Override
            public void onSyncError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Error: " + error,
                            Toast.LENGTH_LONG).show();
                    btnDownload.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
        progressBar.setVisibility(View.GONE);
        btnDownload.setEnabled(true);
    }
    private void showFilterDialog() {
        FilterDialogFragment filterDialog = FilterDialogFragment.newInstance();
        filterDialog.setFilterHelper(filterHelper);
        filterDialog.setOnFilterAppliedListener(this);
        filterDialog.show(getSupportFragmentManager(), "FilterDialog");
    }
    @Override
    public void onFilterApplied(FilterHelper filterHelper) {
        // Aquí actualizas tu vista con los resultados filtrados
        List<PrecioMedicamento> preciosFiltrados = dbHelper.getPreciosMedicamentosFiltered(filterHelper);
        // Actualizar tu vista con los resultados
        updateMapWithFilteredResults(preciosFiltrados);
    }
    private void updateMapWithFilteredResults(List<PrecioMedicamento> precios) {
        if (filterHelper.getSearchQuery().isEmpty() &&
                filterHelper.getSelectedTipos().isEmpty() &&
                filterHelper.getSelectedLaboratorios().isEmpty() &&
                filterHelper.getSelectedCiudades().isEmpty()) {
            // Si no hay filtros activos, mostrar todas las sedes
            loadSedes();
            return;
        }

        mMap.clear(); // Limpiar marcadores existentes

        // Crear un HashSet para almacenar las sedes únicas
        Set<Long> sedesAgregadas = new HashSet<>();

        for (PrecioMedicamento precio : precios) {
            long sedeId = precio.getSedeId();

            // Solo agregar la sede si no ha sido agregada antes
            if (!sedesAgregadas.contains(sedeId)) {
                Sede sede = dbHelper.getSedeById(sedeId);
                if (sede != null) {
                    LatLng position = new LatLng(sede.getLatitud(), sede.getLongitud());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(position)
                            .title(sede.getDireccion());

                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(sedeId);

                    sedesAgregadas.add(sedeId);
                }
            }
        }

        // Si hay resultados, hacer zoom al primer marcador
        if (!sedesAgregadas.isEmpty()) {
            long firstSedeId = precios.get(0).getSedeId();
            Sede firstSede = dbHelper.getSedeById(firstSedeId);
            if (firstSede != null) {
                LatLng position = new LatLng(firstSede.getLatitud(), firstSede.getLongitud());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadSedes();

        // Configurar el click listener para los marcadores
        mMap.setOnMarkerClickListener(marker -> {
            long sedeId = (long) marker.getTag();
            showPreciosMedicamentos(sedeId);
            return true;
        });
    }

    private void loadSedes() {
        List<Sede> sedes = dbHelper.getAllSedes();

        for (Sede sede : sedes) {
            LatLng position = new LatLng(sede.getLatitud(), sede.getLongitud());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(sede.getDireccion());

            Marker marker = mMap.addMarker(markerOptions);
            // Guardamos el id de la sede en el tag del marker
            marker.setTag(sede.getId());
        }

        if (!sedes.isEmpty()) {
            Sede firstSede = sedes.get(0);
            LatLng position = new LatLng(firstSede.getLatitud(), firstSede.getLongitud());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
        }
    }

    private void showPreciosMedicamentos(long sedeId) {
        List<PrecioMedicamento> precios = dbHelper.getPreciosMedicamentosPorSede(sedeId);

        if (precios.isEmpty()) {
            Toast.makeText(this, "No hay precios disponibles para esta sede", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asegurarnos de que el BottomSheet anterior se cierre y se limpie
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }

        // Crear un nuevo BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(this);

        // Inflar la vista
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_precios, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Obtener referencias a las vistas
        TextView tvFranquiciaNombre = bottomSheetView.findViewById(R.id.tvFranquiciaNombre);
        TextView tvSedeNombre = bottomSheetView.findViewById(R.id.tvSedeNombre);
        LinearLayout medicamentosContainer = bottomSheetView.findViewById(R.id.medicamentosContainer);

        // Obtener información de la sede y franquicia
        Sede sede = dbHelper.getSedeById(sedeId);
        Franquicia franquicia = dbHelper.getFranquiciaById(sede.getFranquiciaId());

        // Establecer la información de la franquicia y sede
        tvFranquiciaNombre.setText(franquicia.getNombre());
        tvSedeNombre.setText(sede.getDireccion());

        // Limpiar el container antes de agregar nuevos items
        medicamentosContainer.removeAllViews();

        // Agregar los precios de los medicamentos
        for (PrecioMedicamento precio : precios) {
            View itemView = getLayoutInflater().inflate(R.layout.item_medicamento, medicamentosContainer, false);
            TextView tvMedicamento = itemView.findViewById(R.id.tvMedicamento);
            TextView tvPrecio = itemView.findViewById(R.id.tvPrecio);

            tvMedicamento.setText(precio.getNombreMedicamento());
            tvPrecio.setText(String.format("$%.2f", precio.getPrecio()));

            medicamentosContainer.addView(itemView);
        }

        // Agregar listener para cuando se cierre el BottomSheet
        bottomSheetDialog.setOnDismissListener(dialog -> {
            // Limpiar recursos si es necesario
            medicamentosContainer.removeAllViews();
        });

        // Mostrar el BottomSheet
        bottomSheetDialog.show();
    }
//  Menu de navegacion


















    // Métodos del ciclo de vida para MapView
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}