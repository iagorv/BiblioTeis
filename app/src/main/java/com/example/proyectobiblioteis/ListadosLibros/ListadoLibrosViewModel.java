package com.example.proyectobiblioteis.ListadosLibros;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
public class ListadoLibrosViewModel extends ViewModel {

    MutableLiveData<List<Book>> libros;
    MutableLiveData<List<Book>> librosFiltrados;
    private BookRepository bookRepository;
    private List<Book> listaCompletaLibros = new ArrayList<>();


    public ListadoLibrosViewModel() {
        libros = new MutableLiveData<>();
        librosFiltrados = new MutableLiveData<>();
        libros.setValue(new ArrayList<>());
        librosFiltrados.setValue(new ArrayList<>());

        bookRepository = new BookRepository();
        cargarLibrosDesdeApi();
    }

    private void cargarLibrosDesdeApi() {
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> result) {
                listaCompletaLibros.clear();
                listaCompletaLibros.addAll(result);
                libros.setValue(result);
                librosFiltrados.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ListadoLibrosViewModel", "Error al cargar los libros", t);
            }
        });
    }
    public void filtrarLibros(String query) {
        if (query.isEmpty()) {
            librosFiltrados.setValue(listaCompletaLibros);
        } else {
            List<Book> filtrados = new ArrayList<>();
            for (Book libro : listaCompletaLibros) {
                if (libro.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filtrados.add(libro);
                }
            }
            librosFiltrados.setValue(filtrados);
        }
    }






}

