package com.example.proyectobiblioteis.Perfil;

import static com.example.proyectobiblioteis.MainActivity.EMAIL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.DetalleLibro;
import com.example.proyectobiblioteis.LibroPrestadoAdapter;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibrosViewModel;
import com.example.proyectobiblioteis.R;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PerfilUsuario extends AppCompatActivity {

    private PerfilUsuarioViewModel viewModel;
    private ImageView imFotoPerfil;
    private RecyclerView rvLibrosPrestados;
    private LibroPrestadoAdapter adapter;
    private ListadoLibrosViewModel listadoLibrosViewModel;
    public static final String LIBRO = "libro";
    private BookRepository bookRepository;
    private View selectedView;
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


        bookRepository = new BookRepository();


        Toolbar tb = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(tb);




        registerForContextMenu(imFotoPerfil);
        registerForContextMenu(tvNombreUsuario);

        imFotoPerfil.setOnLongClickListener(v->{
            selectedView = v; //variable con la que sabe que elemento estan manteniendo pulsado
            return false;
        });

        tvNombreUsuario.setOnLongClickListener(v->{
            selectedView = v;
            return false;
        });

        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menudef, menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.Listado) {


                    Intent intent = new Intent(PerfilUsuario.this, ListadoLibros.class);
                    startActivity(intent);

                    return true;
                }
                if (id == R.id.Camara) {

                    Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamara, 100);


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

        //Shared preferences guardadas en el login, para cogerlas debe tener el mismo nombre
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String email = sp.getString(EMAIL, "hola");
        Toast.makeText(this, email, Toast.LENGTH_LONG).show();


        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences spEncripted = EncryptedSharedPreferences.create(this, "EMCRYPTEDSHARE",
                    masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            String s=spEncripted.getString("contraseña",null);


            Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
                @Override
                public void onSuccess(List<Book> listaLibros) {
                    if (listaLibros != null) {

                        for (Book libro : listaLibros) {
                            if (libro.getId() == 2) {
                                // Crear Intent para DetalleLibro
                                Intent intent = new Intent(PerfilUsuario.this, DetalleLibro.class);
                                intent.putExtra(LIBRO, libro);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(PerfilUsuario.this, "Error al cargar libros", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.imFotoPerfil) {
            inflater.inflate(R.menu.menu_foto_perfil, menu); // Menú para la imagen
        } else if (v.getId() == R.id.tvNombreUsuario) {
            inflater.inflate(R.menu.menu_nombre_usuario, menu); // Menú para el texto
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedView == null) return super.onContextItemSelected(item);

        if (selectedView.getId() == R.id.tvNombreUsuario) {
            if (item.getItemId() == R.id.opcionNombreUsuario) {
                Toast.makeText(this, "Opcion del nombre de usuario", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else if (selectedView.getId() == R.id.imFotoPerfil) {
            if (item.getItemId() == R.id.opcionFotoPerfil) {
                Toast.makeText(this, "Opción de foto de usuario", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }
}
