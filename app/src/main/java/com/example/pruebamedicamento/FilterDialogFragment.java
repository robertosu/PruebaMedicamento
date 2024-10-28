package com.example.pruebamedicamento;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {
    private FilterHelper filterHelper;
    private DatabaseHelper dbHelper;
    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(FilterHelper filterHelper);
    }

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    public void setFilterHelper(FilterHelper filterHelper) {
        this.filterHelper = filterHelper;
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter, null);

        dbHelper = new DatabaseHelper(requireContext());

        // Configurar SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQuery(filterHelper.getSearchQuery(), false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHelper.setSearchQuery(newText);
                return true;
            }
        });

        // Configurar los contenedores de filtros
        setupFilterSection(view, R.id.tiposContainer, dbHelper.getAllTiposMedicamento(),
                filterHelper.getSelectedTipos(), "Tipos de Medicamento");
        setupFilterSection(view, R.id.laboratoriosContainer, dbHelper.getAllLaboratorios(),
                filterHelper.getSelectedLaboratorios(), "Laboratorios");
        setupFilterSection(view, R.id.ciudadesContainer, dbHelper.getAllCiudades(),
                filterHelper.getSelectedCiudades(), "Ciudades");

        // Configurar botones
        Button btnAplicar = view.findViewById(R.id.btnAplicar);
        Button btnLimpiar = view.findViewById(R.id.btnLimpiar);

        btnAplicar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(filterHelper);
            }
            dismiss();
        });

        btnLimpiar.setOnClickListener(v -> {
            filterHelper.clearFilters();
            searchView.setQuery(null, false);
            if (listener != null) {
                listener.onFilterApplied(filterHelper);
            }
            dismiss();
        });

        builder.setView(view)
                .setTitle("Filtrar Medicamentos");

        return builder.create();
    }

    private void setupFilterSection(View parentView, int containerId, List<String> items,
                                    List<String> selectedItems, String title) {
        LinearLayout container = parentView.findViewById(containerId);

        // Agregar título de la sección
        android.widget.TextView titleView = new android.widget.TextView(requireContext());
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setPadding(0, 16, 0, 8);
        container.addView(titleView);

        // Agregar checkboxes
        for (String item : items) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(item);
            checkBox.setChecked(selectedItems.contains(item));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item);
                    }
                } else {
                    selectedItems.remove(item);
                }
            });
            container.addView(checkBox);
        }
    }
}