package com.example.pruebamedicamento.filterhelper;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {
    private String searchQuery = "";
    private List<String> selectedTipos = new ArrayList<>();
    private List<String> selectedLaboratorios = new ArrayList<>();
    private List<String> selectedCiudades = new ArrayList<>();
    private Long selectedSedeId = null; // Nuevo campo para filtrar por sede

    public String getSearchQuery() {
        return searchQuery.toLowerCase();
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<String> getSelectedTipos() {
        return selectedTipos;
    }

    public List<String> getSelectedLaboratorios() {
        return selectedLaboratorios;
    }

    public List<String> getSelectedCiudades() {
        return selectedCiudades;
    }

    public Long getSelectedSedeId() {
        return selectedSedeId;
    }

    public void setSelectedSedeId(Long sedeId) {
        this.selectedSedeId = sedeId;
    }

    public void clearFilters() {
        searchQuery = "";
        selectedTipos.clear();
        selectedLaboratorios.clear();
        selectedCiudades.clear();
        selectedSedeId = null;
    }

    public boolean hasActiveFilters() {
        return !searchQuery.isEmpty() ||
                !selectedTipos.isEmpty() ||
                !selectedLaboratorios.isEmpty() ||
                !selectedCiudades.isEmpty();
    }
}