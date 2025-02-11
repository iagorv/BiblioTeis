package com.example.proyectobiblioteis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilUsuario extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;
    private TextView tvFechaUsuario;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);


        tvCorreoUsuario=findViewById(R.id.tvCorreoUsuario);

        tvCorreoUsuario.setText(SessionManager.getInstance().getUser().getEmail());
        

    }
}