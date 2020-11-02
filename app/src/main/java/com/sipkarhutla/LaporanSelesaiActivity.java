package com.sipkarhutla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sipkarhutla.network.ApiServices;
import com.sipkarhutla.network.InitRetrofit;
import com.sipkarhutla.response.SelesaiItem;
import com.sipkarhutla.response.ResponseSelesai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanSelesaiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_selesai);

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


        Call<ResponseSelesai> laporanCall = api.show_selesai();
        // Kirim request
        laporanCall.enqueue(new Callback<ResponseSelesai>() {
            @Override
            public void onResponse(Call<ResponseSelesai> call, Response<ResponseSelesai> response) {
                // Pasikan response Sukses
                if (response.isSuccessful()){
                    Log.d("response api", response.body().toString());
                    // tampung data response body ke variable
                    List<SelesaiItem> data = response.body().getSelesai();
                    boolean status = response.body().isStatus();
                    // Kalau response status nya = true
                    if (status){
                        // Buat Adapter untuk recycler view
                        AdapterSelesai adapter = new AdapterSelesai(LaporanSelesaiActivity.this, data);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // kalau tidak true
                        Toast.makeText(LaporanSelesaiActivity.this, "Tidak ada laporan untuk saat ini", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseSelesai> call, Throwable t) {
                // print ke log jika Error
                t.printStackTrace();
            }
        });
    }
}