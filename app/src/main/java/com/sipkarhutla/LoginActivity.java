package com.sipkarhutla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText Username,Password;
    String username,password;
    String login_url = "http://gep-pol-ru.com/login.php";
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.btnLogin);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);


        sharedPrefManager = new SharedPrefManager(this);

        if (sharedPrefManager.getSPSudahLogin()){
            startActivity(new Intent(LoginActivity.this, MenuActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = Username.getText().toString();
                password = Password.getText().toString();

                if (username.equals("") || password.equals(""))
                {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Username Atau Password Belum Lengkap")
                            .setContentText("Lengkapi Data Untuk Login !")
                            .show();

                }else {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        progressDialog.dismiss();
                                        Log.i("tagconvertstr", "["+response+"]");
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        if (code.equals("login_failed"))
                                        {
                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Username Atau Password Salah")
                                                    .setContentText("Login Gagal !")
                                                    .show();

                                        }else {
                                            Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                                            Bundle bundle = new Bundle();

                                            String username = jsonObject.getString("username");
                                            String id = jsonObject.getString("id");
                                            String nama = jsonObject.getString("nama");
                                            String jabatan = jsonObject.getString("jabatan");
                                            String instansi = jsonObject.getString("instansi");

                                            bundle.putString("username",username);
                                            bundle.putString("id",id);
                                            bundle.putString("nama",nama);
                                            bundle.putString("jabatan",jabatan);
                                            bundle.putString("instansi",instansi);
                                            intent.putExtras(bundle);
                                            sharedPrefManager.saveSPString(SharedPrefManager.SP_USERNAME, username);
                                            sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, nama);
                                            sharedPrefManager.saveSPString(SharedPrefManager.SP_ID, id);
                                            sharedPrefManager.saveSPString(SharedPrefManager.SP_JABATAN, jabatan);
                                            sharedPrefManager.saveSPString(SharedPrefManager.SP_ISNTANSI, instansi);
                                            // Shared Pref ini berfungsi untuk menjadi trigger session login
                                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);
                                            startActivity(intent);

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Tidak Tersedia !")
                                    .setContentText(error.getMessage())
                                    .show();
                            error.printStackTrace();

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("username",username);
                            params.put("password",password);
                            return params;
                        }
                    };

                    MySingleton.getInstance(LoginActivity.this).addToRequestque(stringRequest);
                }
            }
        });

    }

}