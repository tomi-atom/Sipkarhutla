package com.sipkarhutla.network;

import com.sipkarhutla.response.ResponseLaporan;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by atom.
 */

public interface ApiServices {

    //@TIPEMETHOD("API_END_POINT")
    @GET("tampil_masuk.php")
    Call<ResponseLaporan> request_show_all_berita();
    @GET("tampil_selesai.php")
    Call<ResponseLaporan> request_show_all_pengemis();



}
