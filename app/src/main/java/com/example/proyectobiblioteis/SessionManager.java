package com.example.proyectobiblioteis;

import com.example.proyectobiblioteis.API.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User user;

    private SessionManager() {
        // Constructor privado para evitar múltiples instancias
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
//Llamar a este metodo cuando se haga el logout
    public void logout() {
        user = null;
    }
}
