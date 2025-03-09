package com.example.proyectobiblioteis.ListadosLibros;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.repository.BookRepository;
import com.example.proyectobiblioteis.API.repository.ImageRepository;
import com.example.proyectobiblioteis.R;

import java.util.List;

import okhttp3.ResponseBody;

public class MyAdapterLibro extends RecyclerView.Adapter<MyAdapterLibro.CardViewHolder> {

    List<Book> libros;
    private OnItemClickListener listener;

    public MyAdapterLibro(List<Book> libros, OnItemClickListener listener) {
        this.libros = libros;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Book libro);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        //Parte "normal del metodo si no hiciera falta imagen
        Book libro = libros.get(position);
        holder.tvNombreLibro.setText(libro.getTitle());
        holder.tvAutor.setText(libro.getAuthor());
        holder.tvFechaSalida.setText(libro.getPublishedDate());

        //Inicio imagen

        String imageName = libro.getBookPicture();

        if (imageName == null || imageName.trim().isEmpty()) {
            // Si no hay imagen, usa un placeholder
            holder.img.setImageResource(R.drawable.placeholder);
        } else {
            // Llamar al repositorio para obtener la imagen
            ImageRepository imageRepository = new ImageRepository();
            imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        // Convertir el ResponseBody en Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                        holder.img.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        holder.img.setImageResource(R.drawable.placeholder); // Si hay error, usa placeholder
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    holder.img.setImageResource(R.drawable.placeholder); // Si falla, usa placeholder
                }
            });
        }
        //Final Imagen


        holder.itemView.setOnClickListener(v -> listener.onItemClick(libro));
    }

    @Override
    public int getItemCount() {
        return libros.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreLibro;
        TextView tvAutor;
        ImageView img;
        TextView tvFechaSalida;

        public CardViewHolder(View view) {
            super(view);
            tvNombreLibro = view.findViewById(R.id.tvNombreLibro);
            tvAutor = view.findViewById(R.id.tvAutor);
            img = view.findViewById(R.id.ivPhoto);
            tvFechaSalida = view.findViewById(R.id.tvFechaSalida);
        }
    }
}

