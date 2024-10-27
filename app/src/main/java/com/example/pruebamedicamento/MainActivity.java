package com.example.pruebamedicamento;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Inicializar MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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
            marker.setTag(sede.getId()); // Guardamos el ID de la sede en el marcador
        }

        // Si hay sedes, centrar el mapa en la primera
        if (!sedes.isEmpty()) {
            Sede firstSede = sedes.get(0);
            LatLng position = new LatLng(firstSede.getLatitud(), firstSede.getLongitud());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
        }
    }

    // Es necesario implementar los métodos del ciclo de vida para MapView
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

    private void showPreciosMedicamentos(long sedeId) {
        List<PrecioMedicamento> precios = dbHelper.getPreciosMedicamentosPorSede(sedeId);

        if (precios.isEmpty()) {
            Toast.makeText(this, "No hay precios disponibles para esta sede", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el diálogo con la información
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Precios de Medicamentos");

        // Crear el layout para mostrar los precios
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Agregar la dirección de la sede
        TextView tvDireccion = new TextView(this);
        tvDireccion.setText("Sede: " + precios.get(0).getDireccionSede());
        tvDireccion.setTypeface(null, Typeface.BOLD);
        layout.addView(tvDireccion);

        // Agregar un separador
        View separator = new View(this);
        separator.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        params.setMargins(0, 8, 0, 8);
        separator.setLayoutParams(params);
        layout.addView(separator);

        // Agregar cada medicamento y su precio
        for (PrecioMedicamento precio : precios) {
            TextView tvMedicamento = new TextView(this);
            tvMedicamento.setText(String.format("%s\nPrecio: $%.2f",
                    precio.getNombreMedicamento(), precio.getPrecio()));
            tvMedicamento.setPadding(0, 4, 0, 4);
            layout.addView(tvMedicamento);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }
}