package com.sipkarhutla.network;

import com.sipkarhutla.response.ResponseLaporan;
import com.sipkarhutla.response.ResponseSelesai;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by atom.
 */

public interface ApiServices {

    //@TIPEMETHOD("API_END_POINT")
    @GET("tampil_laporan.php")
    Call<ResponseLaporan> request_show_all_berita();
    @GET("laporan_selesai.php")
    Call<ResponseSelesai> show_selesai();



}
