package com.example.pruebamedicamento.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamedicamento.dbhelper.DatabaseHelper;
import com.example.pruebamedicamento.R;
import com.example.pruebamedicamento.adapter.MedicamentoAdapter;
import com.example.pruebamedicamento.model.PrecioMedicamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MedicamentosActivity extends AppCompatActivity {
    private RecyclerView rvMedicamentos;
    private MedicamentoAdapter adapter;
    private DatabaseHelper dbHelper;
    private String categoriaNombre;
    private long categoriaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        // Obtener datos de la categoría seleccionada
        categoriaId = getIntent().getLongExtra("categoria_id", -1);
        categoriaNombre = getIntent().getStringExtra("categoria_nombre");

        // Configurar título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoriaNombre);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        rvMedicamentos = findViewById(R.id.rvMedicamentos);
        setupRecyclerView();
        loadMedicamentos();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_categoria);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_mapa) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
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


    private void setupRecyclerView() {
        rvMedicamentos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadMedicamentos() {
        List<PrecioMedicamento> medicamentos = dbHelper.getPreciosMedicamentoPorCategoria(categoriaNombre);
        adapter = new MedicamentoAdapter(medicamentos, dbHelper);
        rvMedicamentos.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}