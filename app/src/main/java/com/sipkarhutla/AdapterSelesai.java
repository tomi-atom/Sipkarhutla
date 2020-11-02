package com.sipkarhutla;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sipkarhutla.response.SelesaiItem;
import com.squareup.picasso.Picasso;

import java.util.List;


class AdapterSelesai extends RecyclerView.Adapter<AdapterSelesai.MyViewHolder> {
    // Buat Global variable untuk manampung context
    Context context;
    List<SelesaiItem> laporan;
    public AdapterSelesai(Context context, List<SelesaiItem> data_laporan) {
        // Inisialisasi
        this.context = context;
        this.laporan = data_laporan;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Layout inflater
        View view = LayoutInflater.from(context).inflate(R.layout.laporan_item, parent, false);

        // Hubungkan dengan MyViewHolder
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvJudul.setText(laporan.get(position).getNama());
        holder.tvTglTerbit.setText(laporan.get(position).getTanggal());

        // Dapatkan url gambar

        final String urlGambarBerita = "http://sipkarhutla.com/laporan/" + laporan.get(position).getFoto();

        // Set image ke widget dengna menggunakan Library Piccasso
        // krena imagenya dari internet
        Picasso.get().load(urlGambarBerita).into(holder.ivGambarBerita);

        // Event klik ketika item list nya di klik
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mulai activity Detail
                Intent varIntent = new Intent(context, DetailActivity.class);
                // sisipkan data ke intent
                varIntent.putExtra("tanggal", laporan.get(position).getTanggal());
                varIntent.putExtra("nama", laporan.get(position).getNama());
                varIntent.putExtra("hp", laporan.get(position).getHp());
                varIntent.putExtra("keterangan", laporan.get(position).getKeterangan());
                varIntent.putExtra("foto", urlGambarBerita);
                varIntent.putExtra("lat", laporan.get(position).getLat());
                varIntent.putExtra("lng", laporan.get(position).getLng());
                varIntent.putExtra("status", laporan.get(position).getStatus());
                varIntent.putExtra("updateby", laporan.get(position).getUpdateby());

                // method startActivity cma bisa di pake di activity/fragment
                // jadi harus masuk ke context dulu
                context.startActivity(varIntent);
            }
        });
    }
    // Menentukan Jumlah item yang tampil
    @Override
    public int getItemCount() {
        return laporan.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Deklarasi widget
        ImageView ivGambarBerita;
        TextView tvJudul, tvTglTerbit, tvPenulis;

        public MyViewHolder(View itemView) {
            super(itemView);
            // inisialisasi widget
            ivGambarBerita = (ImageView) itemView.findViewById(R.id.ivPosterBerita);
            tvJudul = (TextView) itemView.findViewById(R.id.tvJudulBerita);
            tvTglTerbit = (TextView) itemView.findViewById(R.id.tvTglTerbit);
        }
    }
}
