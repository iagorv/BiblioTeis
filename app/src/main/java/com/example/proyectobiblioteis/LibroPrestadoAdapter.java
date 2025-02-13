package com.example.proyectobiblioteis;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.proyectobiblioteis.API.models.Book;
import com.example.proyectobiblioteis.API.models.BookLending;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LibroPrestadoAdapter extends RecyclerView.Adapter<LibroPrestadoAdapter.LibroViewHolder> {

    List<BookLending> librosPrestados; // Lista de libros prestados

    public LibroPrestadoAdapter(List<BookLending> librosPrestados) {
        this.librosPrestados = librosPrestados;
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.libro_prestado, parent, false);
        return new LibroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        BookLending libroPrestado = librosPrestados.get(position);
        Book libro = libroPrestado.getBook();
        Log.d("LibroPrestadoAdapter", "Libro prestado: " + libroPrestado.toString());

        holder.tvTituloLibro.setText(libro.getTitle());

        String lendDateStr = libroPrestado.getLendDate();
        String returnDateStr = calcularFechaDevolucion(lendDateStr); // LendDate + 15 días

        holder.tvFechaVencimiento.setText("Vence el: " + returnDateStr);
    }

    @Override
    public int getItemCount() {
        return librosPrestados.size(); // Número de libros prestados
    }

    public static class LibroViewHolder extends RecyclerView.ViewHolder {

        TextView tvTituloLibro, tvFechaVencimiento;

        public LibroViewHolder(View view) {
            super(view);
            tvTituloLibro = view.findViewById(R.id.tvTituloLibro);
            tvFechaVencimiento = view.findViewById(R.id.tvFechaVencimiento);
        }
    }
    private String calcularFechaDevolucion(String lendDateStr) {
        if (lendDateStr == null || lendDateStr.isEmpty()) {
            return "Fecha no disponible";
        }

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Formato de la API
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy"); // Formato deseado

        try {
            Date lendDate = formatoEntrada.parse(lendDateStr);

            // Sumar 15 días
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lendDate);
            calendar.add(Calendar.DAY_OF_YEAR, 15);

            return formatoSalida.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "Fecha inválida";
        }
    }
}