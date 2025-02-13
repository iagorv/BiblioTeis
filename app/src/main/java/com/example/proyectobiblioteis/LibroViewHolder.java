package com.example.proyectobiblioteis;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibroViewHolder extends RecyclerView.ViewHolder {

    TextView tvTituloLibro, tvFechaVencimiento;


    public LibroViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTituloLibro = itemView.findViewById(R.id.tvTituloLibro);

        tvFechaVencimiento = itemView.findViewById(R.id.tvFechaVencimiento);

    }
}

