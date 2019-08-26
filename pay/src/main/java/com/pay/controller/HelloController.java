package com.pay.controller;
import com.alibaba.fastjson.JSONObject;
import com.pay.enums.ConfigCode;
import com.pay.model.SystConfig;
import com.pay.service.SystemService;
import com.pay.util.GoogleAuthenticator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
@Controller
public class HelloController {
	@Autowired
	private SystemService systemService;
    //成功后网页跳转地址
    @RequestMapping(value = "payreturn.do")
    public ModelAndView ad(String orderid) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("payreturn");//返回到成功页面
        modelAndView.addObject("orderid", orderid);//返回交易成功订单号
        return modelAndView;

    }

    @RequestMapping(value = "pay.do")
    public @ResponseBody
	static
    Map weixin(@RequestBody String jsonObj) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //从网页传入price:支付价格， istype:支付渠道：1-支付宝；2-微信支付
        JSONObject jsonObject = JSONObject.parseObject(jsonObj);
        String return_type = "json";//支付金额
		
		String api_code = "33396133";//测试商户号
		String api_key = "879adf264bf0fc038d427a413ede6b03";//测试签名秘钥
		
        //String api_code = jsonObject.getString("api_code");//商户号
        //String api_key = jsonObject.getString("api_key");//签名秘钥
		
        String price = "100.00";//订单定价
        String is_type = "alipay";//支付类型
        String mark = "mark";//描述

        //发起时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = df.format(1294890859000L);

        String order_id = UUID.randomUUID().toString();//此处就在您服务器生成新订单，并把创建的订单号传入到下面的orderid中。每次有任何参数变化，订单号就变一个吧。
        String return_url = "http://localhost:8080/payreturn.do";//支付成功，用户会跳转到这里
        String notify_url = "http://localhost:8080/paynotify.do";//通知状态异步回调接收地址
        Map map = new HashMap();
        map.put("return_type", return_type);
        map.put("api_code", api_code);
        map.put("is_type", is_type);
        map.put("price", price);
        map.put("order_id", order_id);
        map.put("time", dateTime);
        map.put("mark", mark);
        map.put("return_url", return_url);
        map.put("notify_url", notify_url);



        //对参数名按照ASCII升序排序
        Object[] key = map.keySet().toArray();
        Arrays.sort(key);
        System.out.println(key.toString());


        //生成加密原串
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < key.length; i++) {
            res.append(key[i] + "=" + map.get(key[i]) + "&");
        }
        //再拼接秘钥
        String src = res.append("key=" + api_key).toString();

        //MD5加密
		String md5 = MD5Encoder(src);
			
		
        //将字符串全部转为大写
        String rel = md5.toUpperCase();


        Map maps = new HashMap();
        maps.put("return_type", return_type);
        maps.put("api_code", api_code);
        maps.put("is_type", is_type);
        maps.put("price", price);
        maps.put("order_id", order_id);
        maps.put("time", dateTime);
        maps.put("mark", mark);
        maps.put("return_url", return_url);
        maps.put("notify_url", notify_url);
        maps.put("sign", rel);
        return maps;


    }
    
    public static void main(String[] args) {
    	try {
			weixin(null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    //通知状态异步回调接收地址
    @RequestMapping(value = "paynotify.do")
    public @ResponseBody
    String paynotify(@RequestBody String jsonStr) throws NoSuchAlgorithmException, IOException {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		
		
			/**返回内容参数：
		{
		"paysapi_id":"",
		"order_id":"",
		"is_type":"",
		"price":"",
		"real_price":"",
		"mark":"",
		"code":""，
		"sign":""
		"messages": {
			"returncode": "SUCCESS"
		}
		}
	**/	
		
        String order_id = jsonObject.getString("order_id");//是您在发起支付接口传入的您的自定义订单号
        String paysapi_id = jsonObject.getString("paysapi_id"); //是此订单在Api服务器上的唯一编号
        String price = jsonObject.getString("price"); //支付金额
        String real_price = jsonObject.getString("real_price"); //实际支付金额
        String mark = jsonObject.getString("mark"); //描述
        String sign = jsonObject.getString("sign"); //签名认证串
		
		String code = jsonObject.getString("code"); //订单状态 0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
        String is_type = jsonObject.getString("is_type"); //支付类型
        //校验传入的参数是否格式正确，略
        String api_code = "商户号";//测试商户号
		String api_key = "秘钥";//测试签名秘钥
        Map map = new HashMap();
        map.put("order_id", order_id);
        map.put("paysapi_id", paysapi_id);
        map.put("price", price);
        map.put("real_price", real_price);
        map.put("mark", mark);
        map.put("code", code);
        map.put("is_type", is_type);
        map.put("api_code", api_code);
		
		

        //拼接
        StringBuffer ress = new StringBuffer();
        String content = ress.append("回调数据：").append(order_id).append(" ").append(paysapi_id).append(" ").append(price).append(" ").append(real_price).append(" ").append(mark).append(" ").append(sign).toString();

        //数据写入本地文件
        String path = "D:\\User_notify_url.txt";
        FileWriter fw = new FileWriter(path, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.append(content + "\r\n");


        //对参数名按照ASCII升序排序
        Object[] key = map.keySet().toArray();
        Arrays.sort(key);

        //生成加密原串
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < key.length; i++) {
            res.append(key[i] + "=" + map.get(key[i]) + "&");
        }

        String src = res.append("key=" + api_key).toString();

        //MD5加密
          //MD5加密
		String md5 = MD5Encoder(src);
			
        String rel = md5.toUpperCase();

        //判断是否匹配
        if (rel.equals(sign)) {
            bw.newLine();
            content = "sign值不匹配";
            bw.append(content + "\r\n");

        } else {

            //校验key成功，执行自己的业务逻辑：加余额，订单付款成功，装备购买成功等等。

            Map maps = new HashMap();
            maps.put("order_id", order_id);
            maps.put("paysapi_id", paysapi_id);
            maps.put("price", price);
            maps.put("real_price", real_price);
            maps.put("order_id", order_id);
            maps.put("mark", mark);
            bw.newLine();
            content = "支付成功";
            bw.append(content + "\r\n");
            bw.newLine();
            bw.close();
            fw.close();

            return "SUCCESS";//成功后返回给我们SUCCESS,表示回调已收到
        }
        return null;
    }
	
	
	
	
	public static String MD5Encoder(String s) {
        try {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = ((int) md[i]) & 0xff;
                if (val < 16){
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
	
	
	
}