package com.pay.model;

import java.util.Map;
 
/**
 * 图片上传返回结果封装类
 * @author acer
 *
 */
public class ResponseResult {

	private Integer code;
    private String message;
    private Map<String, Object> result;

    public Integer getCode() {
        return this.code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getResult() {
        return this.result;

    }
    public void setResult(Map<String, Object> result) {
        this.result = result;

    }


}

