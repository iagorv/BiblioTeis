package com.example.proyectobiblioteis;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
public class ListadoLibrosViewModel extends ViewModel {

    MutableLiveData<List<Book>> libros;
    private BookRepository bookRepository;

    public ListadoLibrosViewModel() {
        this.libros = new MutableLiveData<>();
        this.libros.setValue(new ArrayList<>());

        bookRepository = new BookRepository();
        cargarLibrosDesdeApi();
    }

    private void cargarLibrosDesdeApi() {
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> result) {
                libros.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ListadoLibrosViewModel", "Error al cargar los libros", t);
            }
        });
    }
}

