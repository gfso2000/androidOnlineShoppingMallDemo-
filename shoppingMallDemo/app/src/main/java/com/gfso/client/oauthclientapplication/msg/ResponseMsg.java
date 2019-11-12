package com.gfso.client.oauthclientapplication.msg;

import java.util.Map;

public class ResponseMsg {
	private String status;
	private String errorMessage;
	private Map<String, String> data;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public boolean isSuccess(){
		return status.equalsIgnoreCase("success");
	}
}
