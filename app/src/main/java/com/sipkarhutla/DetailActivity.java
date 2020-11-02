package com.sipkarhutla;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.sipkarhutla.base.MyApplication;
import com.sipkarhutla.base.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailActivity extends AppCompatActivity {

    String urlAddres = "http://sipkarhutla.com/inputproses.php";
    SharedPrefManager sharedPrefManager;
    EditText nama,no_hp,ket ;
    ImageView ivFoto;
    Button button,selesaiBtn;
    String nid = "";
    private static final String TAG = PelaporanActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        nama = findViewById(R.id.nama);
        no_hp = findViewById(R.id.nohp);
        ket = findViewById(R.id.ket);
        ivFoto = (ImageView) findViewById(R.id.profile_avatar);
        sharedPrefManager = new SharedPrefManager(this);
        button = findViewById(R.id.saveBtn);
        selesaiBtn = findViewById(R.id.selesaiBtn);
        showDetailPelaporan();
    }
    private void showDetailPelaporan() {
        // Tangkap data dari intent
        final String sid = getIntent().getStringExtra("id");
        nid = sid;
        final String snama = getIntent().getStringExtra("nama");
        String snohp = getIntent().getStringExtra("hp");
        String sket = getIntent().getStringExtra("keterangan");
        String sfoto = getIntent().getStringExtra("foto");
        String slat = getIntent().getStringExtra("lat");
        String slng = getIntent().getStringExtra("lng");
        String sstatus = getIntent().getStringExtra("status");
        String supdateby = getIntent().getStringExtra("updateby");
        double nstatus = Double.parseDouble(sstatus);
        // Untuk gambar berita
        Picasso.get().load(sfoto).into(ivFoto);

        nama.setText(snama);
        no_hp.setText(snohp);
        ket.setText(sket);

        if(nstatus==0){
            selesaiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }else {
            selesaiBtn.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(DetailActivity.this,OfflineMap.class);
                map.putExtra("lat",slat);
                map.putExtra("lng",slng);
                startActivity(map);
            }
        });
        selesaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Proses Laporan?")
                        .setContentText("Anda Yakin Proses Laporan Ini  !")
                        .setConfirmText("Ya,Proses!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                uploadData();
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
    private void uploadData() {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Upload Data...");
            progressDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlAddres, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    progressDialog.dismiss();
                    String resultResponse = new String(response.data);
                    try {
                        JSONArray jsonArray = new JSONArray(resultResponse);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");

                        if (code.equals("login_failed")) {

                            new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Terjadi Kesalahan..")
                                    .setContentText("Proses Laporan Gagal ! \n"+message)
                                    .show();
                            //Toast.makeText(DetailActivity.this,"Upload Data Gagal\n"+message,Toast.LENGTH_SHORT).show();

                        } else if(code.equals("berhasil")) {
                            new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Berhasil ")
                                    .setContentText("Laporan Telah diproses !")
                                    .setConfirmText("Ok!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();

                        }else {
                            new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Terjadi Kesalahan..")
                                    .setContentText("Proses Laporan Gagal ! \n"+message)
                                    .show();
                            // Toast.makeText(DetailActivity.this,"Terjadi Kesalahan\n"+message,Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON Error: " + e);
                        new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Terjadi Kesalahan..")
                                .setContentText("Upload Data Laporan Gagal ! 1")
                                .show();
                        //Toast.makeText(DetailActivity.this,"Jaringan Bermasalah \n Gagal Upload Data",Toast.LENGTH_SHORT).show();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.d(TAG, "Volley Error: " + error);
                    new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Terjadi Kesalahan..")
                            .setContentText("Proses Laporan Gagal ! 2"+error)
                            .show();

                    if (error instanceof NetworkError) {
                    } else if (error instanceof ServerError) {
                    } else if (error instanceof AuthFailureError) {
                    } else if (error instanceof ParseError) {
                    } else if (error instanceof NoConnectionError) {
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(DetailActivity.this,
                                "Oops. Timeout error!"+error,
                                Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(DetailActivity.this,"Jaringan Bermasalah \n Gagal Upload Data  Volley Error",Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("id",nid);
                    params.put("updateby",sharedPrefManager.getSpId());



                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();

                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(multipartRequest);



    }

}