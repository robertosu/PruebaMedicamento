package com.example.pruebamedicamento;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {
    private String searchQuery = "";
    private List<String> selectedTipos = new ArrayList<>();
    private List<String> selectedLaboratorios = new ArrayList<>();
    private List<String> selectedCiudades = new ArrayList<>();

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

    public void clearFilters() {
        searchQuery = "";
        selectedTipos.clear();
        selectedLaboratorios.clear();
        selectedCiudades.clear();
    }
}