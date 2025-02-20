package com.example.proyectobiblioteis.ListadosLibros;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobiblioteis.DetalleLibro;
import com.example.proyectobiblioteis.Perfil.PerfilUsuario;
import com.example.proyectobiblioteis.R;


public class ListadoLibros extends AppCompatActivity {

    public static final String LIBRO = "libro";
    private RecyclerView rvLibros;
    private ListadoLibrosViewModel listadoLibrosViewModel;

    private EditText etBuscarLibro;
    private MyAdapterLibro adapter;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros);

        rvLibros = findViewById(R.id.rvLibros);


        etBuscarLibro = findViewById(R.id.etBuscarLibro);
        btnBuscar = findViewById(R.id.btnBuscar);

        Toolbar tb = findViewById(R.id.toolbar);
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


                    Intent intent = new Intent(ListadoLibros.this, PerfilUsuario.class);
                    startActivity(intent);

                    return true;
                }


                return false;
            }
        });







        rvLibros.setLayoutManager(new LinearLayoutManager(this));
        listadoLibrosViewModel = new ViewModelProvider(this).get(ListadoLibrosViewModel.class);

        // Usar librosFiltrados en lugar de libros
        listadoLibrosViewModel.librosFiltrados.observe(this, libros -> {
            adapter =  new MyAdapterLibro(libros, libro -> {
                Intent intent = new Intent(ListadoLibros.this, DetalleLibro.class);
                intent.putExtra(LIBRO,libro);

                startActivity(intent);
            });
            rvLibros.setAdapter(adapter);
        });
        btnBuscar.setOnClickListener(view -> {
            String query = etBuscarLibro.getText().toString().trim();
            listadoLibrosViewModel.filtrarLibros(query);
        });


    }
}
