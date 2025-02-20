package com.example.proyectobiblioteis.Perfil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.LibroPrestadoAdapter;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;
import com.example.proyectobiblioteis.R;

import java.util.ArrayList;

public class PerfilUsuario extends AppCompatActivity {

    private PerfilUsuarioViewModel viewModel;
    private ImageView imFotoPerfil;
    private RecyclerView rvLibrosPrestados;
    private LibroPrestadoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);

        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario);
        TextView tvFechaUsuario = findViewById(R.id.tvFechaUsuario);
        imFotoPerfil = findViewById(R.id.imFotoPerfil);
        rvLibrosPrestados = findViewById(R.id.rvLibrosPrestados);




        Toolbar tb = findViewById(R.id.toolbarPerfil);
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


                    Intent intent = new Intent(PerfilUsuario.this, ListadoLibros.class);
                    startActivity(intent);

                    return true;
                }


                return false;
            }
        });


        rvLibrosPrestados.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibroPrestadoAdapter(new ArrayList<>());
        rvLibrosPrestados.setAdapter(adapter);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(PerfilUsuarioViewModel.class);
        viewModel.getLibrosPrestados().observe(this, libros -> {
            adapter.actualizarLista(libros);
        });
        // Observar cambios en el usuario
        viewModel.getUsuario().observe(this, usuario -> {
            if (usuario != null) {
                tvNombreUsuario.setText(usuario.getName());
                tvCorreoUsuario.setText(usuario.getEmail());
                tvFechaUsuario.setText(usuario.getDateJoined());
                cargarImagenPerfil(usuario.getProfilePicture());
            }
        });

        viewModel.getLibrosPrestados().observe(this, libros -> {
            adapter.actualizarLista(libros);
        });
    }

    private void cargarImagenPerfil(String imageName) {
        viewModel.cargarImagenPerfil(imageName, new BookRepository.ApiCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (bitmap != null) {
                    imFotoPerfil.setImageBitmap(bitmap);
                } else {
                    imFotoPerfil.setImageResource(R.drawable.placeholder);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                imFotoPerfil.setImageResource(R.drawable.placeholder);
            }
        });
    }
}
