package com.example.pruebamedicamento.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pruebamedicamento.dbhelper.DatabaseHelper;
import com.example.pruebamedicamento.model.Franquicia;
import com.example.pruebamedicamento.R;
import com.example.pruebamedicamento.model.Sede;

import java.util.List;

public class FarmaciaAdapter extends RecyclerView.Adapter<FarmaciaAdapter.FarmaciaViewHolder> {
    private Context context;
    private List<Sede> sedes;
    private DatabaseHelper dbHelper;

    public FarmaciaAdapter(Context context, List<Sede> sedes) {
        this.context = context;
        this.sedes = sedes;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public FarmaciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_farmacia, parent, false);
        return new FarmaciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FarmaciaViewHolder holder, int position) {
        Sede sede = sedes.get(position);
        Franquicia franquicia = dbHelper.getFranquiciaById(sede.getFranquiciaId());

        // Configurar los textos
        holder.tvNombreFranquicia.setText(franquicia.getNombre());
        holder.tvDireccion.setText(sede.getDireccion());

        // Cargar la imagen con Glide
        Glide.with(context)
                .load(franquicia.getImagenUrl()) // Asegúrate de que `getUrlImagen()` devuelva la URL de la imagen
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen mientras se carga
                .error(R.drawable.ic_launcher_foreground) // Imagen si hay un error
                .into(holder.imagenFranquicia);
    }
    @Override
    public int getItemCount() {
        return sedes.size();
    }

    class FarmaciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreFranquicia, tvDireccion;
        ImageView imagenFranquicia;

        FarmaciaViewHolder(View itemView) {
            super(itemView);
            tvNombreFranquicia = itemView.findViewById(R.id.tvNombreFranquicia);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            imagenFranquicia = itemView.findViewById(R.id.ivFranquicia);
        }
    }
}