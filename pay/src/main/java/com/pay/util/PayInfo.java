package com.pay.util;

public class PayInfo {

  private String account;
  private String type;
  private String amount;
  private String merchantNo;
  private String merchantOrderNo;
  private String notifyUrl;
  private String key;
  private String sign;

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getMerchantNo() {
    return merchantNo;
  }

  public void setMerchantNo(String merchantNo) {
    this.merchantNo = merchantNo;
  }

  public String getMerchantOrderNo() {
    return merchantOrderNo;
  }

  public void setMerchantOrderNo(String merchantOrderNo) {
    this.merchantOrderNo = merchantOrderNo;
  }

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public void setNotifyUrl(String notifyUrl) {
    this.notifyUrl = notifyUrl;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getMDSign() {
    StringBuilder sb = new StringBuilder();
    sb.append("account=").append(StrUtil.strnull(account));
    sb.append("&amount=").append(StrUtil.strnull(amount));
    sb.append("&key=").append(StrUtil.strnull(key));
    sb.append("&merchantOrderNo=").append(StrUtil.strnull(merchantOrderNo));
    sb.append("&merchantNo=").append(StrUtil.strnull(merchantNo));
    sb.append("&notifyUrl=").append(StrUtil.strnull(notifyUrl));
    sb.append("&type=").append(type);
    sb.append("8ab211a6536711e893d10242ac11as02");
    return Md5Util.getMD5String(sb.toString());
  }
  
  public boolean verifySign() {
    return getMDSign().equals(sign);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("account=").append(StrUtil.strnull(account));
    sb.append("&merchantNo=").append(StrUtil.strnull(merchantNo));
    sb.append("&merchantOrderNo=").append(StrUtil.strnull(merchantOrderNo));
    sb.append("&notifyUrl=").append(StrUtil.strnull(notifyUrl));
    sb.append("&type=").append(type);
    sb.append("&amount=").append(StrUtil.strnull(amount));
    sb.append("&key=").append(StrUtil.strnull(key));
    return sb.toString();
  }
}
