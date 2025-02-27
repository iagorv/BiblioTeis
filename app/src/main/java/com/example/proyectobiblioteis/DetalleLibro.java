package com.example.proyectobiblioteis;

import static com.example.proyectobiblioteis.ListadosLibros.ListadoLibros.LIBRO;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.view.MenuProvider;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.models.BookLending;
import com.example.proyectobiblioteis.API.models.BookLendingForm;
import com.example.proyectobiblioteis.API.repository.BookLendingRepository;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;
import com.example.proyectobiblioteis.ListadosLibros.ListadoLibros;
import com.example.proyectobiblioteis.Perfil.PerfilUsuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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
    private TextView tvfechaDevolucion;
    private int idLibro;

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
        tvfechaDevolucion = findViewById(R.id.tvfechaDevolucion);

        Toolbar tb = findViewById(R.id.toolbarDetalleLibro);
        setSupportActionBar(tb);


        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menudef, menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.Perfil) {


                    Intent intent = new Intent(DetalleLibro.this, PerfilUsuario.class);
                    startActivity(intent);

                    return true;
                }
                if (id == R.id.Listado) {


                    Intent intent = new Intent(DetalleLibro.this, ListadoLibros.class);
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
            tvFechaPublicacion.setText("Publicado el: " + libro.getPublishedDate());
            idLibro = libro.getId();

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


            btnPrestar.setOnClickListener(v -> {

               // prestarLibro(librolibro.getId());
               // prestarLibro2();
                 Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamara, 100);


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

                boolean tienePrestamoUsuario = false;
                boolean libroPrestadoOtroUsuario = false;
                String fechaDevolucion = "";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Ajusta el formato según el API


                for (BookLending lending : lendings) {
                    if (lending.getBookId() == libro.getId() && lending.getReturnDate() == null) {
                        if (lending.getUserId() == userIdActual) {
                            tienePrestamoUsuario = true;
                            currentLendingId = lending.getId();
                            break;
                        } else {
                            libroPrestadoOtroUsuario = true;
                            try {
                                Date lendDate = sdf.parse(lending.getLendDate()); // Convertir String a Date
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(lendDate);
                                calendar.add(Calendar.DAY_OF_MONTH, 15);
                                fechaDevolucion = sdf.format(calendar.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                actualizarBotones(tienePrestamoUsuario, libroPrestadoOtroUsuario, fechaDevolucion);

            }

            @Override
            public void onFailure(Throwable t) {
                actualizarBotones(false, false, "");
            }
        });
    }


    private void actualizarBotones(boolean tieneLibro, boolean libroPrestadoOtro, String fechaDevolucion) {
        if (tieneLibro) {
            btnPrestar.setVisibility(View.GONE);
            tvfechaDevolucion.setVisibility(View.GONE);
            btnDevolver.setVisibility(View.VISIBLE);
        } else if (libroPrestadoOtro) {
            btnPrestar.setVisibility(View.GONE);
            btnDevolver.setVisibility(View.GONE);
            tvfechaDevolucion.setVisibility(View.VISIBLE);
            // Mostrar la fecha de devolución estimada
            tvfechaDevolucion.setText("Disponible después del: " + fechaDevolucion);
        } else {
            btnPrestar.setVisibility(View.VISIBLE);
            btnDevolver.setVisibility(View.GONE);
            tvfechaDevolucion.setVisibility(View.GONE);
        }
    }


    private void devolverLibro(int lendingId) {
        bookLendingRepository.returnBook(lendingId, new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    Toast.makeText(DetalleLibro.this, "Libro devuelto exitosamente", Toast.LENGTH_SHORT).show();
                    actualizarBotones(false, false, "");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Simula que se escaneó el QR y procede con el préstamo del libro

            prestarLibro(idLibro);
        }
    }

    private void prestarLibro(int idLibro) {
        int userIdActual = SessionManager.getInstance().getUser().getId();

        BookLendingForm lendingForm = new BookLendingForm(userIdActual, idLibro);
        System.out.println("lending"+ lendingForm.getUserId()+" "+ lendingForm.getBookId());

        bookLendingRepository.lendBook(lendingForm, new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success) {
                    Toast.makeText(DetalleLibro.this, "Libro prestado exitosamente", Toast.LENGTH_SHORT).show();
                    actualizarBotones(true,false,null);

                } else {
                    Toast.makeText(DetalleLibro.this, "Error al prestar el libro", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetalleLibro.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prestarLibro2( ) {

        BookLendingForm lendingForm = new BookLendingForm(5, 3);
        System.out.println("lending"+ lendingForm.getUserId()+" "+ lendingForm.getBookId());

        bookLendingRepository.lendBook(lendingForm, new BookRepository.ApiCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean success) {
                if (success) {
                    Toast.makeText(DetalleLibro.this, "Libro prestado exitosamente", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DetalleLibro.this, "Error al prestar el libro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetalleLibro.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });


    }


}