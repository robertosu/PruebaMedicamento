package com.example.pruebamedicamento.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamedicamento.R;
import com.example.pruebamedicamento.model.TipoMedicamento;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder> {
    private List<TipoMedicamento> categorias;
    private Context context;
    private OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(TipoMedicamento categoria);
    }

    public CategoriasAdapter(Context context, List<TipoMedicamento> categorias, OnCategoriaClickListener listener) {
        this.context = context;
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        TipoMedicamento categoria = categorias.get(position);
        holder.bind(categoria);
    }
    //devuelve la longitud de la lista
    @Override
    public int getItemCount() {
        return categorias.size();
    }

    class CategoriaViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategoria;
        private TextView tvNombreCategoria;
        private TextView tvDescripcionCategoria;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
            tvDescripcionCategoria = itemView.findViewById(R.id.tvDescripcionCategoria);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCategoriaClick(categorias.get(position));
                }
            });
        }

        public void bind(TipoMedicamento categoria) {
            tvNombreCategoria.setText(categoria.getNombre());
            tvDescripcionCategoria.setText(categoria.getDescripcion());
            // Aquí puedes cargar la imagen usando Glide o Picasso
            // Glide.with(context).load(categoria.getImagenUrl()).into(ivCategoria);
        }
    }
}