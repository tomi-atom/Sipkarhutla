package com.sipkarhutla;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sipkarhutla.response.LaporanItem;
import com.squareup.picasso.Picasso;

import java.util.List;


class AdapterSelesai extends RecyclerView.Adapter<AdapterSelesai.MyViewHolder> {
    // Buat Global variable untuk manampung context
    Context context;
    List<LaporanItem> laporan;
    public AdapterSelesai(Context context, List<LaporanItem> data_laporan) {
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
        // Set widget
        holder.tvJudul.setText(laporan.get(position).getNama());
        holder.tvTglTerbit.setText(laporan.get(position).getNik());

        // Dapatkan url gambar

        final String urlGambarBerita = "http://gep-pol-ru.com/FOTO/" + laporan.get(position).getFoto();
        final String urlGambarKtp = "http://gep-pol-ru.com/KTP/" + laporan.get(position).getKtp();

        // Set image ke widget dengna menggunakan Library Piccasso
        // krena imagenya dari internet
        Picasso.get().load(urlGambarBerita).into(holder.ivGambarBerita);

        // Event klik ketika item list nya di klik
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mulai activity Detail
                Intent varIntent = new Intent(context, MainActivity.class);
                // sisipkan data ke intent
                varIntent.putExtra("nama", laporan.get(position).getNama());
                varIntent.putExtra("alias", laporan.get(position).getAlias());
                varIntent.putExtra("nik", laporan.get(position).getNik());
                varIntent.putExtra("tempatlahir", laporan.get(position).getTempatlahir());
                varIntent.putExtra("tanggallahir", laporan.get(position).getTanggallahir());
                varIntent.putExtra("suku", laporan.get(position).getSuku());
                varIntent.putExtra("namaayah", laporan.get(position).getNamaayah());
                varIntent.putExtra("namaibu", laporan.get(position).getNamaibu());
                varIntent.putExtra("nohp", laporan.get(position).getNoHp());
                varIntent.putExtra("alamatktp", laporan.get(position).getAlamatktp());
                varIntent.putExtra("alamattinggal", laporan.get(position).getAlamattinggal());
                varIntent.putExtra("pekerjaan", laporan.get(position).getPekerjaan());
                varIntent.putExtra("ciriciri", laporan.get(position).getCiriciri());
                varIntent.putExtra("jk", laporan.get(position).getJk());
                varIntent.putExtra("agama", laporan.get(position).getAgama());
                varIntent.putExtra("pendidikanterakhir", laporan.get(position).getPendidikanterakhir());
                varIntent.putExtra("pembinaan", laporan.get(position).getPembinaan());
                varIntent.putExtra("foto", urlGambarBerita);
                varIntent.putExtra("ktp", urlGambarKtp);

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
