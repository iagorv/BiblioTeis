package com.example.proyectobiblioteis;


import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LibroPrestadoAdapter extends RecyclerView.Adapter<LibroPrestadoAdapter.LibroViewHolder> {

    List<BookLending> librosPrestados;


    public LibroPrestadoAdapter(List<BookLending> librosPrestados) {
        this.librosPrestados = new ArrayList<>(librosPrestados);
    }
    public void actualizarLista(List<BookLending> nuevaLista) {
        this.librosPrestados.clear();
        this.librosPrestados.addAll(nuevaLista);
        notifyDataSetChanged(); // Notifica cambios a la UI
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
        String returnDateStr = libroPrestado.getReturnDate();
        String fechaVencimiento = calcularFechaDevolucion(lendDateStr);


        if (returnDateStr != null) {
            holder.tvTituloLibro.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvFechaVencimiento.setText("Devuelto el: " + returnDateStr);
        } else {

            if (haVencido(fechaVencimiento)) {
                holder.tvTituloLibro.setTextColor(Color.RED);
            } else {
                holder.tvTituloLibro.setTextColor(Color.parseColor("#1E88E5"));
            }

            holder.tvFechaVencimiento.setText("Vence el: " + fechaVencimiento);
        }
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

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lendDate);

            calendar.add(Calendar.DAY_OF_YEAR, 15);

            return formatoSalida.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "Fecha inválida";
        }
    }
    private boolean haVencido(String fechaStr) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date fechaVencimiento = formato.parse(fechaStr);
            return fechaVencimiento != null && fechaVencimiento.before(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


}