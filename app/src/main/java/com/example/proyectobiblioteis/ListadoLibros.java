package com.example.proyectobiblioteis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ListadoLibros extends AppCompatActivity {

    private RecyclerView rvLibros;
    private ListadoLibrosViewModel listadoLibrosViewModel;
    private ImageButton BotonPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros);

        rvLibros = findViewById(R.id.rvLibros);
        BotonPerfil=findViewById(R.id.BotonPerfil);
        rvLibros.setLayoutManager(new LinearLayoutManager(this));

        listadoLibrosViewModel = new ViewModelProvider(this).get(ListadoLibrosViewModel.class);

        // Observamos los cambios en la lista de libros
        listadoLibrosViewModel.libros.observe(this, libros -> {
            MyAdapterLibro adapter = new MyAdapterLibro(libros);
            rvLibros.setAdapter(adapter);
        });

        BotonPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(ListadoLibros.this, PerfilUsuario.class);
            startActivity(intent);
        });
    }
}
