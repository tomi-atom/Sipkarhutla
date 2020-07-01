package com.sipkarhutla.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseLaporan {

	@SerializedName("laporan")
	private List<LaporanItem> laporan;

	@SerializedName("status")
	private boolean status;

	public void setLaporan(List<LaporanItem> laporan){
		this.laporan = laporan;
	}

	public List<LaporanItem> getLaporan(){
		return laporan;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ResponseLaporan{" +
			"laporan = '" + laporan + '\'' +
			",status = '" + status + '\'' + 
			"}";
		}
}