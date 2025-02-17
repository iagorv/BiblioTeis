package com.example.proyectobiblioteis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;

import okhttp3.ResponseBody;

public class DetalleLibro extends AppCompatActivity {
    private TextView tvAutor;
    private TextView tvNombreLibro;
    private TextView tvIsbn;
    private TextView tvFechaPublicacion;
    private ImageView ivLibro;

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
        Intent intent = getIntent();
        Book libro = (Book) intent.getSerializableExtra("libro");


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
        } else {
            tvAutor.setText("hola");
        }
    }
}