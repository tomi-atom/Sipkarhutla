package com.sipkarhutla.response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseSelesai{

	@SerializedName("selesai")
	private List<SelesaiItem> selesai;

	@SerializedName("status")
	private boolean status;

	public void setSelesai(List<SelesaiItem> selesai){
		this.selesai = selesai;
	}

	public List<SelesaiItem> getSelesai(){
		return selesai;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}