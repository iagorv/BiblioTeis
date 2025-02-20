package com.example.proyectobiblioteis;

import static com.example.proyectobiblioteis.ListadosLibros.ListadoLibros.LIBRO;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.models.BookLending;
import com.example.proyectobiblioteis.API.repository.BookLendingRepository;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;
import com.example.proyectobiblioteis.Perfil.PerfilUsuario;

import java.util.List;

import okhttp3.ResponseBody;

public class DetalleLibro extends AppCompatActivity {
    private TextView tvAutor;
    private TextView tvNombreLibro;
    private TextView tvIsbn;
    private TextView tvFechaPublicacion;
    private ImageView ivLibro;
    private Button btnPrestar;
    private Button btnDevolver;
    private BookLendingRepository bookLendingRepository;
    private int currentLendingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_libro);


        tvNombreLibro = findViewById(R.id.tvNombreLibro);
        tvAutor = findViewById(R.id.tvAutor);
        tvIsbn = findViewById(R.id.tvIsbn);
        tvFechaPublicacion = findViewById(R.id.tvFechaPublicacion);
        ivLibro = findViewById(R.id.ivLibro);
        btnPrestar = findViewById(R.id.btnPrestar);

        btnDevolver = findViewById(R.id.btnDevolver);



        Toolbar tb = findViewById(R.id.toolbarDetalleLibro);
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


                    Intent intent = new Intent(DetalleLibro.this, PerfilUsuario.class);
                    startActivity(intent);

                    return true;
                }


                return false;
            }
        });



        bookLendingRepository = new BookLendingRepository();
        Intent intent = getIntent();
        Book libro = (Book) intent.getSerializableExtra(LIBRO);



        if (libro != null) {
            tvNombreLibro.setText(libro.getTitle());
            tvAutor.setText(libro.getAuthor());
            tvIsbn.setText("isbn: " + libro.getIsbn());
            tvFechaPublicacion.setText("Publicado el: "+libro.getPublishedDate());

            String imageName = libro.getBookPicture();
            if (imageName == null || imageName.trim().isEmpty()) {
                ivLibro.setImageResource(R.drawable.placeholder);
            } else {
                ImageRepository imageRepository = new ImageRepository();
                imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                            ivLibro.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ivLibro.setImageResource(R.drawable.placeholder);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ivLibro.setImageResource(R.drawable.placeholder);
                    }
                });
            }

            verificarEstadoPrestamo(libro);


            btnDevolver.setOnClickListener(v -> {
                if (currentLendingId != -1) {
                    devolverLibro(currentLendingId);
                } else {
                    Toast.makeText(DetalleLibro.this, "No se encontró préstamo asociado", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvAutor.setText("hola");
        }
    }



    private void verificarEstadoPrestamo(Book libro) {
        bookLendingRepository.getAllLendings(new BookRepository.ApiCallback<List<BookLending>>() {
            @Override
            public void onSuccess(List<BookLending> lendings) {
                int userIdActual = SessionManager.getInstance().getUser().getId();
                boolean tienePrestamoActivo = false;

                for (BookLending lending : lendings) {
                    if (lending.getUserId() == userIdActual
                            && lending.getBookId() == libro.getId()
                            && lending.getReturnDate() == null) {
                        tienePrestamoActivo = true;
                        currentLendingId = lending.getId();
                        break;
                    }
                }

                actualizarBotones(tienePrestamoActivo);
            }

            @Override
            public void onFailure(Throwable t) {
                actualizarBotones(false);
            }
        });
    }


    private void actualizarBotones(boolean tieneLibro) {
        if (tieneLibro) {
            btnPrestar.setVisibility(View.GONE);
            btnDevolver.setVisibility(View.VISIBLE);
        } else {
            btnPrestar.setVisibility(View.VISIBLE);
            btnDevolver.setVisibility(View.GONE);
        }
    }
    private void devolverLibro(int lendingId) {
        bookLendingRepository.returnBook(lendingId, new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    Toast.makeText(DetalleLibro.this, "Libro devuelto exitosamente", Toast.LENGTH_SHORT).show();
                    actualizarBotones(false);
                    currentLendingId = -1;
                } else {
                    Toast.makeText(DetalleLibro.this, "Error al devolver el libro (ID: " + lendingId + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetalleLibro.this, "Error al devolver el libro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}