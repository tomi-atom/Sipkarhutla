package com.sipkarhutla.response;

import com.google.gson.annotations.SerializedName;

public class LaporanItem {

	@SerializedName("jk")
	private String jk;

	@SerializedName("alamattinggal")
	private String alamattinggal;

	@SerializedName("namaayah")
	private String namaayah;

	@SerializedName("no_hp")
	private String noHp;

	@SerializedName("pendidikanterakhir")
	private String pendidikanterakhir;

	@SerializedName("ktp")
	private String ktp;

	@SerializedName("tgl_create")
	private String tglCreate;

	@SerializedName("agama")
	private String agama;

	@SerializedName("alamatktp")
	private String alamatktp;

	@SerializedName("tanggallahir")
	private String tanggallahir;

	@SerializedName("tgl_update")
	private String tglUpdate;

	@SerializedName("nik")
	private String nik;

	@SerializedName("nama")
	private String nama;

	@SerializedName("namaibu")
	private String namaibu;

	@SerializedName("pekerjaan")
	private String pekerjaan;

	@SerializedName("foto")
	private String foto;

	@SerializedName("nama_karyawan")
	private String namaKaryawan;

	@SerializedName("id_karyawan")
	private String idKaryawan;

	@SerializedName("alias")
	private String alias;

	@SerializedName("tempatlahir")
	private String tempatlahir;

	@SerializedName("suku")
	private String suku;

	@SerializedName("id")
	private String id;

	@SerializedName("ciriciri")
	private String ciriciri;

	@SerializedName("pembinaan")
	private String pembinaan;

	public void setJk(String jk){
		this.jk = jk;
	}

	public String getJk(){
		return jk;
	}

	public void setAlamattinggal(String alamattinggal){
		this.alamattinggal = alamattinggal;
	}

	public String getAlamattinggal(){
		return alamattinggal;
	}

	public void setNamaayah(String namaayah){
		this.namaayah = namaayah;
	}

	public String getNamaayah(){
		return namaayah;
	}

	public void setNoHp(String noHp){
		this.noHp = noHp;
	}

	public String getNoHp(){
		return noHp;
	}

	public void setPendidikanterakhir(String pendidikanterakhir){
		this.pendidikanterakhir = pendidikanterakhir;
	}

	public String getPendidikanterakhir(){
		return pendidikanterakhir;
	}

	public void setKtp(String ktp){
		this.ktp = ktp;
	}

	public String getKtp(){
		return ktp;
	}

	public void setTglCreate(String tglCreate){
		this.tglCreate = tglCreate;
	}

	public String getTglCreate(){
		return tglCreate;
	}

	public void setAgama(String agama){
		this.agama = agama;
	}

	public String getAgama(){
		return agama;
	}

	public void setAlamatktp(String alamatktp){
		this.alamatktp = alamatktp;
	}

	public String getAlamatktp(){
		return alamatktp;
	}

	public void setTanggallahir(String tanggallahir){
		this.tanggallahir = tanggallahir;
	}

	public String getTanggallahir(){
		return tanggallahir;
	}

	public void setTglUpdate(String tglUpdate){
		this.tglUpdate = tglUpdate;
	}

	public String getTglUpdate(){
		return tglUpdate;
	}

	public void setNik(String nik){
		this.nik = nik;
	}

	public String getNik(){
		return nik;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setNamaibu(String namaibu){
		this.namaibu = namaibu;
	}

	public String getNamaibu(){
		return namaibu;
	}

	public void setPekerjaan(String pekerjaan){
		this.pekerjaan = pekerjaan;
	}

	public String getPekerjaan(){
		return pekerjaan;
	}

	public void setFoto(String foto){
		this.foto = foto;
	}

	public String getFoto(){
		return foto;
	}

	public void setNamaKaryawan(String namaKaryawan){
		this.namaKaryawan = namaKaryawan;
	}

	public String getNamaKaryawan(){
		return namaKaryawan;
	}

	public void setIdKaryawan(String idKaryawan){
		this.idKaryawan = idKaryawan;
	}

	public String getIdKaryawan(){
		return idKaryawan;
	}

	public void setAlias(String alias){
		this.alias = alias;
	}

	public String getAlias(){
		return alias;
	}

	public void setTempatlahir(String tempatlahir){
		this.tempatlahir = tempatlahir;
	}

	public String getTempatlahir(){
		return tempatlahir;
	}

	public void setSuku(String suku){
		this.suku = suku;
	}

	public String getSuku(){
		return suku;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCiriciri(String ciriciri){
		this.ciriciri = ciriciri;
	}

	public String getCiriciri(){
		return ciriciri;
	}

	public void setPembinaan(String pembinaan){
		this.pembinaan = pembinaan;
	}

	public String getPembinaan(){
		return pembinaan;
	}

	@Override
 	public String toString(){
		return 
			"PengemisItem{" + 
			"jk = '" + jk + '\'' + 
			",alamattinggal = '" + alamattinggal + '\'' + 
			",namaayah = '" + namaayah + '\'' + 
			",no_hp = '" + noHp + '\'' + 
			",pendidikanterakhir = '" + pendidikanterakhir + '\'' + 
			",ktp = '" + ktp + '\'' + 
			",tgl_create = '" + tglCreate + '\'' + 
			",agama = '" + agama + '\'' + 
			",alamatktp = '" + alamatktp + '\'' + 
			",tanggallahir = '" + tanggallahir + '\'' + 
			",tgl_update = '" + tglUpdate + '\'' + 
			",nik = '" + nik + '\'' + 
			",nama = '" + nama + '\'' + 
			",namaibu = '" + namaibu + '\'' + 
			",pekerjaan = '" + pekerjaan + '\'' + 
			",foto = '" + foto + '\'' + 
			",nama_karyawan = '" + namaKaryawan + '\'' + 
			",id_karyawan = '" + idKaryawan + '\'' + 
			",alias = '" + alias + '\'' + 
			",tempatlahir = '" + tempatlahir + '\'' + 
			",suku = '" + suku + '\'' + 
			",id = '" + id + '\'' + 
			",ciriciri = '" + ciriciri + '\'' + 
			",pembinaan = '" + pembinaan + '\'' + 
			"}";
		}
}