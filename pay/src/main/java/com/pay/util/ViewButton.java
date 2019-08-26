package com.pay.util;
/**
*@author star
*@version 创建时间：2019年6月22日下午8:11:15
*/
public class ViewButton {
	private int index;
	private boolean state;
	private String name;
	public ViewButton() {}
	public ViewButton(int index,boolean state,String name) {
		this.index = index;
		this.state = state;
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
