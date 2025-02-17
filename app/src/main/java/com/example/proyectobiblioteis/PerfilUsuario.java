package com.example.proyectobiblioteis;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobiblioteis.API.models.BookLending;
import com.example.proyectobiblioteis.API.repository.BookLendingRepository;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class PerfilUsuario extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;
    private TextView tvFechaUsuario;
    private ImageView imFotoPerfil;
    private RecyclerView rvLibrosPrestados;
    private LibroPrestadoAdapter adapter;
    private List<BookLending> librosPrestados = new ArrayList<>();
    private BookLendingRepository bookLendingRepository;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario);
        tvFechaUsuario = findViewById(R.id.tvFechaUsuario);
        imFotoPerfil = findViewById(R.id.imFotoPerfil);
        rvLibrosPrestados = findViewById(R.id.rvLibrosPrestados);

        rvLibrosPrestados.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibroPrestadoAdapter(librosPrestados);
        rvLibrosPrestados.setAdapter(adapter);


        tvNombreUsuario.setText(SessionManager.getInstance().getUser().getName());
        tvCorreoUsuario.setText(SessionManager.getInstance().getUser().getEmail());
        tvFechaUsuario.setText(SessionManager.getInstance().getUser().getDateJoined());
        cargarImagenPerfil();

        obtenerLibrosPrestados();
    }

    private void cargarImagenPerfil() {
        String imageName = SessionManager.getInstance().getUser().getProfilePicture();

        if (imageName == null || imageName.trim().isEmpty()) {
            imFotoPerfil.setImageResource(R.drawable.placeholder); // Imagen por defecto
        } else {
            ImageRepository imageRepository = new ImageRepository();
            imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                        imFotoPerfil.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        imFotoPerfil.setImageResource(R.drawable.placeholder); // Si hay error, usar placeholder
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    imFotoPerfil.setImageResource(R.drawable.placeholder); // Si falla, usar placeholder
                }
            });
        }
    }
    private void obtenerLibrosPrestados() {
        bookLendingRepository = new BookLendingRepository();
        bookLendingRepository.getAllLendings(new BookRepository.ApiCallback<List<BookLending>>() {
            @Override
            public void onSuccess(List<BookLending> lendings) {
                librosPrestados.clear();

                int userIdActual = SessionManager.getInstance().getUser().getId();

                for (BookLending lending : lendings) {
                    if (lending.getUserId() == userIdActual) {
                        librosPrestados.add(lending);
                    }
                }

                adapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(Throwable t) {
                // Manejar error en caso de fallo
                Log.e("PerfilUsuario", "Error obteniendo libros prestados", t);
            }
        });
    }
}