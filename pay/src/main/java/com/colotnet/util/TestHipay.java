package com.colotnet.util;

import java.net.URLEncoder;
import java.util.Base64;

import com.pay.util.PayInfo;
import com.pay.util.RSADemo;

public class TestHipay {

  public static void main(String[] args) throws Exception {
    PayInfo payInfo = new PayInfo();
    // account=test&merchantNo=pwb1553510308696&type=1&amount=100&key=27298aba450ef021afa40dd1f7762576
    payInfo.setAccount("167078");
    // 1微信 2支付宝
    payInfo.setType("1");
    payInfo.setAmount("2");
    // 线上
    // 商户号
    payInfo.setMerchantNo("pwb1559037592337");
    // 商户公钥
    payInfo.setKey("5bf0ce373cb9a0a18bffa777168fdfbe");
    payInfo.setMerchantOrderNo("OP142259");
    payInfo.setNotifyUrl("http://test.com/payment_platform/pay/base/hipay/callback");

    // payInfo.setMerchantNo("pwb1554521387775");
    // payInfo.setKey("aa2f895e41773719aa067b1ee475c27e");
    // payInfo.setMerchantOrderNo("OAB1026750998");
    // 本地
    // payInfo.setMerchantNo("pwb1553580631242");
    // payInfo.setKey("19ff212f13c8c573428441b0ec1633e6");
    String mdSign = payInfo.getMDSign();

    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw+cz2iJ1XgimMyLp19eTZAe72d63E+On7PG4gM9z66jtCrblALtocGBaf9d9i/r2QQ7f2RllMscs1Nol15LbHj8L+4g2Sj2FJSmnbR2byiV/R6qt1DP6tTZKKfmV2PzLaHv63McvqYE3c3IwO0iJGM1bRAzUlk5cTIBViwYmfwC1Zg5zYWOSaRkZhtazABg0IkkdUHqGw5th0REgFNNuKFwWXQ97l5jvHJDabptfALDrolL8NllDN0+uXoxVeDeQ4Kh8hOWBzgfOs4QDF2u9amwnr+ulG9KfH6z7J7MnOnRPEAh/C55N6c+X/ERYsh+fi8An2U7w5Dd/DRe+0uFgZQIDAQAB";
    byte[] decoded = Base64.getDecoder().decode(publicKey);
    // 公钥加密传送数据到第三方
    byte[] encryptByPublicKey = RSADemo.encryptByPublicKey(payInfo.toString().getBytes(), decoded);
    String encodeToString = Base64.getEncoder().encodeToString(encryptByPublicKey);
    String encode = URLEncoder.encode(encodeToString, "UTF-8");
    String url = "https://hipay3721.com/pay?pr=" + encode + "&sign=" + mdSign;
    // 生成请求地址
    System.out.println(url);
  }

}
