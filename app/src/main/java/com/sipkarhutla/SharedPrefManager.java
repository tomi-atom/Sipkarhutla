package com.sipkarhutla;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String SP_MAHASISWA_APP = "spMahasiswaApp";

    public static final String SP_PIN = "pin";
    public static final String SP_ID_UPT = "id_upt";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    public static final String SP_USERNAME = "username";
    public static final String SP_PASSWORD = "password";

    public static final String SP_NAMA = "spNama";
    public static final String SP_JABATAN = "spJabatan";
    public static final String SP_ISNTANSI = "spInstansi";
    public static final String SP_ID = "spId";


    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_MAHASISWA_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }

    public String getSpJabatan(){
        return sp.getString(SP_JABATAN, "");
    }
    public String getSpIsntansi(){
        return sp.getString(SP_ISNTANSI, "");
    }
    public String getSpId(){
        return sp.getString(SP_ISNTANSI, "");
    }


    public boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
