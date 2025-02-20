package com.example.proyectobiblioteis.PaginaInicio;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibrosViewModel;

import java.util.List;

public class PaginaInicioViewModel extends ViewModel {




    private MutableLiveData<List<Book>> ultimosLibros;
    private ListadoLibrosViewModel listadoLibrosViewModel;

    public PaginaInicioViewModel() {
        ultimosLibros = new MutableLiveData<>();
        listadoLibrosViewModel = new ListadoLibrosViewModel();

        // Cargar libros y obtener los dos últimos
        listadoLibrosViewModel.libros.observeForever(libros -> {
            if (libros.size() >= 2) {
                ultimosLibros.setValue(libros.subList(libros.size() - 2, libros.size()));
            } else {
                ultimosLibros.setValue(libros);
            }
        });
    }

    public LiveData<List<Book>> getUltimosLibros() {
        return ultimosLibros;
    }

}
