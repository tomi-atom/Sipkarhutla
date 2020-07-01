package com.sipkarhutla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MenuActivity extends AppCompatActivity {

    TextView welcome;
    ImageButton LaporanMasuk, LaporanSelesai,Info,Logout;
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        sharedPrefManager = new SharedPrefManager(this);
        welcome = findViewById(R.id.welcome);


        LaporanMasuk = findViewById(R.id.laporanmasuk);
        LaporanSelesai = findViewById(R.id.laporanselesai);
        Info = findViewById(R.id.info);
        Logout = findViewById(R.id.logout);

        welcome.setText("Selamat Datang : "+sharedPrefManager.getSPNama());

        LaporanMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,LaporanMasukActivity.class);
                startActivity(i);
            }
        });
        LaporanSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,LaporanSelesaiActivity.class);
                startActivity(i);
            }
        });
        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,LaporanMasukActivity.class);
                startActivity(i);
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MenuActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Keluar?")
                        .setContentText("Anda Yakin Keluar dari Aplikasi !")
                        .setConfirmText("Ya,Keluar!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                                startActivity(new Intent(MenuActivity.this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        })
                        .setCancelButton("Kembali", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });
    }
}