
package com.pay.vo;

import java.io.Serializable;

/**
*@author star
*@version 创建时间：2019年2月11日上午10:42:34
*/
public class Member implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5608411409752795458L;
	private String key;
	private String values;
	private boolean flag;
	public Member() {}
	
	public Member(String key,String values) {
		this.key = key;
		this.values = values;
	}
	
	public Member(String key,String values,boolean flag) {
		this.key = key;
		this.values = values;
		this.flag = flag;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	   
	@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof Member) {
        	Member vo = (Member) obj;
 
            // 比较每个属性的值 一致时才返回true
            if (vo.key.equals(this.key) && vo.values.equals(this.values))
                return true;
        }
        return false;
    }
 
    /**
     * 重写hashcode 方法，返回的hashCode不一样才再去比较每个属性的值
     */
    @Override
    public int hashCode() {
        return key.hashCode() * values.hashCode();
    }

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	  
}
