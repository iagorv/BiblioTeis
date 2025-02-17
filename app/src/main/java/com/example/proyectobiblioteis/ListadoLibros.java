package com.example.proyectobiblioteis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etBuscarLibro;
    private MyAdapterLibro adapter;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros);

        rvLibros = findViewById(R.id.rvLibros);
        BotonPerfil=findViewById(R.id.BotonPerfil);

        etBuscarLibro = findViewById(R.id.etBuscarLibro);
        btnBuscar = findViewById(R.id.btnBuscar);
        rvLibros.setLayoutManager(new LinearLayoutManager(this));
        listadoLibrosViewModel = new ViewModelProvider(this).get(ListadoLibrosViewModel.class);

        // Usar librosFiltrados en lugar de libros
        listadoLibrosViewModel.librosFiltrados.observe(this, libros -> {
            adapter =  new MyAdapterLibro(libros, libro -> {
                Intent intent = new Intent(ListadoLibros.this, DetalleLibro.class);
                intent.putExtra("libro",libro);

                startActivity(intent);
            });
            rvLibros.setAdapter(adapter);
        });
        btnBuscar.setOnClickListener(view -> {
            String query = etBuscarLibro.getText().toString().trim();
            listadoLibrosViewModel.filtrarLibros(query);
        });

        BotonPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(ListadoLibros.this, PerfilUsuario.class);
            startActivity(intent);
        });
    }
}
