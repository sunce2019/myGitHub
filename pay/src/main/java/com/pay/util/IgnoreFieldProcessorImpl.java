package com.pay.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pay.controller.BaseController;

import net.sf.json.JSON;
import net.sf.json.JsonConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.PropertyFilter;

public class IgnoreFieldProcessorImpl implements PropertyFilter {
	Log log = LogFactory.getLog(this.getClass());  
    
    /** 
     * 保留的属性名称 
     */  
    private String[] fields;  
    
    /** 
     * 空参构造方法<br/> 
     * 默认不忽略集合 
     */  
    public IgnoreFieldProcessorImpl() {  
        // empty  
    }  
    
    /** 
     * 构造方法 
     * @param fields 保留属性名称数组 
     */  
    public IgnoreFieldProcessorImpl(String[] fields) {  
        this.fields = fields;   
    }  
    
    /** 
     * 构造方法 
     * @param fields    保留属性名称数组 
     */  
    public IgnoreFieldProcessorImpl(boolean ignoreColl, String[] fields) {  
        this.fields = fields;  
    }  
    
    public boolean apply(Object source, String name, Object value) {  
        Field declaredField = null;  
          
        // 保留设定的属性  
        if(fields != null && fields.length > 0) {  
            if(juge(fields,name)) {    
                 return false;    
            } else {    
                return true;   
                 
            }   
        }  
            
        return false;  
    }  
    /** 
     * 保留相等的属性 
     * @param s 
     * @param s2 
     * @return 
     */  
     public boolean juge(String[] s,String s2){    
         boolean b = false;    
         for(String sl : s){    
             if(s2.equals(sl)){    
                 b=true;    
             }    
         }    
         return b;    
     }    
       
     /** 
      * 获取保留的属性 
      * @param fields 
      */  
    public String[] getFields() {  
        return fields;  
    }  
    
    /** 
     * 设置保留的属性 
     * @param fields 
     */  
    public void setFields(String[] fields) {  
        this.fields = fields;  
    }  
      
    /** 
     * 保留字段转换json 对象 
     * @param configs 保留字段名称 
     * @param entity 需要转换实体 
     * @return 
     */  
    public static JSONObject JsonConfig(String[] configs,Object entity){  
        JsonConfig config = new JsonConfig();  
        config.setJsonPropertyFilter(new IgnoreFieldProcessorImpl(true, configs)); // 保留的属性<span style="font-family: Arial, Helvetica, sans-serif;">configs</span>  
        config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor()); // 将对象中的日期进行格式化  
        JSONObject fromObject = JSONObject.fromObject(entity, config );  
        return fromObject;  
   
    }  
      
      
    /** 
     * 保留字段转换json 数组 
     * @param configs 保留字段名称 
     * @param entity 需要转换实体 
     * @return 
     */  
    public static JSONArray ArrayJsonConfig(String[] configs,Object entity){  
        JsonConfig config = new JsonConfig();  
        config.setJsonPropertyFilter(new IgnoreFieldProcessorImpl(true, configs)); //<span style="font-family: Arial, Helvetica, sans-serif;">保留的属性</span><span style="font-family: Arial, Helvetica, sans-serif;">configs</span>  
        config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());  
        JSONArray fromObject = JSONArray.fromObject(entity, config );  
        return fromObject;  
    }  
}
