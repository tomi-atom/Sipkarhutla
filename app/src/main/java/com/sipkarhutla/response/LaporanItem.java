package com.sipkarhutla.response;

import com.google.gson.annotations.SerializedName;

public class LaporanItem{

	@SerializedName("keterangan")
	private String keterangan;

	@SerializedName("nama")
	private String nama;

	@SerializedName("foto")
	private String foto;

	@SerializedName("hp")
	private String hp;

	@SerializedName("id")
	private String id;

	@SerializedName("tanggal")
	private String tanggal;

	public void setKeterangan(String keterangan){
		this.keterangan = keterangan;
	}

	public String getKeterangan(){
		return keterangan;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setFoto(String foto){
		this.foto = foto;
	}

	public String getFoto(){
		return foto;
	}

	public void setHp(String hp){
		this.hp = hp;
	}

	public String getHp(){
		return hp;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTanggal(String tanggal){
		this.tanggal = tanggal;
	}

	public String getTanggal(){
		return tanggal;
	}
}