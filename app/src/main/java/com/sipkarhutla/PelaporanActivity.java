package com.sipkarhutla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sipkarhutla.base.MyApplication;
import com.sipkarhutla.base.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class PelaporanActivity extends AppCompatActivity {

    String urlAddres = "http://sipkarhutla.com/inputpelaporan.php";
    EditText nama, no_hp, ket;
    String Nama, No_hp, Ket;
    String Lat = "";
    String Lng = "";
    Button saveBtn;
    SharedPrefManager sharedPrefManager;
    AlertDialog.Builder builder;
    private Bitmap bitmap1;


    private static final String TAG = PelaporanActivity.class.getSimpleName();
    private ImageView mAvatar;
    private Uri mCropImageUri;
    private LinearLayout mContainer;

    private SessionManager mSessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelaporan);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        saveBtn = findViewById(R.id.saveBtn);
        nama = findViewById(R.id.nama);
        no_hp = findViewById(R.id.nohp);
        ket = findViewById(R.id.ket);

        //initiate shared preferences handler class
        mSessionManager = new SessionManager(this);

        mContainer = findViewById(R.id.container);
        mAvatar = findViewById(R.id.profile_avatar);
        ImageView cameraAction = findViewById(R.id.camera_action);

        //Check if avatar previously uploaded in preferences and load url
        if (!"hello".equalsIgnoreCase(mSessionManager.getUrl())) {
            //Picasso library to display images
            Picasso.get().load(mSessionManager.getUrl()).placeholder(R.drawable.photo).into(mAvatar);
        }
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            no_hp.setText(mPhoneNumber);
            return;
        } else {
        }

        cameraAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });


    }


    private void selectImage() {
        CropImage.startPickImageActivity(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    //Uses https://github.com/zetbaitsu/Compressor library to compress selected image
                    File file = new Compressor(this).compressToFile(new File(uri.getPath()));
                    bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

                    Picasso.get().load(file).into(mAvatar);
                    Toast.makeText(this, "Compressed", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed Compress", Toast.LENGTH_SHORT).show();
                    Picasso.get().load(uri).into(mAvatar);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //  uploadAvatar();
                    }
                }, 1000);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //TODO handle cropping error
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(false)
                .setActivityTitle("Crop Image")
                .setCropMenuCropButtonIcon(R.drawable.ic_check)
                .setAllowRotation(true)
                .setInitialCropWindowPaddingRatio(0)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(80)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setMultiTouchEnabled(true)
                .start(this);
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * Upload image selected using volley
     */

    private void uploadData() {
        Nama = nama.getText().toString();
        No_hp = no_hp.getText().toString();
        Ket = ket.getText().toString();
/*
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Toast.makeText(PelaporanActivity.this,
                                    "Lat : " + location.getLatitude() + " Long : " + location.getLongitude(),
                                    Toast.LENGTH_LONG).show();
                            Lat = String.valueOf(location.getLatitude());
                            Lng = String.valueOf(location.getLongitude());
                        }else
                            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("GPS Tidak Aktif..")
                                .setContentText("Tolong Aktifkan GPS pada perangkat anda! \n")
                                .show();
                    }
                });

*/
        if (Nama.equals("")  ||No_hp.equals("") || Ket.equals("")|| mAvatar == null )
        {
            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Data Belum Lengkap..")
                    .setContentText("Tolong Lengkapi Data !")
                    .show();
        }

        /*else if (Lat.equals("")  ||Lng.equals("")  )
        {
            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("GPS Tidak Aktif..")
                    .setContentText("Tolong Aktifkan GPS pada perangkat anda! 1")
                    .show();
        }*/
        else {

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

                            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Terjadi Kesalahan..")
                                    .setContentText("Upload Data Laporan Gagal ! \n"+message)
                                    .show();
                            //Toast.makeText(PelaporanActivity.this,"Upload Data Gagal\n"+message,Toast.LENGTH_SHORT).show();

                        } else if(code.equals("berhasil")) {
                            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Laporan Telah Diterima ")
                                    .setContentText("Laporan Akan Segera diproses !")
                                    .setConfirmText("Ok!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            Intent intent = new Intent(PelaporanActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();

                        }else {
                            new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Terjadi Kesalahan..")
                                    .setContentText("Upload Data Laporan Gagal ! \n"+message)
                                    .show();
                            // Toast.makeText(PelaporanActivity.this,"Terjadi Kesalahan\n"+message,Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON Error: " + e);
                        new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Terjadi Kesalahan..")
                                .setContentText("Upload Data Laporan Gagal ! 1")
                                .show();
                        //Toast.makeText(PelaporanActivity.this,"Jaringan Bermasalah \n Gagal Upload Data",Toast.LENGTH_SHORT).show();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.d(TAG, "Volley Error: " + error);
                    new SweetAlertDialog(PelaporanActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Terjadi Kesalahan..")
                            .setContentText("Upload Data Laporan Gagal ! 2"+error)
                            .show();

                    if (error instanceof NetworkError) {
                    } else if (error instanceof ServerError) {
                    } else if (error instanceof AuthFailureError) {
                    } else if (error instanceof ParseError) {
                    } else if (error instanceof NoConnectionError) {
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(PelaporanActivity.this,
                                "Oops. Timeout error!"+error,
                                Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(PelaporanActivity.this,"Jaringan Bermasalah \n Gagal Upload Data  Volley Error",Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    String foto = getStringImage(bitmap1);
                    params.put("nama",Nama);
                    params.put("no_hp",No_hp);
                    params.put("ket",Ket);
                    params.put("foto",foto);


                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                    if (mAvatar == null) {
                        Log.i(TAG, "Foto null");
                    }
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(multipartRequest);

        }

    }
}