package com.example.pruebamedicamento.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pruebamedicamento.dbhelper.DatabaseHelper;
import com.example.pruebamedicamento.fragment.FilterDialogFragment;
import com.example.pruebamedicamento.filterhelper.FilterHelper;
import com.example.pruebamedicamento.dbhelper.FirebaseSyncHelper;
import com.example.pruebamedicamento.R;
import com.example.pruebamedicamento.adapter.MedicamentoAdapter;
import com.example.pruebamedicamento.model.Franquicia;
import com.example.pruebamedicamento.model.PrecioMedicamento;
import com.example.pruebamedicamento.model.Sede;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, FilterDialogFragment.OnFilterAppliedListener {

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
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_precios_recycler, null);
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
        bottomNav.setSelectedItemId(R.id.menu_mapa);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_mapa) {
                return true;
            } else if (itemId == R.id.menu_recycler) {
                startActivity(new Intent(this, ListaFarmaciasActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_categoria) {
                startActivity(new Intent(this, CategoriasActivity.class));
                finish();
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
        // Obtener precios filtrados en lugar de todos los precios
        List<PrecioMedicamento> precios;
        if (filterHelper.hasActiveFilters()) {
            precios = dbHelper.getPreciosMedicamentosFiltered(filterHelper, sedeId);
        } else {
            precios = dbHelper.getPreciosMedicamentosPorSede(sedeId);
        }

        if (precios.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos que coincidan con los filtros en esta sede", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }

        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_precios_recycler, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvFranquiciaNombre = bottomSheetView.findViewById(R.id.tvFranquiciaNombre);
        TextView tvSedeNombre = bottomSheetView.findViewById(R.id.tvSedeNombre);
        ImageView ivLogo = bottomSheetView.findViewById(R.id.ivLogo);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerViewMedicamentos);

        Sede sede = dbHelper.getSedeById(sedeId);
        Franquicia franquicia = dbHelper.getFranquiciaById(sede.getFranquiciaId());

        tvFranquiciaNombre.setText(franquicia.getNombre());
        tvSedeNombre.setText(sede.getDireccion());

        // Cargar la imagen con Glide
        Glide.with(this)
                .load(franquicia.getImagenUrl()) // Si es una URL
                // .load(franquicia.getLogoResourceId()) // Si es un recurso (R.drawable.xxx)
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen mientras carga
                .error(R.drawable.ic_launcher_foreground) // Imagen si hay error
                .centerCrop() // Escala la imagen
                .into(ivLogo);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        MedicamentoAdapter adapter = new MedicamentoAdapter(precios, dbHelper);
        recyclerView.setAdapter(adapter);

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
        if (dbHelper != null) {
            dbHelper.close();
        }
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