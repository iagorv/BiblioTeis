package com.example.proyectobiblioteis;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder{


    TextView tvNombreLibro;
    TextView tvAutor;
    ImageView img;
    TextView tvFechaSalida;
    public CardViewHolder(View view) {
        super(view);
        tvNombreLibro = view.findViewById(R.id.tvNombreLibro);
        tvAutor = view.findViewById(R.id.tvAutor);
        img = view.findViewById(R.id.ivPhoto);
        tvFechaSalida=view.findViewById(R.id.tvFechaSalida);
    }


}
