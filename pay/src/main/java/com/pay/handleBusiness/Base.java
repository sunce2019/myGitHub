package com.pay.handleBusiness;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
*@author star
*@version 创建时间：2019年4月8日下午2:32:18
*/
public class Base {
	
	/**
	 * 获取当前时间 10位时间戳
	 * @return
	 */
	public static String timestampToDate() {
		long timeStampSec = System.currentTimeMillis()/1000;
		String timestamp = String.format("%010d", timeStampSec);
	    return timestamp;
	}
	
	private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("busi1");
	/**金额为分的格式 */    
    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+"; 
	/**   
     * 将分为单位的转换为元并返回金额格式的字符串 （除100）  
     *   
     * @param amount  
     * @return  
     * @throws Exception   
     */    
    public static Double changeF2Y(String amount) throws Exception{    
        if(!amount.toString().matches(CURRENCY_FEN_REGEX)) {    
            throw new Exception("金额格式有误");    
        }    
            
        int flag = 0;    
        String amString = amount.toString();    
        if(amString.charAt(0)=='-'){    
            flag = 1;    
            amString = amString.substring(1);    
        }    
        StringBuffer result = new StringBuffer();    
        if(amString.length()==1){    
            result.append("0.0").append(amString);    
        }else if(amString.length() == 2){    
            result.append("0.").append(amString);    
        }else{    
            String intString = amString.substring(0,amString.length()-2);    
            for(int i=1; i<=intString.length();i++){    
                if( (i-1)%3 == 0 && i !=1){    
                    //result.append(",");    
                }    
                result.append(intString.substring(intString.length()-i,intString.length()-i+1));    
            }    
            result.reverse().append(".").append(amString.substring(amString.length()-2));    
        }    
        String  results = "";
        if(flag == 1){    
        	results =  "-"+result.toString();    
        }else{    
        	results =  result.toString();    
        } 
        return Double.parseDouble(results);
    } 
    
    public static String doubleToString(Double d)throws Exception{
    	return String.format("%.2f", d);
    }
    public static Double StringToDouble(String d)throws Exception{
    	return Double.parseDouble(d);
    }
    
    /**
     * 获取请求参数，返回map
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String,String> getCallBackParameter(final HttpServletRequest request)throws Exception{
    	Map<String,String> map = new HashMap<String, String>();
    	String json = getJson(request);
    	try {
            return getMapByJson(json);
		} catch (Exception e) {
			map =  getAllRequestParam(request);
			if(map.size()==0) {
				String[] parame = json.split("&");
				for(String s :parame) {
					if(StringUtils.isBlank(s))continue;
					String[] kv = s.split("=");
					String key = "";
					String v = "";
					try {
						key = kv[0];
					} catch (Exception e2) {
					}
					try {
						v = kv[1];
					} catch (Exception e2) {
					}
					if(!StringUtils.isBlank(key))map.put(key, v);
				}
				return map;
			}
		}
    	return map;
    }
    

    private static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
    	Map<String, String> res = new HashMap<String, String>();
    	Enumeration<String> temp = request.getParameterNames();
    	if (null != temp) {
    		while(temp.hasMoreElements()) {
                String parameterName = temp.nextElement();
                res.put(parameterName, request.getParameter(parameterName));
            }
    	}
    	return res;
    }
    
    public static Map<String, String> getMapByJson(String json) {  

	    Map<String, String> map = new HashMap<String, String>();  

	    // 最外层解析  

	    JSONObject object = JSONObject.fromObject(json);  

	    for (Object k : object.keySet()) {  

	        Object v = object.get(k);  

	        map.put(k.toString(), v.toString());  

	    }  

	    return map;  

	}
	
	public static String changeY2F(Double amount){   
		String amounts = Double.toString(amount);
        String currency =  amounts.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        int index = currency.indexOf(".");    
        int length = currency.length();    
        Long amLong = 0l;    
        if(index == -1){    
            amLong = Long.valueOf(currency+"00");    
        }else if(length - index >= 3){    
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));    
        }else if(length - index == 2){    
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
        }else{    
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
        }    
        return amLong.toString();    
    }
	
	public static String getOrderIdByUUId() {
        int machineId = 1;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {//有可能是负数
            hashCodeV = - hashCodeV;
        }
        // 0 代表前面补充0     
        // 4 代表长度为4     
        // d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }
	public static int getSecondTimestampTwo(Date date){  
	    if (null == date) {  
	        return 0;  
	    }  
	    String timestamp = String.valueOf(date.getTime()/1000);  
	    return Integer.valueOf(timestamp);  
	} 
	
	/**
	 * 回调白名单ip
	 * @param request
	 * @param payorderDao
	 * @param businessDao
	 * @param orderNumber
	 * @return
	 */
	public boolean CheckIP(HttpServletRequest request,String name,String callbackip) {
		String ip = getClientIP(request);
		logger.info("对方ip:"+ip+",我方ip:"+callbackip);
		if(StringUtils.isBlank(callbackip))return false;
		if(callbackip.contains(ip))return false;
		logger.info("回调上分白名单错误："+ip+" 我方商户号白名单："+name+"，白名单ip："+callbackip);
		return true;
	}
	
	public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
	
	public static String getMapASCLL(Map<String, String> map) {
	    String result = "";
	    try {
	        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
	        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
	        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

	            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
	                return (o1.getKey()).toString().compareTo(o2.getKey());
	            }
	        });
	        // 构造签名键值对的格式
	        StringBuilder sb = new StringBuilder();
	        for (Map.Entry<String, String> item : infoIds) {
	            if (item.getKey() != null || item.getKey() != "") {
	                String key = item.getKey();
	                String val = item.getValue();
	                if (!(val == "" || val == null)) {
	                    sb.append(key + "=" + val + "&");
	                }
	            }
	        }
	        result = sb.toString();
	    } catch (Exception e) {
	        return null;
	    }
	    return result;
	}
	
	public static String getMapParam(TreeMap<String, String> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) { 
			  sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0,s.length()-1);
	}
	
	public static String getMapParam(Map<String, String> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) { 
			  sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0,s.length()-1);
	}
	
	public static String getMapParamobj(Map<String, Object> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Object> entry : mapv.entrySet()) { 
			  sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0,s.length()-1);
	}
	
	public static String getMapParamStr(Map<String, String> mapv,String str1,String str2) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) { 
			  sb.append(entry.getKey()).append(str2).append(entry.getValue()).append(str1);
		}
		String s = sb.toString();
		return s.substring(0,s.length()-1);
	}
	
	public static String getMapParamobjStr(Map<String, Object> mapv,String str1,String str2) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Object> entry : mapv.entrySet()) { 
			  sb.append(entry.getKey()).append(str2).append(entry.getValue()).append(str1);
		}
		String s = sb.toString();
		return s.substring(0,s.length()-1);
	}
	
	public static String getJson(HttpServletRequest request) {
		String param= null; 
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            param = responseStrBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("获得JSON字符串："+param);
        return param;
	}
	
}
