package com.example.pruebamedicamento;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
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

        holder.tvNombreFranquicia.setText(franquicia.getNombre());
        holder.tvDireccion.setText(sede.getDireccion());
    }

    @Override
    public int getItemCount() {
        return sedes.size();
    }

    class FarmaciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreFranquicia, tvDireccion;

        FarmaciaViewHolder(View itemView) {
            super(itemView);
            tvNombreFranquicia = itemView.findViewById(R.id.tvNombreFranquicia);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
        }
    }
}