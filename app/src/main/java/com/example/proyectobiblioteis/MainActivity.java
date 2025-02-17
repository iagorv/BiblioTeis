package com.example.proyectobiblioteis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectobiblioteis.API.models.User;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.UserRepository;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView mensajeInicio;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        mensajeInicio = findViewById(R.id.mensajeInicio);
        userRepository = new UserRepository();

        loginButton.setOnClickListener(view -> iniciarSesion());

    }

    private void iniciarSesion() {
        String emailIngresado = emailEditText.getText().toString().trim();
        String passwordIngresado = passwordEditText.getText().toString().trim();

        if (emailIngresado.isEmpty() || passwordIngresado.isEmpty()) {
            mensajeInicio.setText("Por favor, ingrese todos los campos.");
            return;
        }

        userRepository.getUsers(new BookRepository.ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                boolean usuarioEncontrado = false;

                for (User user : users) {
                    if (user.getEmail().equals(emailIngresado) && user.getPasswordHash().equals(passwordIngresado)) {
                        usuarioEncontrado = true;
                        SessionManager.getInstance().setUser(user);
                        break;
                    }
                }

                if (usuarioEncontrado) {

                    mensajeInicio.setText("Inicio de sesión exitoso "+ SessionManager.getInstance().getUser().getName());
                    mensajeInicio.setTextColor(Color.GREEN);
                    Intent intent = new Intent(MainActivity.this, ListadoLibros.class);
                    startActivity(intent);

                } else {
                    mensajeInicio.setText("Credenciales incorrectas");
                    mensajeInicio.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mensajeInicio.setText("Error al conectar con el servidor");
                mensajeInicio.setTextColor(Color.RED);
                Log.e("MainActivity", "Error al obtener usuarios", t);
            }
        });
    }
}