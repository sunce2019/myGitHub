package com.pay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.time.DateFormatUtils;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDateValueProcessor implements JsonValueProcessor {
	private String format ="yyyy-MM-dd HH:mm:ss";  
    
    public JsonDateValueProcessor() {  
        super();  
    }  
      
    public JsonDateValueProcessor(String format) {  
        super();  
        this.format = format;  
    }  
  
    public Object processArrayValue(Object paramObject,  
            JsonConfig paramJsonConfig) {  
        return process(paramObject);  
    }  
  
    public Object processObjectValue(String paramString, Object paramObject,  
            JsonConfig paramJsonConfig) {  
        return process(paramObject);  
    }  
      
      
    private Object process(Object value){  
        if(value instanceof Date){    
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);    
            return sdf.format(value);  
        }    
        return value == null ? "" : value.toString();    
    }
    
    public static JsonConfig getDataJsonConfig(){
    	JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessor() {
		@Override
		public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
		         // TODO Auto-generated method stub
		 //导包import org.apache.commons.lang.time.DateFormatUtils; jar包：commons-lang-2.6.jar
		 return DateFormatUtils.format((Date)arg1, "yyyy-MM-dd HH:mm:ss");
		}
		@Override
		public Object processArrayValue(Object arg0, JsonConfig arg1) {
		     return null;
		     }
		});
		return jsonConfig;
    }
}
