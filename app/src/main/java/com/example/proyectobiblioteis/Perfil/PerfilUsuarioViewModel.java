package com.example.proyectobiblioteis.Perfil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectobiblioteis.API.models.BookLending;
import com.example.proyectobiblioteis.API.models.User;
import com.example.proyectobiblioteis.API.repository.BookLendingRepository;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;
import com.example.proyectobiblioteis.SessionManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class PerfilUsuarioViewModel extends ViewModel {

    private final MutableLiveData<User> usuario = new MutableLiveData<>();
    private final MutableLiveData<List<BookLending>> librosPrestados = new MutableLiveData<>();
    private final BookLendingRepository bookLendingRepository = new BookLendingRepository();
    private final ImageRepository imageRepository = new ImageRepository();

    public PerfilUsuarioViewModel() {
        cargarUsuario();
        obtenerLibrosPrestados();
    }

    private void cargarUsuario() {
        usuario.setValue(SessionManager.getInstance().getUser());
    }

    public LiveData<User> getUsuario() {
        return usuario;
    }

    public LiveData<List<BookLending>> getLibrosPrestados() {
        return librosPrestados;
    }

    public void obtenerLibrosPrestados() {
        bookLendingRepository.getAllLendings(new BookRepository.ApiCallback<List<BookLending>>() {
            @Override
            public void onSuccess(List<BookLending> lendings) {
                int userIdActual = SessionManager.getInstance().getUser().getId();
                List<BookLending> prestamosUsuario = new ArrayList<>();

                for (BookLending lending : lendings) {
                    if (lending.getUserId() == userIdActual) {
                        prestamosUsuario.add(lending);
                    }
                }
                librosPrestados.postValue(prestamosUsuario);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PerfilUsuarioViewModel", "Error obteniendo libros prestados", t);
            }
        });
    }

    public void cargarImagenPerfil(String imageName, BookRepository.ApiCallback<Bitmap> callback) {
        if (imageName == null || imageName.trim().isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                    callback.onSuccess(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onSuccess(null);
            }
        });
    }
}
