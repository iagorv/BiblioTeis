package com.example.proyectobiblioteis;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectobiblioteis.API.models.User;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.UserRepository;

import java.util.List;

public class MainActivityVM extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> usuarioLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeLiveData = new MutableLiveData<>();

    public LoginViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<User> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public LiveData<String> getMensajeLiveData() {
        return mensajeLiveData;
    }

    public void iniciarSesion(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mensajeLiveData.setValue("Por favor, ingrese todos los campos.");
            return;
        }

        userRepository.getUsers(new BookRepository.ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for (User user : users) {
                    if (user.getEmail().equals(email) && user.getPasswordHash().equals(password)) {
                        SessionManager.getInstance().setUser(user);
                        usuarioLiveData.setValue(user);
                        mensajeLiveData.setValue("Inicio de sesión exitoso: " + user.getName());
                        return;
                    }
                }
                mensajeLiveData.setValue("Credenciales incorrectas");
            }

            @Override
            public void onFailure(Throwable t) {
                mensajeLiveData.setValue("Error al conectar con el servidor");
                Log.e("LoginViewModel", "Error al obtener usuarios", t);
            }
        });
    }
}