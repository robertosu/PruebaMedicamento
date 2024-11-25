package com.example.pruebamedicamento.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamedicamento.dbhelper.DatabaseHelper;
import com.example.pruebamedicamento.R;
import com.example.pruebamedicamento.adapter.CategoriasAdapter;
import com.example.pruebamedicamento.model.TipoMedicamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CategoriasActivity extends AppCompatActivity implements CategoriasAdapter.OnCategoriaClickListener {
    private RecyclerView rvCategorias;
    private CategoriasAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        dbHelper = new DatabaseHelper(this);
        rvCategorias = findViewById(R.id.rvCategorias);
        setupRecyclerView();
        loadCategorias();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_mapa);
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
            } else return itemId == R.id.menu_categoria;
        });
    }

    private void setupRecyclerView() {
        rvCategorias.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas
        rvCategorias.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int space = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
                outRect.left = space;
                outRect.right = space;
                outRect.top = space;
                outRect.bottom = space;
            }
        });
    }

    private void loadCategorias() {
        List<TipoMedicamento> categorias = dbHelper.getAllCategorias(); // Necesitarás crear este método
        adapter = new CategoriasAdapter(this, categorias, this);
        rvCategorias.setAdapter(adapter);
    }
    //aqui enviamos un intent con el dato de la categoria seleccionada, usaremos esta categoria para recuperar los medicamentos
    @Override
    public void onCategoriaClick(TipoMedicamento categoria) {
        Intent intent = new Intent(this, MedicamentosActivity.class);
        intent.putExtra("categoria_id", categoria.getId());
        intent.putExtra("categoria_nombre", categoria.getNombre());
        startActivity(intent);
    }
}