package com.sample;

public class MessageReceived {
	String msg;
	String msisdn;
	//boolean fired;
	public MessageReceived(String msg, String msisdn) {
		super();
		this.msg = msg;
		this.msisdn = msisdn;
		
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	
	
}
