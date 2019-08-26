
package com.pay.dao;

import java.util.List;

import com.pay.vo.Member;


/**
*@author star
*@version 创建时间：2019年2月11日上午10:42:09
*/
public interface MemberDao {
	boolean add(Member member);
	 
    abstract boolean add(List<Member> list);
 
    void delete(String key);
 
    Member get(String keyId);
    
    public String getOrderNumber();
    
    public String gets(final String keyId);
    
}
