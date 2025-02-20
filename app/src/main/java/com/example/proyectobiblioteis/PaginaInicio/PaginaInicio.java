package com.example.proyectobiblioteis.PaginaInicio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.DetalleLibro;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibrosViewModel;
import com.example.proyectobiblioteis.ListadosLibros.MyAdapterLibro;
import com.example.proyectobiblioteis.Perfil.PerfilUsuario;
import com.example.proyectobiblioteis.R;

import java.util.List;

public class PaginaInicio extends AppCompatActivity {

    private RecyclerView rvUltimosLibros;
    private PaginaInicioViewModel paginaInicioViewModel;
    private MyAdapterLibro adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagina_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Toolbar tb = findViewById(R.id.toolbarInicio);
        setSupportActionBar(tb);


        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menudef,menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if(id == R.id.Perfil){


                    Intent intent = new Intent(PaginaInicio.this, PerfilUsuario.class);
                    startActivity(intent);

                    return true;
                }
                if(id == R.id.Listado){


                    Intent intent = new Intent(PaginaInicio.this, ListadoLibros.class);
                    startActivity(intent);

                    return true;
                }


                return false;
            }
        });




        rvUltimosLibros = findViewById(R.id.rvUltimosLibros);
        rvUltimosLibros.setLayoutManager(new LinearLayoutManager(this));

        paginaInicioViewModel = new ViewModelProvider(this).get(PaginaInicioViewModel.class);

        // Observamos los últimos libros
        paginaInicioViewModel.getUltimosLibros().observe(this, libros -> {
            if (libros.size() > 0) {
                adapter = new MyAdapterLibro(libros, libro -> {
                    Intent intent = new Intent(PaginaInicio.this, DetalleLibro.class);
                    intent.putExtra("libro", libro);
                    startActivity(intent);
                });
                rvUltimosLibros.setAdapter(adapter);
            }
        });







    }
}