package com.example.pruebamedicamento;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el helper de la base de datos
        dbHelper = new DatabaseHelper(this);

        // Obtener el MapView e inicializarlo
        MapView mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Cargar los puntos desde la base de datos
        loadPointsFromDatabase();
    }

    private void loadPointsFromDatabase() {
        List<PointModel> points = dbHelper.getAllPoints();

        for (PointModel point : points) {
            LatLng position = new LatLng(point.getLatitude(), point.getLongitude());

            // Agregar marcador al mapa
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(point.getTitle()));
        }

        // Si hay puntos, centrar el mapa en el primer punto
        if (!points.isEmpty()) {
            PointModel firstPoint = points.get(0);
            LatLng position = new LatLng(firstPoint.getLatitude(), firstPoint.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
        }
    }

    // Métodos del ciclo de vida necesarios para MapView
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}