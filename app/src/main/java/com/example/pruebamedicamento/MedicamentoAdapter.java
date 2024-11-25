package com.example.pruebamedicamento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {
    private List<PrecioMedicamento> medicamentos;

    public MedicamentoAdapter(List<PrecioMedicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicamento_card, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        PrecioMedicamento medicamento = medicamentos.get(position);
        holder.tvMedicamento.setText(medicamento.getNombreMedicamento());
        holder.tvPrecio.setText(String.format("$%.2f", medicamento.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicamento;
        TextView tvPrecio;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicamento = itemView.findViewById(R.id.tvMedicamento);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}