package com.example.pruebamedicamento;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import java.util.List;

public class ListaFarmaciasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FarmaciaAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_farmacias);

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener datos y configurar adapter
        List<Sede> sedes = dbHelper.getAllSedes();
        adapter = new FarmaciaAdapter(this, sedes);
        recyclerView.setAdapter(adapter);

        // Configurar BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_recycler);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_inicio) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_recycler) {
                return true;
            } else if (itemId == R.id.menu_categoria) {
                // Implementar más tarde
                return true;
            }
            return false;
        });
    }
}