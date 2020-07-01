package com.sipkarhutla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sipkarhutla.network.ApiServices;
import com.sipkarhutla.network.InitRetrofit;
import com.sipkarhutla.response.LaporanItem;
import com.sipkarhutla.response.ResponseLaporan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanMasukActivity extends AppCompatActivity {

    // Deklarasi Widget
    private RecyclerView recyclerView;
    AdapterMasuk adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk);
        // Inisialisasi Widget
        recyclerView = (RecyclerView) findViewById(R.id.rvListData);
        // RecyclerView harus pakai Layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // Eksekusi method
        tampilData();
    }

    private void tampilData() {
        ApiServices api = InitRetrofit.getInstance();
        // Siapkan request

        Map<String, String> data = new HashMap<>();


        Call<ResponseLaporan> laporanCall = api.request_show_all_berita();
        // Kirim request
        laporanCall.enqueue(new Callback<ResponseLaporan>() {
            @Override
            public void onResponse(Call<ResponseLaporan> call, Response<ResponseLaporan> response) {
                // Pasikan response Sukses
                if (response.isSuccessful()){
                    Log.d("response api", response.body().toString());
                    // tampung data response body ke variable
                    List<LaporanItem> data_pengemis = response.body().getLaporan();
                    boolean status = response.body().isStatus();
                    // Kalau response status nya = true
                    if (status){
                        // Buat Adapter untuk recycler view
                        AdapterMasuk adapter = new AdapterMasuk(LaporanMasukActivity.this, data_pengemis);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // kalau tidak true
                        Toast.makeText(LaporanMasukActivity.this, "Tidak ada laporan untuk saat ini", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLaporan> call, Throwable t) {
                // print ke log jika Error
                t.printStackTrace();
            }
        });
    }
}