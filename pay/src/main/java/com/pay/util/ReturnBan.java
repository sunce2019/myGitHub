package com.pay.util;

import java.util.List;

public class ReturnBan {
	private boolean state;
	private String msg;
	private List list;
	private Object obj;
	private Object obj2;
	
	public ReturnBan(){}
	
	public ReturnBan(String msg,boolean state){
		this.msg = msg;
		this.state = state;
	}
	
	public ReturnBan(String msg,boolean state,Object obj){
		this.msg = msg;
		this.state = state;
		this.obj = obj;
	}
	
	public ReturnBan(String msg,boolean state,Object obj,Object obj2){
		this.msg = msg;
		this.state = state;
		this.obj = obj;
		this.obj2 = obj2;
	}
	
	public ReturnBan(String msg,boolean state,List list){
		this.msg = msg;
		this.state = state;
		this.list = list;
	}
	
	public ReturnBan(String msg,boolean state,Object obj,List list){
		this.msg = msg;
		this.state = state;
		this.obj = obj;
		this.list = list;
	}
	
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj2() {
		return obj2;
	}

	public void setObj2(Object obj2) {
		this.obj2 = obj2;
	}
}
