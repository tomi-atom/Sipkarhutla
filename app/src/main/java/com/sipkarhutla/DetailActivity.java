package com.sipkarhutla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {


    EditText nama,no_hp,ket ;
    ImageView ivFoto;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        nama = findViewById(R.id.nama);
        no_hp = findViewById(R.id.nohp);
        ket = findViewById(R.id.ket);
        ivFoto = (ImageView) findViewById(R.id.profile_avatar);

        button = findViewById(R.id.saveBtn);
        showDetailPelaporan();
    }
    private void showDetailPelaporan() {
        // Tangkap data dari intent
        final String snama = getIntent().getStringExtra("nama");
        String snohp = getIntent().getStringExtra("hp");
        String sket = getIntent().getStringExtra("keterangan");
        String sfoto = getIntent().getStringExtra("foto");
        String slat = getIntent().getStringExtra("lat");
        String slng = getIntent().getStringExtra("lng");

        // Untuk gambar berita
        Picasso.get().load(sfoto).into(ivFoto);

        nama.setText(snama);
        no_hp.setText(snohp);
        ket.setText(sket);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(DetailActivity.this,OfflineMap.class);
                map.putExtra("lat",slat);
                map.putExtra("lng",slng);
                startActivity(map);
            }
        });



    }

}