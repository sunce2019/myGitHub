<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <!-- 禁止浏览器从本地计算机的缓存中访问页面内容。  -->
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache, must-revalidate">
    <meta http-equiv="expires" content="0">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no,email=no,adress=no">
    <title>银联云闪付充值</title>
    <script src="../../js/qrcode.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.1.1.min.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" href="../../css/union.css" />
    <link rel="bookmark" href="../../images/faviconysf.ico" type="image/x-icon">
    <style>
      * {
        margin: 0;
        padding: 0;
      }

      html {
        font-size: 16px;
      }

      body {
        background: #fff;
      }

      .union-page {
        width: 96%;
        max-width: 720px;
        height: auto;
        margin: 10px auto;
      }

      .header {
        height: 46px;
        margin-top: 2%;
        background: #f5f5f5;
        box-shadow: 0 0 1px #409EFF;
        padding-left: 10px;
        border-radius: 5px;
        display: flex;
        flex-direction: row;
      }

      .header div {
        margin-top: 4px;
      }

      .ysf {
        width: auto;
        height: 53px;
        margin: -7px 0 0 0;
      }

      .welcome {
        font-size: 1.375rem;
        line-height: 46px;
        color: darkslateblue;
        margin-left: 0.5rem;
      }

      .qrcode-wrap {
        height: 200px;
        margin-top: 2%;
        position: relative;
      }

      #qrcode_first {
        width: 162px;
        margin: 0 auto;
      }

      .qr_image {
        width: 100%;
        margin-top: 2px;
      }

      #download_startapp_button_group {
        margin: 5px auto;
      }

      .card-text {
        font-size: 14px;
      }

      .wrap-title {
        font-size: 0.8rem;
        width: 175px;
        height: 175px;
        position: absolute;
        left: 50%;
        margin-left: -87.5px;
        top: -5px;
        background: linear-gradient(to left, #17a2b8, #17a2b8) left top no-repeat,
          linear-gradient(to bottom, #17a2b8, #17a2b8) left top no-repeat,
          linear-gradient(to left, #17a2b8, #17a2b8) right top no-repeat,
          linear-gradient(to bottom, #17a2b8, #17a2b8) right top no-repeat,
          linear-gradient(to left, #17a2b8, #17a2b8) left bottom no-repeat,
          linear-gradient(to bottom, #17a2b8, #17a2b8) left bottom no-repeat,
          linear-gradient(to left, #17a2b8, #17a2b8) right bottom no-repeat,
          linear-gradient(to left, #17a2b8, #17a2b8) right bottom no-repeat;
        background-size: 3px 30px, 30px 3px, 3px 30px, 30px 3px;
        border-radius: 4px;
      }

      .remind {
        position: absolute;
        top: 88%;
        background: #fff;
      }

      .order-info {
        text-align: center;
      }

      .order-info p {
        margin: 3px 0;

      }

      .link {
        display: inline-block;
        width: 33.333333%;
        margin: 2px 0;
        border: 1px solid transparent;
        font-size: 13px;
        text-align: center;
        z-index: 100;
      }

      .link svg {
        width: 90%;
        max-width: 100px;
      }

      .cf_button {
        color: rgb(253, 253, 255);
        display: inline-block;
        background-color: rgb(75, 82, 177);
        font-size: 12px;
        width: 100px;
        height: 30px;
        line-height: 30px;
        margin-top: 3px;
        border-radius: 5px;
      }

      .tips {
        color: #333;
        animation: hue 1s infinite linear;
      }

      .memo,
      .memo:visited,
      .memo:hover,
      .mome:link,
      .memo:active {
        width: auto;
        outline: none;
        border: 0;
        color: #212529;
        background: #fff;
        background-color: transparent;
      }

      #uprice {
        color: #f00;
      }

      .goldfish {
        width: 155px;
        height: 30px;
        margin: 0 auto;
        display: flex;
      }

      .logo {
        width: 25px;
        margin-top: 1.5px;
      }

      .scroll {
        margin-top: 3px;
        width: 130px;
        height: 20px;
        line-height: 25px;
        font-size: 13px;
        color: #808080;
        font-weight: 200;
      }

      /*反馈 解决方案*/
      .help,
      .solve {
        width: 46px;
        height: 46px;
        background: linear-gradient(to right bottom, #1abc9c, #17a2b8, #108ee9);
        /* background-color: #17a2b8; */
        color: #fff;
        text-align: center;
        line-height: 15px;
        border-radius: 50%;
        z-index: 12;
        box-shadow: 0 0 8px #108ee9;
        white-space: pre-wrap;
        letter-spacing: 1px;
        font-size: 14px;
        padding: 9px 3px 7px;
        box-sizing: border-box;
        z-index: 999;
        cursor: pointer;
      }

      .help {
        position: fixed;
        right: 15px;
        top: 160px;
      }

      .solve {
        position: fixed;
        right: 15px;
        top: 215px;
      }

      .am-list .am-list-content {
        text-align: left;
      }

      .am-list .am-list-item {
        font-size: 16px;
      }

      #hiddenbox,
      #solvebox {
        display: none;
        width: 100%;
        height: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 1000;
      }

      .am-agreement {
        padding: 0 10px;
      }

      .am-agreement-content {
        text-align: left;
      }

      .am-agreement-content>p {
        color: #2c3e50;
      }

      .toast {
        width: 70%;
        height: auto;
        position: fixed;
        border-radius: 5px;
        top: 50%;
        left: 50%;
        margin: -10px 0 0 -35%;
        color: #fff;
        font-size: 13px;
        padding: 8px 15px;
        box-sizing: border-box;
        background: rgba(0, 0, 0, .5);
        z-index: 10001;
        display: none;
        text-align: center;
      }
      .appimg {
                width: 60px;
                height: 100px;
                background: transparent;
                border: transparent;
                margin-right: 5px;
                margin-bottom: 10px;
            }
            .appimg_group {
                display:flex;
                display: -webkit-flex;
                flex-wrap: wrap;
                -webkit-flex-wrap: warp;
            }

    </style>
  </head>

  <body onload="onload()">
    <div class="union-page">
     <div class="header">
                <div><img class="ysf" src="../../images/icon-yunshanfu@2x.png" alt="">
                </div>
                <span class="welcome">欢迎使用 银联云闪付</span>
            </div>
      <div class="sction">
        <p style="font-size:14px;line-height: 24px;width:96%;margin:10px auto 2px;">
          &nbsp;&nbsp;&nbsp;&nbsp;银联云闪付是中国银联在2017年12月11日推出的针对移动支付新品牌，包括银联云闪付二维码支付、智能手机、银联IC卡、智能手表手环支付产品。近期推出很多热门红包活动！
        </p>
        <div style="height:95px;text-align:center;margin:10px auto 15px;background: #f5f5f5;border-radius: 4px;">
          <p style="height:48px;line-height:45px;overflow: hidden;margin:-10px 0;color:#000;">¥
            <span id="amo" style="font-weight:800;color:#000;font-size:40px;"></span>
          </p>
          <p style="margin:4px 0 1px;">订单号：
            <button type="button" id="mem" class="memo" style="color:#f00;font-size:16px;">10A45OQ0jqE0sA</button>
          </p>
          <p style="margin:4px 0 1px;" id="times">平均出码时间：15-30秒请耐心等待。
            <button type="button" id="mem" class="memo" style="color:#f00;font-size:16px;"></button>
          </p>
          <p style="margin:0 0;display: none" id="disTwo">过期时间：<button type="button" id="dat" class="memo" style="color:#f00;font-size:16px;">05-29 14:59:23</button>
          </p>
        </div>

        <div class="qrcode-wrap" >
          <div class="wrap-title" ></div>
          <div id="tixing_desc">
            <a id="qrcode_first_link" href="javascript:;">
              <div id="qrcode_first">
					<img src="../images/loading24.gif" style="width:100px;height:100px;margin:50px 0 0 25px;">
              </div>
            </a>
            <div class="remind" >
              <p style="color:#808080;font-size: 15px;text-align: center;letter-spacing: 1px;margin-bottom:1px;display: none" id="disOne" >
                二维码过期剩:
                <span id="time" style="color:#f00;">0 时 0 分 0 秒</span>
              </p>
              <div class="order-info" style="background:#fff;height: auto;">
                <p style="color:#000;text-align: center;">到账金额为【
                  <span id="uprice"></span> 】，请放心支付</p>
                <!-- <p style="color:#000;text-align: center;">到账以实际支付金额为准，请放心支付</p> -->
                <p
                  style="color:#fc4209;font-size:1.225rem;;text-align: center;padding:0;margin:0 auto;border-top:1px solid #eee;padding-top:5px;">
                  重复扫码、过期支付 均不到账</p>
                <p style="color:#000;text-align: center;">正常付款即时到账，未到账请联系客服</p>
              </div>
              
              <p style="text-align:left;margin:8px 0 2px;font-size:15px;">二维码支持的APP列表：</p>
              <div class="appimg_group">
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id600273928" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.unionpay"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20171206145954.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">云闪付</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/zhong-guo-yin-xing-shou-ji/id399608199?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.chinamworld.bocmbci"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526140733.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">中国银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/nong-xing-zhang-shang-yin-xing/id515651240?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.android.bankabc"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170707163613.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">农业银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/gong-xing-shou-ji-yin-xing/id423514795?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.icbc"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526141013.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">工商银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/zhong-guo-jian-she-yin-xing/id391965015?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.chinamworld.main"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526140454.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">建设银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/jiao-tong-yin-xing/id337876534?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.bankcomm.Bankcomm"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526135958.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">交通银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/you-chu-shou-ji-yin-xing/id493489515?mt=8" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.yitong.mbank.psbc"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526134223.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">邮储银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id392899425" data-android="http://youhui.95516.com/hybrid_v3/html/help/download.html"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526134200.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">招商银行</div></button>
              					<button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/pu-fa-shou-ji-yin-xing/id471855847?mt=8" data-android="https://sj.qq.com/myapp/detail.htm?apkName=cn.com.spdb.mobilebank.per&apkCode=132"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526114559.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">浦发银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/min-sheng-yin-xing-shou-ji/id523091708?mt=8" data-android="https://sj.qq.com/myapp/detail.htm?apkName=cn.com.cmbc.newmbank"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170918111337.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">民生银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/min-sheng-yin-xing-shou-ji/id447733826?mt=8" data-android="https://sj.qq.com/myapp/detail.htm?apkName=com.cebbank.mobile.cemb"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170712174527.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">光大银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id415872960" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.cgbchina.xpt"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526113747.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">广发银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/xing-ye-yin-xing-shou-ji-yin/id433592972?mt=8" data-android="https://sj.qq.com/myapp/detail.htm?apkName=com.cib.cibmb"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170526161709.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">兴业银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id485543769" data-android="https://android.myapp.com/myapp/detail.htm?apkName=cn.com.cqb.mobilebank.per"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20171127111812.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">重庆银行</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id414245413" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.jingdong.app.mall"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170718191428.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">京东</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id423084029" data-android="https://android.myapp.com/myapp/detail.htm?apkName=com.sankuai.meituan"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20170718191704.png" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">美团</div></button>
                                <button class="appimg" onclick="download_app(this)" data-ios="https://itunes.apple.com/cn/app/id379395415" data-android="https://android.myapp.com/myapp/detail.htm?apkName=ctrip.android.view"><img src="https://billcloud.unionpay.com/upwxs-mktc/upload/mapp/logoFile_20180913095651.jpg" style="width:100%;"/><div style="bottom:5px;align-content: center;font-size: 15px;height:40px;">携程</div></button>
                           
                            </div>
              <a style="color:deeppink;text-align:left;margin:18px 0 2px;font-size:15px;" href="https://billcloud.unionpay.com/upwxs-mktc/web/mapp/wxe990cdbcc189456e/custom/alllist" target="_blank">更多</a>
              
              
              
              <div id="download_startapp_button_group"
                style="text-align:center;border-top:1px solid #eee;padding-top:1px;">
                <div>
                  <p style="text-align:left;margin:8px 0 2px;font-size:15px;">
                    <svg class="icon"
                      style="width: auto; height: 15px;vertical-align: middle;fill: currentColor;overflow: hidden;margin-bottom:4px;"
                      viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8106">
                      <path
                        d="M818.09308393 664.0074184L818.12885203 663.79281502 818.44545724 663.24571079 818.57925297 663.65372077Z"
                        p-id="8107"></path>
                      <path
                        d="M1020.61195215 420.0834452c9.65448002-10.12872505 11.05999517-25.98019343 2.54874003-37.71181707-4.66165174-6.42881298-11.44018683-10.3764453-18.68369676-11.68393412l-0.03576679-0.11127594-322.92432206-47.1768634-0.49676613-0.67295277 0.27024004 0.82529404-0.51531166-0.21592769-0.25699303-0.28878689L535.7353271 30.64150714l-0.07815776 0.0013243c-6.67520893-12.29727342-21.32781578-18.50221155-35.10611476-14.00482377-7.5521667 2.46395941-13.38750896 7.70318365-16.85824763 14.19425744L483.57358386 30.83226511 339.68766281 323.33331962l0 0.00132431-1.98838879 0.69414695-321.09225008 46.65757751-0.02516969 0.07550783c-13.76107776 2.53019446-24.20508371 14.53603238-24.20508373 29.02967381 0 7.94163113 3.16870347 15.11625478 8.26485919 20.42833821l-0.03709239 0.11127592 233.62700996 227.72940557 0.0013243 0.44775103-55.48146365 320.7451753 0.06226083 0.04636518c-1.86519017 13.86705385 6.30561566 27.52215551 20.08391336 32.02086631 7.54819246 2.46395941 15.35470227 1.67840642 21.98487162-1.51546677l0.09405467 0.06888496 289.08065527-151.45398467 0.79482579 0.2543444-0.63056184-0.4596737 0.0105971-0.00264992 0.93789426 0.22652477 288.27788232 151.8407992 0.06226082-0.0463652c12.61520426 6.05259556 28.12754763 2.48912783 36.6546997-9.22924749 4.67224882-6.42086451 6.33078407-14.08695575 5.33857745-21.3808038l0.09272907-0.06888497-54.91846249-321.69234236L1020.63712186 420.15762872 1020.61195215 420.0834452z"
                        p-id="8108"></path>
                      <path
                        d="M786.14375123 647.91882686L786.1755451 647.72674457 786.45903215 647.2366026 786.57825653 647.60222164Z"
                        p-id="8109"></path>
                    </svg>
                    已安装云闪付，截图保存后，可直接点击下方按钮进行扫码支付，或使用其他银行APP扫码支付：</p>
                  <div id="open-app"
                    style="border-radius: .3rem;width:70%;margin:0 auto;height:40px;line-height:40px;text-align: center;color: #fff;background: #67C23A;border-color: #67C23A;box-sizing: border-box;">
                    启动云闪付APP
                  </div>
                  <p style="text-align:left;margin:8px 0 2px;font-size:15px;">
                    <svg class="icon"
                      style="width: auto; height: 15px;vertical-align: middle;fill: currentColor;overflow: hidden;margin-bottom:4px;"
                      viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8106">
                      <path
                        d="M818.09308393 664.0074184L818.12885203 663.79281502 818.44545724 663.24571079 818.57925297 663.65372077Z"
                        p-id="8107"></path>
                      <path
                        d="M1020.61195215 420.0834452c9.65448002-10.12872505 11.05999517-25.98019343 2.54874003-37.71181707-4.66165174-6.42881298-11.44018683-10.3764453-18.68369676-11.68393412l-0.03576679-0.11127594-322.92432206-47.1768634-0.49676613-0.67295277 0.27024004 0.82529404-0.51531166-0.21592769-0.25699303-0.28878689L535.7353271 30.64150714l-0.07815776 0.0013243c-6.67520893-12.29727342-21.32781578-18.50221155-35.10611476-14.00482377-7.5521667 2.46395941-13.38750896 7.70318365-16.85824763 14.19425744L483.57358386 30.83226511 339.68766281 323.33331962l0 0.00132431-1.98838879 0.69414695-321.09225008 46.65757751-0.02516969 0.07550783c-13.76107776 2.53019446-24.20508371 14.53603238-24.20508373 29.02967381 0 7.94163113 3.16870347 15.11625478 8.26485919 20.42833821l-0.03709239 0.11127592 233.62700996 227.72940557 0.0013243 0.44775103-55.48146365 320.7451753 0.06226083 0.04636518c-1.86519017 13.86705385 6.30561566 27.52215551 20.08391336 32.02086631 7.54819246 2.46395941 15.35470227 1.67840642 21.98487162-1.51546677l0.09405467 0.06888496 289.08065527-151.45398467 0.79482579 0.2543444-0.63056184-0.4596737 0.0105971-0.00264992 0.93789426 0.22652477 288.27788232 151.8407992 0.06226082-0.0463652c12.61520426 6.05259556 28.12754763 2.48912783 36.6546997-9.22924749 4.67224882-6.42086451 6.33078407-14.08695575 5.33857745-21.3808038l0.09272907-0.06888497-54.91846249-321.69234236L1020.63712186 420.15762872 1020.61195215 420.0834452z"
                        p-id="8108"></path>
                      <path
                        d="M786.14375123 647.91882686L786.1755451 647.72674457 786.45903215 647.2366026 786.57825653 647.60222164Z"
                        p-id="8109"></path>
                    </svg>
                    未安装云闪付，可点击下方按钮下载：</p>
                  <div style="display: flex;width:100%;justify-content: space-around;margin: 1px auto 0;">
                    <a href="https://youhui.95516.com/hybrid_v3/html/help/download.html" target="_blank" class="link">
                      <svg class="icon"
                        style="width: 100px; height: 36px;vertical-align: middle;fill: currentColor;overflow: hidden;color:#28a745;"
                        viewBox="0 0 4008 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="449">
                        <path
                          d="M61.134918 333.00879C27.379107 333.00879 0 360.868105 0 395.213644L0 638.199391C0 672.54493 27.377159 700.404245 61.134918 700.404245 94.941385 700.404245 122.320492 672.54493 122.320492 638.199391L122.320492 395.213644C122.320492 360.868105 94.943333 333.00879 61.136867 333.00879L61.134918 333.00879 61.134918 333.00879ZM798.917402 333.00879C765.108988 333.00879 737.733779 360.868105 737.733779 395.213644L737.733779 638.199391C737.733779 672.54493 765.110938 700.404245 798.917402 700.404245 832.675162 700.404245 860.052321 672.54493 860.052321 638.199391L860.052321 395.213644C860.052321 360.868105 832.675162 333.00879 798.917402 333.00879L798.917402 333.00879 798.917402 333.00879ZM559.000986 104.807702 609.28596 31.792474C612.299942 27.417221 611.547908 21.734895 607.627979 19.020509 603.758705 16.35526 598.134035 17.713436 595.17071 22.037586L542.927718 97.920511C508.517286 84.342682 470.290185 76.800972 430.001807 76.800972 389.764085 76.800972 351.486328 84.342682 317.075897 97.920511L264.881613 22.037586C261.867631 17.713436 256.291669 16.35526 252.373688 19.020509 248.504414 21.734895 247.752379 27.417221 250.766361 31.792474L301.000681 104.807702C221.078547 142.070076 165.018885 212.773849 157.884302 295.343482L702.117364 295.343482C694.98473 212.771884 638.973774 142.070076 559.000986 104.807702L559.000986 104.807702 559.000986 104.807702ZM315.215295 219.963731C298.586769 219.963731 285.075481 206.48811 285.075481 189.8421 285.075481 173.146952 298.588716 159.67133 315.215295 159.67133 331.841873 159.67133 345.3064 173.146952 345.3064 189.8421 345.3064 206.48811 331.843821 219.963731 315.215295 219.963731L315.215295 219.963731 315.215295 219.963731ZM548.603042 219.963731C531.976464 219.963731 518.46323 206.48811 518.46323 189.8421 518.46323 173.146952 531.976464 159.67133 548.603042 159.67133 565.229621 159.67133 578.694149 173.146952 578.694149 189.8421 578.694149 206.48811 565.231568 219.963731 548.603042 219.963731L548.603042 219.963731 548.603042 219.963731ZM159.090284 778.852177C159.090284 805.253076 180.490136 826.624201 206.813278 826.624201L261.318216 826.624201 261.318216 961.795146C261.318216 996.191788 288.695375 1024 322.453135 1024 356.259602 1024 383.63871 996.191788 383.63871 961.795146L383.63871 826.624201 478.831422 826.624201 478.831422 961.795146C478.831422 996.191788 506.208581 1024 540.015048 1024 573.772805 1024 601.149964 996.191788 601.149964 961.795146L601.149964 826.624201 655.654903 826.624201C681.976098 826.624201 703.377899 805.253076 703.377899 778.852177L703.377899 334.87014 159.096129 334.87014 159.096129 778.852177 159.090284 778.852177 159.090284 778.852177ZM1283.482598 236.605809 1306.704086 305.180923 1336.697778 305.180923 1262.517211 87.163207 1228.652297 87.163207 1154.795142 305.180923 1183.822491 305.180923 1206.399101 236.605809 1283.480648 236.605809 1283.482598 236.605809 1283.482598 236.605809ZM1212.206908 214.609648 1233.491811 151.534067C1237.684499 138.270721 1241.232308 125.009341 1244.458651 112.070307L1245.103529 112.070307C1248.327925 124.686996 1251.554269 137.624065 1256.391835 151.856413L1277.678687 214.609648 1212.206908 214.609648 1212.206908 214.609648ZM1366.695368 305.180923 1395.075888 305.180923 1395.075888 211.050086C1395.075888 206.197213 1395.720768 201.346302 1397.010526 197.786741 1401.848092 181.936769 1416.36274 168.675388 1435.068126 168.675388 1461.837425 168.675388 1471.189144 189.700582 1471.189144 214.931994L1471.189144 305.180923 1499.569664 305.180923 1499.569664 211.698709C1499.569664 158.002601 1466.028163 145.065533 1444.417898 145.065533 1418.614945 145.065533 1400.554436 159.622192 1392.81589 174.501197L1392.17101 174.501197 1390.559786 148.625094 1365.40366 148.625094C1366.370006 161.564128 1366.693418 174.827474 1366.693418 190.999792L1366.693418 305.182888 1366.695368 305.180923 1366.695368 305.180923ZM1652.123486 75.517486 1652.123486 168.999699 1651.478607 168.999699C1644.38299 156.384976 1628.257119 145.063567 1604.390751 145.063567 1566.333149 145.063567 1534.081405 177.087825 1534.402871 229.164341 1534.402871 276.714261 1563.43022 308.738518 1601.164408 308.738518 1626.641999 308.738518 1645.672748 295.475172 1654.379589 278.007575L1655.024467 278.007575 1656.314225 305.178958 1681.791816 305.178958C1680.825473 294.504204 1680.502058 278.654231 1680.502058 264.744229L1680.502058 75.513555 1652.121538 75.513555 1652.123486 75.517486 1652.123486 75.517486ZM1652.123486 239.514783C1652.123486 244.043347 1651.80202 247.925253 1650.833728 251.80716 1645.672748 273.156664 1628.257119 285.771388 1609.228318 285.771388 1578.589747 285.771388 1563.106805 259.569009 1563.106805 227.869062 1563.106805 193.258178 1580.522436 167.380109 1609.873196 167.380109 1631.160048 167.380109 1646.639093 182.259113 1650.833728 200.375335 1651.800073 203.934895 1652.123486 208.785805 1652.123486 212.3434L1652.123486 239.514783 1652.123486 239.514783ZM1727.593811 305.180923 1755.652868 305.180923 1755.652868 221.726805C1755.652868 216.873929 1756.297746 212.347332 1756.942626 208.463459 1760.8119 187.113954 1775.003136 171.912604 1795.000232 171.912604 1798.869504 171.912604 1801.772438 172.236915 1804.675354 172.881606L1804.675354 146.034535C1802.095846 145.387878 1799.837802 145.065533 1796.611443 145.065533 1777.582645 145.065533 1760.490434 158.328878 1753.392868 179.354071L1752.10311 179.354071 1751.136766 148.623128 1726.304053 148.623128C1727.270398 163.179787 1727.593811 179.027795 1727.593811 197.466361L1727.593811 305.182888 1727.593811 305.180923 1727.593811 305.180923ZM2038.07285 305.180923 2038.07285 148.621163 2009.692337 148.621163 2009.692337 305.180923 2038.07285 305.180923 2038.07285 305.180923ZM2023.883564 86.838896C2013.561609 86.838896 2006.144512 94.602709 2006.144512 104.630805 2006.144512 114.334589 2013.240143 122.098403 2023.238691 122.098403 2034.526985 122.098403 2041.622599 114.334589 2041.299191 104.630805 2041.299191 94.602709 2034.526985 86.838896 2023.883564 86.838896L2023.883564 86.838896 2023.883564 86.838896ZM1897.029897 141.507937C1853.811324 141.507937 1819.624942 172.23888 1819.624942 224.639709 1819.624942 274.129599 1852.200095 305.182888 1894.448428 305.182888 1932.182616 305.182888 1972.174866 279.951477 1972.174866 222.051116 1972.174866 174.178851 1941.857757 141.507937 1897.027955 141.507937L1897.027955 141.507937 1897.029897 141.507937 1897.029897 141.507937ZM1896.383064 162.855476C1929.924573 162.855476 1943.149462 196.495394 1943.149462 223.022084 1943.149462 258.281592 1922.830901 283.833383 1895.738191 283.833383 1868.002551 283.833383 1848.326921 257.957281 1848.326921 223.666775 1848.326921 193.906799 1862.839614 162.855476 1896.381122 162.855476L1896.381122 162.855476 1896.383064 162.855476 1896.383064 162.855476ZM2193.311391 75.517486 2193.311391 168.999699 2192.666518 168.999699C2185.570904 156.384976 2169.445023 145.063567 2145.578655 145.063567 2107.521059 145.063567 2075.269314 177.087825 2075.592722 229.164341 2075.592722 276.714261 2104.620067 308.738518 2142.354255 308.738518 2167.833812 308.738518 2186.86065 295.475172 2195.569452 278.007575L2196.214325 278.007575 2197.504088 305.178958 2222.983627 305.178958C2222.017271 294.504204 2221.693864 278.654231 2221.693864 264.744229L2221.693864 75.513555 2193.311391 75.513555 2193.311391 75.517486 2193.311391 75.517486ZM2193.311391 239.514783C2193.311391 244.043347 2192.987983 247.925253 2192.021645 251.80716 2186.86065 273.156664 2169.445023 285.771388 2150.416225 285.771388 2119.777651 285.771388 2104.29666 259.569009 2104.29666 227.869062 2104.29666 193.258178 2121.712287 167.380109 2151.063058 167.380109 2172.349899 167.380109 2187.828948 182.259113 2192.023587 200.375335 2192.989926 203.934895 2193.313351 208.785805 2193.313351 212.3434L2193.313351 239.514783 2193.311391 239.514783 2193.311391 239.514783ZM2450.358996 236.605809 2473.580473 305.180923 2503.576117 305.180923 2429.397504 87.163207 2395.532588 87.163207 2321.675441 305.180923 2350.702786 305.180923 2373.279391 236.605809 2450.360938 236.605809 2450.358996 236.605809 2450.358996 236.605809ZM2379.081357 214.609648 2400.368199 151.534067C2404.560896 138.270721 2408.108703 125.009341 2411.335044 112.070307L2411.979917 112.070307C2415.206276 124.686996 2418.430658 137.624065 2423.268228 151.856413L2444.555087 214.609648 2379.083299 214.609648 2379.081357 214.609648 2379.081357 214.609648ZM2533.569818 369.227472 2561.628866 369.227472 2561.628866 284.155729 2562.273757 284.155729C2571.627414 299.681391 2589.687932 308.738518 2610.3299 308.738518 2647.09579 308.738518 2681.284114 280.920479 2681.284114 224.637744 2681.284114 177.087825 2652.901658 145.065533 2615.16747 145.065533 2589.687932 145.065533 2571.304007 156.386942 2559.69423 175.470199L2559.049357 175.470199 2557.759594 148.623128 2532.280055 148.623128C2532.924928 163.502133 2533.569818 179.676418 2533.569818 199.730642L2533.569818 369.229437 2533.569818 369.227472 2533.569818 369.227472ZM2561.628866 214.609648C2561.628866 210.727742 2562.595222 206.523489 2563.563502 202.965892 2569.045945 181.616388 2587.106463 167.706386 2606.458668 167.706386 2636.454312 167.706386 2652.901658 194.553457 2652.901658 225.931057 2652.901658 261.835255 2635.486031 286.42001 2605.49233 286.42001 2585.173769 286.42001 2568.079607 272.83432 2562.920572 253.10244 2562.275699 249.542878 2561.630826 245.662937 2561.630826 241.458684L2561.630826 214.611613 2561.628866 214.609648 2561.628866 214.609648ZM2717.083666 369.227472 2745.142731 369.227472 2745.142731 284.155729 2745.787604 284.155729C2755.141279 299.681391 2773.201779 308.738518 2793.843765 308.738518 2830.609655 308.738518 2864.797978 280.920479 2864.797978 224.637744 2864.797978 177.087825 2836.415506 145.065533 2798.681318 145.065533 2773.201779 145.065533 2754.817854 156.386942 2743.208095 175.470199L2742.563204 175.470199 2741.273459 148.623128 2715.79392 148.623128C2716.438793 163.502133 2717.083666 179.676418 2717.083666 199.730642L2717.083666 369.229437 2717.083666 369.227472 2717.083666 369.227472ZM2745.142731 214.609648C2745.142731 210.727742 2746.109069 206.523489 2747.077367 202.965892 2752.55981 181.616388 2770.620328 167.706386 2789.972533 167.706386 2819.968177 167.706386 2836.415506 194.553457 2836.415506 225.931057 2836.415506 261.835255 2818.999879 286.42001 2789.006195 286.42001 2768.687634 286.42001 2751.593472 272.83432 2746.434436 253.10244 2745.789564 249.542878 2745.144673 245.662937 2745.144673 241.458684L2745.144673 214.611613 2745.142731 214.609648 2745.142731 214.609648ZM1192.533229 950.825564C1218.336182 954.060814 1253.165492 956.647442 1299.610417 956.647442 1384.109049 956.647442 1453.130584 936.591251 1496.347217 897.129456 1537.629214 859.605668 1564.077047 801.382961 1564.077047 723.748756 1564.077047 649.351767 1538.274094 597.595629 1496.347217 563.309054 1456.354978 529.669138 1400.236869 512.848197 1318.960684 512.848197 1271.872828 512.848197 1227.364486 516.081482 1192.533229 521.905325L1192.533229 950.823599 1192.533229 950.825564 1192.533229 950.825564ZM1271.872828 579.481374C1282.839667 576.89278 1300.900175 574.95281 1326.056301 574.95281 1425.391046 574.95281 1480.866225 629.942231 1480.221346 726.335384 1480.221346 836.96285 1418.942256 893.892241 1315.736288 893.245585 1299.610417 893.245585 1282.839667 893.245585 1271.872828 891.303648L1271.872828 579.479407 1271.872828 579.481374 1271.872828 579.481374ZM1772.423609 629.942231C1677.603026 629.942231 1609.873196 693.342123 1609.873196 796.852432 1609.873196 897.774149 1678.247904 959.234069 1767.262632 959.234069 1847.247113 959.234069 1928.521357 907.477933 1928.521357 791.677212 1928.521357 695.930718 1865.952503 629.942231 1772.421667 629.942231L1772.421667 629.942231 1772.423609 629.942231 1772.423609 629.942231ZM1770.487031 688.168868C1824.670508 688.168868 1846.60028 744.451603 1846.60028 793.621114 1846.60028 857.021006 1814.993426 901.659987 1769.840199 901.659987 1721.46259 901.659987 1691.790364 855.727692 1691.790364 794.916393 1691.790364 742.513599 1714.366972 688.1728 1770.485089 688.1728L1770.485089 688.1728 1770.487031 688.168868 1770.487031 688.168868ZM1954.969176 637.059388 2047.855139 952.116913 2122.033752 952.116913 2162.026002 824.02185C2171.056252 792.968559 2179.441629 761.91527 2185.89237 723.100134L2187.182115 723.100134C2194.277747 761.270579 2201.373361 791.028589 2211.048483 824.02185L2248.461206 952.116913 2321.996906 952.116913 2420.688719 637.057423 2342.638874 637.057423 2307.805661 776.796241C2299.420284 813.671408 2292.32467 847.959947 2287.165634 882.248487L2285.875889 882.248487C2278.780275 847.959947 2270.394898 813.673373 2260.719757 776.796241L2221.372398 637.057423 2156.868908 637.057423 2116.231786 780.678148C2107.201536 813.02475 2096.881523 847.959947 2090.42884 882.24652L2089.139076 882.24652C2083.335168 847.957982 2076.239554 813.671408 2069.141998 780.029527L2036.245363 637.057423 1954.971136 637.057423 1954.969176 637.059388 1954.969176 637.059388ZM2471.645837 952.116913 2551.630318 952.116913 2551.630318 767.092457C2551.630318 758.035329 2552.275209 748.331545 2554.85666 741.214388 2563.242037 717.278256 2585.173769 694.633472 2616.780641 694.633472 2659.997272 694.633472 2676.769969 728.922011 2676.769969 774.207649L2676.769969 952.114948 2756.109577 952.114948 2756.109577 765.148555C2756.109577 665.520153 2699.346573 629.9383 2644.518223 629.9383 2592.269383 629.9383 2558.083001 659.69631 2544.536647 684.281065L2542.60201 684.281065 2538.732738 637.055458 2469.06827 637.055458C2471.002906 664.226839 2471.647797 694.631507 2471.647797 730.861981L2471.647797 952.114948 2471.645837 952.116913 2471.645837 952.116913ZM2840.608203 952.116913 2920.592684 952.116913 2920.592684 492.792006 2840.608203 492.792006 2840.608203 952.116913 2840.608203 952.116913ZM3148.936333 629.942231C3054.115752 629.942231 2986.38592 693.342123 2986.38592 796.852432 2986.38592 897.774149 3054.758682 959.234069 3143.775356 959.234069 3223.759837 959.234069 3305.036023 907.477933 3305.036023 791.677212 3305.036023 695.930718 3242.467169 629.942231 3148.936333 629.942231L3148.936333 629.942231 3148.936333 629.942231ZM3147.001697 688.168868C3201.185174 688.168868 3223.116906 744.451603 3223.116906 793.621114 3223.116906 857.021006 3191.510051 901.659987 3146.356824 901.659987 3097.979215 901.659987 3068.306979 855.727692 3068.306979 794.916393 3068.306979 742.513599 3090.883602 688.1728 3147.001697 688.1728L3147.001697 688.1728 3147.001697 688.168868 3147.001697 688.168868ZM3618.523207 952.116913C3615.296847 932.707379 3614.007102 904.891305 3614.007102 876.424644L3614.007102 762.563893C3614.007102 693.988781 3584.979756 629.940266 3484.355249 629.940266 3434.685934 629.940266 3394.050754 643.525957 3370.827317 657.758305L3386.308308 709.514443C3407.595167 695.928751 3439.846912 686.224967 3470.806952 686.224967 3526.280174 686.224967 3534.665551 720.513506 3534.665551 741.214388L3534.665551 746.38961C3418.558111 745.742952 3348.250712 785.851403 3348.250712 865.427546 3348.250712 913.299811 3383.726839 959.234069 3446.942526 959.234069 3487.579648 959.234069 3520.478208 941.766472 3539.828471 917.183682L3541.763107 917.183682 3546.924103 952.118878 3618.523207 952.118878 3618.523207 952.116913 3618.523207 952.116913ZM3536.604089 841.491414C3536.604089 846.666634 3535.959199 853.137133 3534.024563 858.95901 3526.928949 881.601829 3503.707454 902.304678 3472.100599 902.304678 3446.944468 902.304678 3426.947372 888.07233 3426.947372 857.019041 3426.947372 809.146776 3480.485976 796.852432 3536.604089 798.147711L3536.604089 841.493379 3536.604089 841.491414 3536.604089 841.491414ZM3908.147906 492.792006 3908.147906 671.347927 3906.858143 671.347927C3892.666915 648.058453 3861.704934 629.944196 3818.488285 629.944196 3743.019908 629.944196 3677.22473 692.697432 3677.869603 798.147711 3677.869603 895.189486 3737.212098 959.236036 3812.037544 959.236036 3857.835644 959.236036 3895.893257 937.239873 3914.598647 902.306643L3915.888393 902.306643 3919.114734 952.120844 3990.068966 952.120844C3988.779202 930.773304 3987.489457 895.838109 3987.489457 863.48954L3987.489457 492.793971 3908.149848 492.793971 3908.147906 492.792006 3908.147906 492.792006ZM3908.147906 815.613345C3908.147906 824.023815 3907.503016 831.785663 3905.56838 838.902819 3897.827893 873.191359 3869.44542 895.185555 3837.195635 895.185555 3786.883372 895.185555 3758.500899 853.135168 3758.500899 795.557154 3758.500899 736.685824 3786.883372 691.400187 3837.840508 691.400187 3873.963467 691.400187 3899.119598 716.6316 3906.21327 747.682924 3907.503016 754.151458 3908.147906 761.91527 3908.147906 768.385771L3908.147906 815.611378 3908.147906 815.613345 3908.147906 815.613345Z"
                          p-id="450"></path>
                      </svg>
                    </a>
                    <a href="https://youhui.95516.com/hybrid_v3/html/help/download.html" target="_blank" class="link">
                      <svg class="icon"
                        style="width: 100px; height: 36px;vertical-align: middle;fill: currentColor;overflow: hidden;color:#108ee9;border: none;"
                        viewBox="0 0 3456 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7655">
                        <path
                          d="M3333.0432 1024H121.0624A121.344 121.344 0 0 1 0 902.8352V120.9856A121.1648 121.1648 0 0 1 121.0624 0h3211.9808C3399.7568 0 3456 54.272 3456 120.9856v781.8496c0 66.688-56.2432 121.1648-122.9568 121.1648z"
                          fill="#A6A6A6" p-id="7656"></path>
                        <path
                          d="M3431.2192 902.8608a98.048 98.048 0 0 1-98.1504 98.048H121.0624a98.176 98.176 0 0 1-98.2784-98.048V120.96A98.304 98.304 0 0 1 121.0624 22.784h3211.9808a98.1504 98.1504 0 0 1 98.1504 98.176l0.0256 781.9008z"
                          fill="" p-id="7657"></path>
                        <path
                          d="M771.2768 506.4704c-0.7424-82.5088 67.5584-122.6496 70.6816-124.5184-38.6816-56.3968-98.6368-64.1024-119.7056-64.7168-50.3552-5.2992-99.2 30.1312-124.8512 30.1312-26.1632 0-65.664-29.6192-108.2368-28.7488-54.784 0.8448-106.0352 32.5632-134.144 81.8176-58.0096 100.4288-14.7456 248.0128 40.832 329.1904 27.8016 39.7568 60.288 84.1472 102.8096 82.5856 41.6-1.7152 57.1392-26.5216 107.3408-26.5216 49.7408 0 64.3328 26.5216 107.6992 25.5232 44.6464-0.7168 72.7552-39.936 99.584-80.0512 32.128-45.568 45.0304-90.4448 45.5424-92.7488-1.0496-0.3584-86.7072-33.0496-87.552-131.9424zM689.3568 263.8336c22.3744-27.9808 37.6832-66.048 33.4336-104.6784-32.384 1.4336-72.8832 22.4-96.2048 49.7664-20.6336 24.1152-39.0656 63.6416-34.304 100.8128 36.3776 2.7136 73.728-18.3552 97.0752-45.9008z"
                          fill="#FFFFFF" p-id="7658"></path>
                        <path
                          d="M1373.312 806.5024h-58.1376l-31.8464-100.0704h-110.6944l-30.336 100.0704H1085.696l109.6704-340.6848h67.7376l110.208 340.6848z m-99.584-142.0544L1244.928 575.488c-3.0464-9.088-8.7552-30.4896-17.1776-64.1792h-1.024a2476.8 2476.8 0 0 1-16.1792 64.1792l-28.288 88.96h91.4688zM1655.3472 680.6528c0 41.7792-11.2896 74.8032-33.8688 99.0464-20.224 21.5808-45.3376 32.3584-75.3152 32.3584-32.3584 0-55.6032-11.6224-69.76-34.8672h-1.024v129.408H1420.8V641.7152c0-26.2656-0.6912-53.2224-2.0224-80.8704h48l3.0464 38.9376h1.024c18.2016-29.3376 45.824-43.9808 82.8928-43.9808 28.9792 0 53.1712 11.4432 72.5248 34.3552 19.4048 22.9376 29.0816 53.0944 29.0816 90.496z m-55.6032 1.9968c0-23.9104-5.376-43.6224-16.1792-59.136-11.8016-16.1792-27.648-24.2688-47.5136-24.2688-13.4656 0-25.7024 4.5056-36.6336 13.3888-10.9568 8.96-18.1248 20.6592-21.4784 35.1488-1.6896 6.7584-2.5344 12.288-2.5344 16.64v40.96c0 17.8688 5.4784 32.9472 16.4352 45.2608s25.1904 18.4576 42.7008 18.4576c20.5568 0 36.5568-7.936 48-23.7568 11.4688-15.8464 17.2032-36.736 17.2032-62.6944zM1937.8944 680.6528c0 41.7792-11.2896 74.8032-33.8944 99.0464-20.1984 21.5808-45.312 32.3584-75.2896 32.3584-32.3584 0-55.6032-11.6224-69.7344-34.8672h-1.024v129.408h-54.5792V641.7152c0-26.2656-0.6912-53.2224-2.0224-80.8704h48l3.0464 38.9376h1.024c18.176-29.3376 45.7984-43.9808 82.8928-43.9808 28.9536 0 53.1456 11.4432 72.5504 34.3552 19.328 22.9376 29.0304 53.0944 29.0304 90.496z m-55.6032 1.9968c0-23.9104-5.4016-43.6224-16.2048-59.136-11.8016-16.1792-27.5968-24.2688-47.488-24.2688a56.832 56.832 0 0 0-36.6592 13.3888c-10.9568 8.96-18.0992 20.6592-21.4528 35.1488-1.664 6.7584-2.5344 12.288-2.5344 16.64v40.96c0 17.8688 5.4784 32.9472 16.384 45.2608 10.9568 12.288 25.1904 18.4576 42.752 18.4576 20.5568 0 36.5568-7.936 48-23.7568 11.4688-15.8464 17.2032-36.736 17.2032-62.6944zM2253.7984 710.9632c0 28.9792-10.0608 52.5568-30.2592 70.7584-22.1952 19.8912-53.0944 29.824-92.8 29.824-36.6592 0-66.048-7.0656-88.2944-21.2224l12.6464-45.4912c23.9616 14.4896 50.2528 21.76 78.8992 21.76 20.5568 0 36.5568-4.6592 48.0512-13.9264 11.4432-9.2672 17.152-21.7088 17.152-37.2224 0-13.824-4.7104-25.472-14.1568-34.9184-9.3952-9.4464-25.088-18.2272-47.0016-26.3424-59.648-22.2464-89.4464-54.8352-89.4464-97.6896 0-28.0064 10.4448-50.9696 31.36-68.8384 20.8384-17.8944 48.64-26.8288 83.4048-26.8288 31.0016 0 56.7552 5.4016 77.312 16.1792l-13.6448 44.4928c-19.2-10.4448-40.9088-15.6672-65.2032-15.6672-19.2 0-34.2016 4.736-44.9536 14.1568a40.448 40.448 0 0 0-13.6448 30.848c0 13.4656 5.1968 24.6016 15.6416 33.3568 9.088 8.0896 25.6 16.8448 49.5616 26.2912 29.312 11.8016 50.8416 25.6 64.6912 41.4208 13.7984 15.7696 20.6848 35.5072 20.6848 59.0592zM2434.2528 601.8048h-60.16v119.2704c0 30.336 10.5984 45.4912 31.8464 45.4912 9.7536 0 17.8432-0.8448 24.2432-2.5344l1.5104 41.4464c-10.752 4.0192-24.9088 6.0416-42.4448 6.0416-21.5552 0-38.4-6.5792-50.56-19.712-12.1088-13.1584-18.2016-35.2256-18.2016-66.2272v-123.8272h-35.84v-40.96h35.84v-44.9792l53.6064-16.1792v61.1584h60.16v41.0112zM2705.6896 681.6512c0 37.76-10.8032 68.7616-32.3584 93.0048-22.6048 24.96-52.608 37.4016-90.0096 37.4016-36.0448 0-64.7424-11.9552-86.144-35.8656s-32.1024-54.0928-32.1024-90.4704c0-38.0672 11.008-69.248 33.1008-93.4912 22.0416-24.2688 51.7888-36.4032 89.1904-36.4032 36.0448 0 65.0496 11.9552 86.9376 35.8912 20.9408 23.2192 31.3856 53.1968 31.3856 89.9328z m-56.6272 1.7664c0-22.656-4.8384-42.0864-14.6432-58.2912-11.4432-19.6096-27.8016-29.3888-48.9984-29.3888-21.9392 0-38.6048 9.8048-50.048 29.3888-9.8048 16.2304-14.6432 35.968-14.6432 59.3152 0 22.656 4.8384 42.0864 14.6432 58.2656 11.8016 19.6096 28.288 29.3888 49.5616 29.3888 20.8384 0 37.1968-9.984 48.9984-29.9008 10.0608-16.512 15.1296-36.1472 15.1296-58.7776zM2883.0976 608.8448a94.7712 94.7712 0 0 0-17.2032-1.5104c-19.2 0-34.048 7.2448-44.4928 21.76-9.088 12.8-13.6448 28.9792-13.6448 48.512v128.896h-54.5536l0.512-168.2944c0-28.3136-0.6912-54.0928-2.048-77.3376h47.5392l1.9968 47.0016h1.5104c5.76-16.1536 14.848-29.1584 27.2896-38.912a65.9968 65.9968 0 0 1 39.4496-13.1584c5.0432 0 9.6 0.3584 13.6448 0.9984v52.0448zM3127.1936 672.0512a128 128 0 0 1-1.9968 24.7552h-163.7376c0.64 24.2688 8.5504 42.8288 23.7568 55.6288 13.7984 11.4432 31.6416 17.1776 53.5552 17.1776 24.2432 0 46.3616-3.8656 66.2528-11.6224l8.5504 37.888c-23.2448 10.1376-50.688 15.1808-82.3552 15.1808-38.0928 0-67.9936-11.2128-89.7536-33.6128-21.7088-22.4-32.5888-52.48-32.5888-90.2144 0-37.0432 10.112-67.8912 30.3616-92.4928 21.1968-26.2656 49.8432-39.3984 85.888-39.3984 35.4048 0 62.208 13.1328 80.4096 39.3984 14.4128 20.864 21.6576 46.6688 21.6576 77.312z m-52.0448-14.1568c0.3584-16.1792-3.2-30.1568-10.5984-41.9584-9.4464-15.1808-23.9616-22.7584-43.4944-22.7584-17.8432 0-32.3584 7.3984-43.4432 22.2464-9.088 11.8016-14.4896 25.9584-16.1536 42.4448h113.6896z"
                          fill="#FFFFFF" p-id="7659"></path>
                        <path
                          d="M1255.68 256.2304c0 30.1312-9.0368 52.8128-27.0848 68.0448-16.7168 14.0544-40.4736 21.0944-71.2448 21.0944-15.2576 0-28.3136-0.6656-39.2448-1.9968V178.7392c14.2592-2.304 29.6192-3.4816 46.208-3.4816 29.312 0 51.4048 6.3744 66.304 19.1232 16.6912 14.4128 25.0624 35.0208 25.0624 61.8496z m-28.288 0.7424c0-19.5328-5.1712-34.5088-15.5136-44.9536-10.3424-10.4192-25.4464-15.6416-45.3376-15.6416-8.448 0-15.6416 0.5632-21.6064 1.7408v125.1584c3.3024 0.512 9.344 0.7424 18.1248 0.7424 20.5312 0 36.3776-5.7088 47.5392-17.1264s16.7936-28.0576 16.7936-49.92zM1405.6704 282.5472c0 18.56-5.2992 33.7664-15.8976 45.696-11.1104 12.2624-25.8304 18.3808-44.2112 18.3808-17.7152 0-31.8208-5.8624-42.3424-17.6384-10.496-11.7504-15.744-26.5728-15.744-44.4416 0-18.688 5.4016-34.0224 16.256-45.9264s25.4464-17.8688 43.8272-17.8688c17.7152 0 31.9488 5.8624 42.7264 17.6128 10.24 11.4176 15.3856 26.1632 15.3856 44.1856z m-27.8272 0.8704c0-11.136-2.4064-20.6848-7.1936-28.6464-5.632-9.6256-13.6448-14.4384-24.064-14.4384-10.7776 0-18.9696 4.8128-24.6016 14.4384-4.8128 7.9616-7.1936 17.664-7.1936 29.1328 0 11.136 2.4064 20.6848 7.1936 28.6464 5.8112 9.6256 13.9008 14.4384 24.3456 14.4384 10.24 0 18.2784-4.8896 24.064-14.6944 4.9664-8.1152 7.4496-17.7408 7.4496-28.8768zM1606.784 223.2064l-37.76 120.6784h-24.576l-15.6416-52.4032a392.192 392.192 0 0 1-9.7024-38.9888h-0.4864a285.44 285.44 0 0 1-9.7024 38.9888l-16.6144 52.4032h-24.8576l-35.5072-120.6784h27.5712l13.6448 57.3696c3.3024 13.568 6.016 26.496 8.192 38.7328h0.4864c1.9968-10.0864 5.2992-22.9376 9.9584-38.4768l17.1264-57.6h21.8624l16.4096 56.3712c3.968 13.7472 7.1936 26.9824 9.6768 39.7312h0.7424c1.8176-12.416 4.5568-25.6512 8.192-39.7312l14.6432-56.3712h26.3424zM1745.8688 343.8848H1719.04v-69.12c0-21.2992-8.0896-31.9488-24.32-31.9488a24.2176 24.2176 0 0 0-19.3792 8.7808 31.1552 31.1552 0 0 0-7.4496 20.6848v71.5776h-26.8288v-86.1696c0-10.5984-0.3328-22.0928-0.9728-34.5344h23.5776l1.2544 18.8672h0.7424c3.1232-5.8624 7.7824-10.7008 13.9008-14.5664 7.2704-4.5056 15.4112-6.784 24.32-6.784 11.264 0 20.6336 3.6352 28.0832 10.9312 9.2672 8.9344 13.9008 22.272 13.9008 39.9872v72.2944zM1819.8528 343.8848h-26.8032V167.8336h26.8032v176.0512zM1977.8048 282.5472c0 18.56-5.2992 33.7664-15.8976 45.696-11.1104 12.2624-25.856 18.3808-44.2112 18.3808-17.7408 0-31.8464-5.8624-42.3424-17.6384-10.496-11.7504-15.744-26.5728-15.744-44.4416 0-18.688 5.4016-34.0224 16.256-45.9264s25.4464-17.8688 43.8016-17.8688c17.7408 0 31.9488 5.8624 42.752 17.6128 10.24 11.4176 15.3856 26.1632 15.3856 44.1856z m-27.8528 0.8704c0-11.136-2.4064-20.6848-7.1936-28.6464-5.6064-9.6256-13.6448-14.4384-24.0384-14.4384-10.8032 0-18.9952 4.8128-24.6016 14.4384-4.8128 7.9616-7.1936 17.664-7.1936 29.1328 0 11.136 2.4064 20.6848 7.1936 28.6464 5.8112 9.6256 13.9008 14.4384 24.3456 14.4384 10.24 0 18.2528-4.8896 24.0384-14.6944 4.992-8.1152 7.4496-17.7408 7.4496-28.8768zM2107.648 343.8848h-24.0896l-1.9968-13.9008h-0.7424c-8.2432 11.0848-19.9936 16.64-35.2512 16.64-11.392 0-20.608-3.6608-27.5456-10.9312a34.2784 34.2784 0 0 1-9.4464-24.576c0-14.7456 6.144-25.984 18.5088-33.7664 12.3392-7.7824 29.696-11.5968 52.0448-11.4176V263.68c0-15.8976-8.3456-23.8336-25.0624-23.8336-11.904 0-22.4 2.9952-31.4624 8.9344l-5.4528-17.6128c11.2128-6.9376 25.0624-10.4192 41.3952-10.4192 31.5392 0 47.36 16.64 47.36 49.92v44.4416c0 12.0576 0.5888 21.6576 1.7408 28.7744z m-27.8528-41.472v-18.6112c-29.5936-0.512-44.3904 7.6032-44.3904 24.32 0 6.2976 1.6896 11.008 5.1456 14.1568a18.7648 18.7648 0 0 0 13.1072 4.7104c5.888 0 11.392-1.8688 16.4096-5.5808a22.8608 22.8608 0 0 0 9.728-18.9952zM2260.096 343.8848h-23.808l-1.2544-19.3792h-0.7424c-7.6032 14.7456-20.5568 22.1184-38.7584 22.1184-14.5408 0-26.6496-5.7088-36.2496-17.1264s-14.3872-26.24-14.3872-44.4416c0-19.5328 5.1968-35.3536 15.6416-47.4368 10.112-11.264 22.5024-16.896 37.248-16.896 16.2048 0 27.5456 5.4528 33.9968 16.384h0.512V167.8336h26.8544v143.5392c0 11.7504 0.3072 22.5792 0.9472 32.512z m-27.8016-50.8928v-20.1216a30.5664 30.5664 0 0 0-10.4448-24.704 26.368 26.368 0 0 0-17.9456-6.5792c-10.0096 0-17.8432 3.968-23.6032 11.9296-5.7088 7.9616-8.6016 18.1248-8.6016 30.5408 0 11.9296 2.7392 21.6064 8.2432 29.056 5.8112 7.936 13.6448 11.904 23.4496 11.904 8.8064 0 15.8464-3.3024 21.1968-9.9328 5.1712-6.1184 7.7056-13.4912 7.7056-22.0928zM2489.5488 282.5472c0 18.56-5.2992 33.7664-15.8976 45.696-11.1104 12.2624-25.8048 18.3808-44.2112 18.3808-17.6896 0-31.7952-5.8624-42.3424-17.6384-10.496-11.7504-15.744-26.5728-15.744-44.4416 0-18.688 5.4016-34.0224 16.256-45.9264s25.4464-17.8688 43.8528-17.8688c17.6896 0 31.9488 5.8624 42.7008 17.6128 10.24 11.4176 15.3856 26.1632 15.3856 44.1856z m-27.8016 0.8704c0-11.136-2.4064-20.6848-7.1936-28.6464-5.6576-9.6256-13.6448-14.4384-24.0896-14.4384-10.752 0-18.944 4.8128-24.6016 14.4384-4.8128 7.9616-7.1936 17.664-7.1936 29.1328 0 11.136 2.4064 20.6848 7.1936 28.6464 5.8112 9.6256 13.9008 14.4384 24.3456 14.4384 10.24 0 18.304-4.8896 24.0896-14.6944 4.9408-8.1152 7.4496-17.7408 7.4496-28.8768zM2633.8048 343.8848h-26.8032v-69.12c0-21.2992-8.0896-31.9488-24.3456-31.9488-7.9616 0-14.3872 2.9184-19.3536 8.7808s-7.4496 12.7744-7.4496 20.6848v71.5776h-26.8544v-86.1696c0-10.5984-0.3072-22.0928-0.9472-34.5344h23.552l1.2544 18.8672h0.7424a39.168 39.168 0 0 1 13.9008-14.5664c7.296-4.5056 15.4112-6.784 24.3456-6.784 11.2384 0 20.608 3.6352 28.0576 10.9312 9.2928 8.9344 13.9008 22.272 13.9008 39.9872v72.2944zM2814.3616 243.3024h-29.5424v58.624c0 14.8992 5.248 22.3488 15.6416 22.3488 4.8128 0 8.8064-0.4096 11.9552-1.2544l0.6912 20.352c-5.2992 1.9968-12.2624 2.9952-20.8384 2.9952-10.5984 0-18.8416-3.2256-24.8064-9.6768-5.9904-6.4512-8.96-17.3056-8.96-32.5376V243.3024h-17.6384v-20.096h17.6384v-22.1184l26.2912-7.936v30.0288h29.5424v20.1216zM2956.3904 343.8848h-26.8544v-68.608c0-21.632-8.0896-32.4608-24.2944-32.4608-12.4416 0-20.9408 6.272-25.6 18.816a33.7152 33.7152 0 0 0-1.2544 9.6512v72.576h-26.8032V167.8336h26.8032v72.7296h0.512c8.448-13.2352 20.5568-19.84 36.2496-19.84 11.1104 0 20.3008 3.6352 27.5968 10.9312 9.088 9.088 13.6448 22.6048 13.6448 40.4736v71.7568zM3102.8992 277.8368c0 4.8128-0.3584 8.8576-0.9984 12.16h-80.4608c0.3584 11.9296 4.1984 21.0176 11.648 27.3152 6.8096 5.632 15.5904 8.448 26.3424 8.448 11.904 0 22.7584-1.8944 32.5376-5.7088l4.1984 18.6368c-11.4432 4.9664-24.9088 7.4496-40.4992 7.4496-18.688 0-33.408-5.504-44.0576-16.512-10.7008-11.008-16-25.7792-16-44.3136 0-18.2016 4.9408-33.3568 14.8992-45.44 10.3936-12.9024 24.448-19.3536 42.1888-19.3536 17.3568 0 30.5408 6.4512 39.4496 19.3536 7.1936 10.24 10.752 22.912 10.752 37.9648z m-25.6-6.9376a36.1216 36.1216 0 0 0-5.1968-20.608c-4.6592-7.4496-11.7504-11.1872-21.3504-11.1872a25.472 25.472 0 0 0-21.3504 10.9312 40.6016 40.6016 0 0 0-7.9616 20.864h55.8592z"
                          fill="#FFFFFF" p-id="7660"></path>
                      </svg>
                    </a>

                    <a href="#" target="_blank" class="link">
                      <span class="cf_button">官方教程</span>
                    </a>
                  </div>
                </div>
              </div>
              <p class="card-text" style="font-size:18px;display:block;width:97%;margin:10px auto 0;color: #000;">付款方法
              </p>
              <p class="card-text" style="font-size:15px;display:block;width:97%;margin:5px auto;">
                &nbsp;&nbsp;&nbsp;&nbsp;
                <strong>方法一：</strong>请使用手机截屏功能截图，打开云闪付APP，使用扫码功能从相册中选取图片，扫描截图，完成支付。
                <br>
                <br>&nbsp;&nbsp;&nbsp;&nbsp;
                <strong>方法二：</strong>使用另一台手机，打开云闪付APP扫码功能，扫描当前页二维码，完成支付。</span>

                <br />
              </p>
              <br />
              <p
                style="color: #f35626;width:96%;padding: 5px 5px;border: 1px solid #f35626;margin:-15px auto 20px;font-size:0.96875rem;">
                <strong>特别提醒：</strong>
                <br>&nbsp;&nbsp;每张二维码的订单号唯一，扫码前请核对该订单是否已支付！重复使用将无法到账！
              </p>

              <details style="width:100%;margin: -10px auto 1px;padding-bottom:10px;border-bottom: 1px solid #eee;">
                <summary
                  style="border-radius: .3rem;width:100%;height:40px;line-height:40px;text-align: left;padding-left:10px;color: #fff;background: #17a2b8; border-color: #17a2b8;box-sizing: border-box;">
                  查看图文教程请点击这里
                </summary>
                <div style="width:100%;height:auto;margin:20px auto 30px;">
                  <div>
                    <span style="background:#f35626;color:#fff;padding:5px 10px;border-radius: 4px;">云闪付APP扫码支付：</span>
                    <p style="margin: 18px 0 0;font-size: 15px;padding-left:2%;">第
                      <strong style="color:#2794fa;"> 1 </strong>步：打开云闪付APP，点击左下角“<span style="color:#f35626;">收付款</span>”按钮</p>
                    <div style="width:96%;height:160px;margin:8px auto;position: relative;border:1px solid #C0C4CC;">
                      <div
                        style="left:15px;bottom:5px;position:absolute;width:100px;height:60px;border:2px solid #f35626;border-radius:100%;z-index:10;">
                      </div>
                      <div style="width:100%;height:100px;position: absolute;left: 0;bottom: 0;z-index:9;">
                        <p
                          style="height: 50px;background: #f5f7fa;margin:0;text-align: center;font-size:15px;filter: blur(1px);">
                          <svg class="icon"
                            style="width: 40px; height: 45px;vertical-align: middle;fill: currentColor;overflow: hidden;font-weight:900;margin-top:-10px;"
                            viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9004">
                            <path
                              d="M939.6 634.2c-2.9 0-5.9-0.6-8.7-2L512 432.7 93.1 632.2c-10.1 4.8-22.3 0.5-27.1-9.6-4.8-10.1-0.5-22.3 9.6-27.1l427.6-203.6c5.5-2.6 12-2.6 17.5 0l427.6 203.6c10.2 4.9 14.5 17 9.6 27.1-3.4 7.3-10.7 11.6-18.3 11.6z"
                              fill="#333333" p-id="9005"></path>
                          </svg>
                          <br>
                          <span style="display:block;margin-top:-14px;">上滑「扫一扫」</span>
                        </p>
                        <p style="height: 50px;display: flex;justify-content: space-around;margin:0;">
                          <span style="text-align: center;color:#f00;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1484">
                              <path
                                d="M950.238554 146.169057 73.118809 146.169057c-40.364313 0-73.097832 32.733519-73.097832 73.088622l0 584.745814c0 40.364313 32.733519 73.098855 73.097832 73.098855l877.119745 0c40.364313 0 73.088622-32.733519 73.088622-73.098855L1023.327176 219.256656C1023.327176 178.901553 990.602867 146.169057 950.238554 146.169057zM271.59621 741.337245l-70.579475 0L201.016735 263.371386l70.579475 0L271.59621 741.337245zM359.806227 741.337245l-46.511313 0L313.294914 263.371386l46.511313 0L359.806227 741.337245zM533.031501 741.337245 430.375469 741.337245 430.375469 263.371386l102.655008 0L533.030477 741.337245zM645.308657 741.337245l-68.971861 0L576.336796 263.371386l68.971861 0L645.308657 741.337245zM807.303144 741.337245l-43.305295 0L763.997849 263.371386l43.305295 0L807.303144 741.337245z"
                                p-id="1485"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">收付款</span>
                          </span>
                          <span style="text-align: center;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="14429">
                              <path d="M0 0h1024v1024H0z" fill-opacity="0" p-id="14430">
                              </path>
                              <path
                                d="M429.44 960a96 96 0 0 0 67.84-28.16l433.28-433.28A96.64 96.64 0 0 0 960 423.04l-19.2-249.6a96 96 0 0 0-88.96-88.96L601.6 64a96 96 0 0 0-75.52 28.16L92.8 526.72a96.64 96.64 0 0 0 0 136.32l268.8 268.8a96 96 0 0 0 67.84 28.16zM593.92 103.68h4.48l249.6 19.2a57.6 57.6 0 0 1 53.76 53.76l19.2 249.6a58.24 58.24 0 0 1-16.64 45.44l-433.92 433.28a59.52 59.52 0 0 1-82.56 0l-268.8-268.8a58.24 58.24 0 0 1 0-82.56l433.92-432.64a57.6 57.6 0 0 1 40.96-17.28z m128 274.56a76.16 76.16 0 1 0-53.76-22.4 75.52 75.52 0 0 0 53.76 22.4z m0-114.56a38.4 38.4 0 0 1 26.88 64 39.04 39.04 0 0 1-53.76 0 38.4 38.4 0 0 1 26.88-64z"
                                p-id="14431"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">享优惠</span>
                          </span>
                          <span style="text-align: center;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8700">
                              <path
                                d="M938.3424 204.8H85.7088A34.6112 34.6112 0 0 0 51.2 239.3088v545.3824c0 19.0464 15.4624 34.5088 34.5088 34.5088h852.5824c19.1488 0 34.5088-15.36 34.5088-34.5088V239.3088c0-19.0976-15.36-34.5088-34.4576-34.5088z m0-51.2C985.7536 153.6 1024 192 1024 239.3088v545.3824C1024 832 985.7536 870.4 938.2912 870.4H85.7088A85.7088 85.7088 0 0 1 0 784.6912V239.3088C0 192.0512 38.4 153.6 85.7088 153.6h852.6336zM51.5584 418.304l921.6-1.4848v-51.2l-921.6 1.4848v51.2z"
                                p-id="8701"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">卡管理</span>
                          </span>
                        </p>
                      </div>
                    </div>
                    <!--  -->
                    <p style="margin: 18px 0 0;font-size: 15px;padding-left:2%;">第
                      <strong style="color:#2794fa;"> 2 </strong>步：按住并上滑“
                      <span style="color:#f35626;">上滑「扫一扫」</span>”按钮</p>
                    <div style="width:96%;height:160px;margin:8px auto;position: relative;border:1px solid #C0C4CC;">
                      <div
                        style="left:30%;bottom:40px;position:absolute;width:150px;height:80px;border:2px solid #f35626;border-radius:100%;z-index:10;">
                      </div>
                      <div style="width:100%;height:100px;position: absolute;left: 0;bottom: 0;">
                        <p style="height: 50px;background: #f5f7fa;margin:0;text-align: center;font-size:15px;">
                          <svg class="icon"
                            style="width: 40px; height: 45px;vertical-align: middle;fill: currentColor;overflow: hidden;font-weight:900;margin-top:-10px;"
                            viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9004">
                            <path
                              d="M939.6 634.2c-2.9 0-5.9-0.6-8.7-2L512 432.7 93.1 632.2c-10.1 4.8-22.3 0.5-27.1-9.6-4.8-10.1-0.5-22.3 9.6-27.1l427.6-203.6c5.5-2.6 12-2.6 17.5 0l427.6 203.6c10.2 4.9 14.5 17 9.6 27.1-3.4 7.3-10.7 11.6-18.3 11.6z"
                              fill="#333333" p-id="9005"></path>
                          </svg>
                          <br>
                          <span style="display:block;margin-top:-14px;">上滑「扫一扫」</span>
                        </p>
                        <p style="height: 50px;display: flex;justify-content: space-around;margin:0;filter: blur(1px);">
                          <span style="text-align: center;color:#f00;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1484">
                              <path
                                d="M950.238554 146.169057 73.118809 146.169057c-40.364313 0-73.097832 32.733519-73.097832 73.088622l0 584.745814c0 40.364313 32.733519 73.098855 73.097832 73.098855l877.119745 0c40.364313 0 73.088622-32.733519 73.088622-73.098855L1023.327176 219.256656C1023.327176 178.901553 990.602867 146.169057 950.238554 146.169057zM271.59621 741.337245l-70.579475 0L201.016735 263.371386l70.579475 0L271.59621 741.337245zM359.806227 741.337245l-46.511313 0L313.294914 263.371386l46.511313 0L359.806227 741.337245zM533.031501 741.337245 430.375469 741.337245 430.375469 263.371386l102.655008 0L533.030477 741.337245zM645.308657 741.337245l-68.971861 0L576.336796 263.371386l68.971861 0L645.308657 741.337245zM807.303144 741.337245l-43.305295 0L763.997849 263.371386l43.305295 0L807.303144 741.337245z"
                                p-id="1485"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">收付款</span>
                          </span>
                          <span style="text-align: center;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="14429">
                              <path d="M0 0h1024v1024H0z" fill-opacity="0" p-id="14430">
                              </path>
                              <path
                                d="M429.44 960a96 96 0 0 0 67.84-28.16l433.28-433.28A96.64 96.64 0 0 0 960 423.04l-19.2-249.6a96 96 0 0 0-88.96-88.96L601.6 64a96 96 0 0 0-75.52 28.16L92.8 526.72a96.64 96.64 0 0 0 0 136.32l268.8 268.8a96 96 0 0 0 67.84 28.16zM593.92 103.68h4.48l249.6 19.2a57.6 57.6 0 0 1 53.76 53.76l19.2 249.6a58.24 58.24 0 0 1-16.64 45.44l-433.92 433.28a59.52 59.52 0 0 1-82.56 0l-268.8-268.8a58.24 58.24 0 0 1 0-82.56l433.92-432.64a57.6 57.6 0 0 1 40.96-17.28z m128 274.56a76.16 76.16 0 1 0-53.76-22.4 75.52 75.52 0 0 0 53.76 22.4z m0-114.56a38.4 38.4 0 0 1 26.88 64 39.04 39.04 0 0 1-53.76 0 38.4 38.4 0 0 1 26.88-64z"
                                p-id="14431"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">享优惠</span>
                          </span>
                          <span style="text-align: center;">
                            <svg class="icon"
                              style="width: 20px; height: 20px;vertical-align: middle;fill: currentColor;overflow: hidden;"
                              viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8700">
                              <path
                                d="M938.3424 204.8H85.7088A34.6112 34.6112 0 0 0 51.2 239.3088v545.3824c0 19.0464 15.4624 34.5088 34.5088 34.5088h852.5824c19.1488 0 34.5088-15.36 34.5088-34.5088V239.3088c0-19.0976-15.36-34.5088-34.4576-34.5088z m0-51.2C985.7536 153.6 1024 192 1024 239.3088v545.3824C1024 832 985.7536 870.4 938.2912 870.4H85.7088A85.7088 85.7088 0 0 1 0 784.6912V239.3088C0 192.0512 38.4 153.6 85.7088 153.6h852.6336zM51.5584 418.304l921.6-1.4848v-51.2l-921.6 1.4848v51.2z"
                                p-id="8701"></path>
                            </svg>
                            <br>
                            <span style="font-size:15px;">卡管理</span>
                          </span>
                        </p>
                      </div>
                    </div>
                    <!--  -->
                    <p style="margin: 18px 0 0;font-size: 15px;padding-left:2%;">第
                      <strong style="color:#2794fa;"> 3 </strong>步：点击“<span style="color:#f35626;">打开相册</span>”，从相册中选择二维码</p>
                    <div style="width:96%;height:220px;margin:8px auto;position: relative;background:rgba(0,0,0,.7);">
                      <div
                        style="left:18%;bottom:3px;position:absolute;width:120px;height:60px;border:2px solid #f35626;border-radius:100%;z-index:10;">
                      </div>
                      <div
                        style="width:60%;height:120px;margin:0 auto;background:rgba(255,255,255,.6);border-bottom-left-radius: 4px;border-bottom-right-radius: 4px;text-align:center;line-height:120px;color:#808080;border:1px solid #eee;border-top:0;">
                        二维码扫码区</div>
                      <div style="width:100%;height:40px;position: absolute;left: 0;bottom: 0;">
                        <p
                          style="width:60%;height: 40px;margin:0 auto;display: flex;justify-content: space-around;color:#fff;">
                          <span style="text-align: center;">
                            <span style="font-size:15px;">打开相册</span>
                          </span>
                          <span style="text-align: center;">
                            <span style="font-size:15px;">使用说明</span>
                          </span>
                        </p>
                      </div>
                    </div>
                    <!--  -->
                    <p style="margin: 18px 0 0;font-size: 15px;padding-left:2%;">第
                      <strong style="color:#2794fa;"> 4 </strong>步：核对订单信息，完成支付</p>
                    <div style="width:96%;height:160px;margin:8px auto;position: relative;border:1px solid #C0C4CC;">
                      <div
                        style="left:30%;bottom:2px;position:absolute;width:150px;height:80px;border:2px solid #f35626;border-radius:100%;z-index:10;">
                      </div>
                      <div style="width:100%;height:140px;position: absolute;left: 0;bottom: 0;z-index:9;">
                        <p
                          style="width:90%;margin:0 auto 5px;height: 50px;border:1px solid #f5f7fa;text-align: center;font-size:15px;border-radius: 4px;font-size:20px;color:#888;">
                          ¥
                          <span
                            style="font-size:45px;margin-top:-2px;display: inline-block;color:#000;margin-left:12px;"
                            id="money">99.00</span>
                        </p>
                        <p style="font-size:15px;color:#808080;text-align:center;" id="info">
                          5g5fuyjTUAk54KL(订单信息)</p>
                        <p
                          style="height: 36px;width:90%;margin:7px auto;text-align:center;line-height:36px;background:#409EFF;color:#fff;border-radius: 4px;">
                          确认付款</p>
                      </div>
                    </div>
                  </div>
                </div>
              </details>
              <!-- developer -->
              <div class="goldfish">
                <span class="logo">
                  <svg class="icon user-avatar"
                    style="width: auto; height: 20px;color:#f60;vertical-align: middle;fill: currentColor;overflow: hidden;"
                    viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1270">
                    <path
                      d="M348.465 456.993a130.076 130.076 0 0 0-20.354-16.618 130.062 130.062 0 0 0-20.352 16.618c-26.858 26.854-39.332 62.564-37.666 97.728l109.018 37.306c15.738-46.324 2.986-101.402-30.646-135.034zM102.715 744.61c-31.293 1.322-62.203 13.782-86.097 37.675A130.098 130.098 0 0 0 0 802.638a130.076 130.076 0 0 0 16.62 20.352c48.044 48.046 124.386 50.364 175.276 7.142l-89.18-85.523z"
                      fill="#FFCF65" p-id="1271"></path>
                    <path
                      d="M341.78 565.17c-100.132-30.843-154.716-79.679-154.716-169.099 0-82.108 61.182-149.7 140.41-160.223-0.007-0.282-0.039-0.556-0.043-0.834C158.65 246.108 25.158 386.389 25.158 557.977c0 162.564 205.07 421.567 437.625 274.765 8.59-5.422 14.278-14.396 15.986-24.406 8.32-48.8 19.156-195.07-136.99-243.165z"
                      fill="#FFB450" p-id="1272"></path>
                    <path
                      d="M478.777 808.258c7.088-41.74 15.91-154.403-78.876-216.35-68.93 48.643-32.762 104.3-70.026 141.565-44.532 44.53-83.993 24.756-120.56 108.987l-1.445 3.476c72.925 39.198 161.657 45.638 254.837-13.146 8.626-5.442 14.362-14.478 16.07-24.532z"
                      fill="#FF4B4B" p-id="1273"></path>
                    <path
                      d="M372.943 234.156c23.29-15.468 38.658-41.906 38.658-71.958 0-4.524-0.452-8.932-1.122-13.268-4.336-0.67-8.746-1.122-13.268-1.122-39.116 0-71.966 26.066-82.554 61.742-1.792 6.042-6.202 10.964-12.244 12.75C132.67 272.44 81.672 382.897 91.77 472.13c12.518 110.57 119.41 211.105 173.857 240.355a18.208 18.208 0 0 0 8.642 2.178 18.286 18.286 0 0 0 8.68-34.394c-50-26.858-144.481-120.75-154.82-212.25-8.133-71.846 33.667-160.764 170.338-206.176 8.398-2.79 17.378 1.524 21.19 9.514 13.856 29.026 43.238 49.154 77.556 49.154 4.524 0 8.932-0.452 13.268-1.124 0.67-4.336 1.122-8.746 1.122-13.268-0.004-30.058-15.37-56.495-38.66-71.963zM292.571 824.364a18.286 18.286 0 1 0 36.572 0 18.286 18.286 0 1 0-36.572 0zM402.285 696.365a18.286 18.286 0 1 0 36.572 0 18.286 18.286 0 1 0-36.572 0zM675.535 567.009a130.076 130.076 0 0 0 20.354 16.618 130.062 130.062 0 0 0 20.352-16.618c26.858-26.854 39.332-62.564 37.666-97.728l-109.018-37.306c-15.74 46.324-2.986 101.402 30.646 135.034z m245.747-287.614c31.294-1.322 62.204-13.782 86.098-37.675a130.09 130.09 0 0 0 16.62-20.354 130.076 130.076 0 0 0-16.62-20.352c-48.044-48.046-124.384-50.364-175.276-7.142l89.178 85.523z"
                      fill="#FFCF65" p-id="1274"></path>
                    <path
                      d="M682.219 458.833c100.131 30.844 154.715 79.68 154.715 169.1 0 82.108-61.182 149.7-140.41 160.223 0.007 0.282 0.039 0.554 0.043 0.834 168.78-11.098 302.273-151.377 302.273-322.965 0-162.564-205.07-421.567-437.625-274.765-8.59 5.422-14.278 14.396-15.986 24.406-8.32 48.8-19.156 195.071 136.99 243.167z"
                      fill="#FFB450" p-id="1275"></path>
                    <path
                      d="M545.225 215.746c-7.088 41.74-15.91 154.403 78.876 216.35 68.93-48.643 32.762-104.3 70.026-141.565 44.532-44.53 83.993-24.756 120.56-108.987l1.445-3.476c-72.925-39.198-161.657-45.638-254.837 13.146-8.628 5.438-14.364 14.474-16.07 24.532z"
                      fill="#FF4B4B" p-id="1276"></path>
                    <path
                      d="M651.057 789.846c-23.29 15.468-38.658 41.908-38.658 71.958 0 4.524 0.452 8.932 1.122 13.268 4.336 0.67 8.746 1.122 13.268 1.122 39.116 0 71.966-26.066 82.554-61.742 1.792-6.042 6.202-10.964 12.244-12.75 169.743-50.14 220.741-160.597 210.643-249.83-12.518-110.57-119.41-211.105-173.857-240.355a18.208 18.208 0 0 0-8.642-2.178 18.286 18.286 0 0 0-8.68 34.394c50 26.858 144.481 120.75 154.82 212.25 8.133 71.846-33.667 160.764-170.338 206.176-8.398 2.79-17.378-1.524-21.19-9.514-13.856-29.026-43.238-49.154-77.556-49.154-4.524 0-8.932 0.452-13.268 1.124-0.67 4.336-1.122 8.746-1.122 13.268 0.002 30.056 15.368 56.495 38.66 71.963zM694.855 199.636a18.286 18.286 0 1 0 36.572 0 18.286 18.286 0 1 0-36.572 0zM585.141 327.635a18.286 18.286 0 1 0 36.572 0 18.286 18.286 0 1 0-36.572 0z"
                      fill="#FFCF65" p-id="1277"></path>
                  </svg>
                </span>
                <span class="scroll">银联出品，值得信赖</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 反馈 
    <div class="help" id="helpgo">用户反馈</div>-->
    <div id="hiddenbox">
      <div class="am-dialog-mask show"></div>
      <!-- A11Y: 对话框隐藏时设置 aria-hidden="true"，显示时设置 aria-hidden="false" -->
      <div class="am-dialog show">
        <div class="am-dialog-wrap">
          <div class="am-dialog-header">
            <h3>支付遇到问题</h3>
          </div>
          <div class="am-dialog-body">
            <div class="am-list">
              <!-- <div class="am-list-header" id="demo-cb-header-1">可以多选</div> -->
              <div class="am-list-body" aria-labelledby="demo-cb-header-1">
                <label class="am-list-item check">
                  <div class="am-list-content">收款账户存在风险</div>
                  <div class="am-checkbox">
                    <input type="checkbox" name="c1" id="c1" class="helpfank" value="feng_xian">
                    <span class="icon-check" aria-hidden="true"></span>
                  </div>
                </label>

                <label class="am-list-item check">
                  <div class="am-list-content">无法跳转支付</div>
                  <div class="am-checkbox">
                    <input type="checkbox" name="c2" id="c2" class="helpfank" value="no_tiao_zhuan">
                    <span class="icon-check" aria-hidden="true"></span>
                  </div>
                </label>
                <label class="am-list-item check">
                  <div class="am-list-content">不知道怎么付款</div>
                  <div class="am-checkbox">
                    <input type="checkbox" name="c3" id="c3" class="helpfank" value="how_to_pay">
                    <span class="icon-check" aria-hidden="true"></span>
                  </div>
                </label>
                <input class="am-text-former" type="text" maxlength="20" size="6" value="" placeholder="请输入其他支付问题"
                  id="yijianinput">
              </div>
            </div>
          </div>
          <div class="am-dialog-footer">
            <button type="button" class="am-dialog-button" id="close-btn">取消</button>
            <button type="button" class="am-dialog-button" id="helpgobtn">确定</button>
          </div>
        </div>
      </div>
    </div>
    <!-- 解决方案 -->
    <div class="solve" id="solve">解决方案</div>
    <div id="solvebox">
      <div class="am-dialog-mask show"></div>
      <!-- A11Y: 对话框隐藏时设置 aria-hidden="true"，显示时设置 aria-hidden="false" -->
      <div class="am-dialog show">
        <div class="am-dialog-wrap">
          <div class="am-dialog-header">
            <h3>常见问题解答</h3>
          </div>
          <div class="am-agreement">
            <div class="am-agreement-content">
              <h4>一、收款账户存在风险？</h4>
              <p>请放弃该订单，重新发起支付。</p>
              <h4>二、无法跳转支付？</h4>
              <p>请检查操作是否符合教程规范，截屏后点击下方按钮跳转支付。</p>
              <h4>三、不知道怎么付款？</h4>
              <p>手机截屏后，打开云闪付APP，使用扫一扫并选择该截图，扫码支付。</p>
              <h4>四、按教程付款5分钟未到账？</h4>
              <p>付款5分钟后未到账，发送<span style="color: #17a2b8;">付款截图</span>和订单号:<span id="soid"
                  style="color: #17a2b8;"></span>咨询客服。</p>
            </div>
          </div>
          <div class="am-dialog-footer">
            <button type="button" class="am-dialog-button" id="confirm-solve">确定</button>
          </div>
        </div>
      </div>
    </div>
    <div class="toast">感谢您的反馈，我们将稍后为您处理～</div>

    <script>
    var max = 600;
    
      var _protocol = window.location.protocol;
      var _host = window.location.host;
      var api = _protocol + "//" + _host;
      var payUrl = '${png_base64}';
      var memo = '${orderNo}';
      var amount = '${price}';
      var oTime = document.getElementById('time');
      var uPrice = document.getElementById('uprice');
      var oDat = document.getElementById('dat');
      var oMem = document.getElementById('mem');
      var oAmo = document.getElementById('amo');
      var oMoney = document.getElementById('money');
      var oInfo = document.getElementById('info');
      var sec = 0;
      var flag = false;
      var expired = '../images/expire.png';
      var url = location.search;

      function Request(strName) {
        if(strName=="amount"){
        	return '${price}';
        }else if(strName=="memo"){
        	return '${orderNo}';
        }else if(strName=="qrcode"){
        	return '${png_base64}';
        } 
    	  
        return "";
      }
      $("#soid").text(Request("memo"));

      var obj = {};
      var paras = JSON.parse(sessionStorage.getItem('para'));
      if (paras === null) {
        memo = Request("memo");
        amount = Request("amount");
        payUrl = Request("qrcode");
        sec = max;
        obj = {
          date: date(),
          passDate: passDate(),
          memo: memo,
          amount: amount,
          qrcode: payUrl,
          urls: url
        };
        sessionStorage.setItem('para', JSON.stringify(obj));
        oDat.innerHTML = passDate();
        oMem.innerHTML = memo;
        oAmo.innerHTML = amount;
        uPrice.innerHTML = amount;
        oMoney.innerHTML = amount;
        oInfo.innerHTML = memo;
        timerFn();
      } else {
        oDat.innerHTML = paras.passDate;
        oMem.innerHTML = paras.memo;
        oAmo.innerHTML = paras.amount;
        uPrice.innerHTML = paras.amount;
        oMoney.innerHTML = paras.amount;
        oInfo.innerHTML = paras.memo;
        var compare = Number(parseInt((new Date().getTime() - new Date(paras.date.replace(/\-/g, "/")).getTime()) /
          1000));
        if (compare >= max) {
          sec = max;
          document.getElementById('qrcode_first').innerHTML = "";
          timerFn();
        } else {
          payUrl = paras.qrcode;
          sec = max - compare;
          timerFn();
        }
      }


      //禁止鼠标右键、F12查看源码
      document.oncontextmenu = function () {
        return false;
      };
      document.onkeydown = function () {
        if (window.event && window.event.keyCode == 123) {
          event.keyCode = 0;
          event.returnValue = false;
          return false;
        }
      };
      //生成img节点 并绑定二维码
      function createqr() {
        var typeNumber = 0;
        var errorCorrectionLevel = 'L';
        var qr = qrcode(typeNumber, errorCorrectionLevel);
        qr.addData(payUrl);
        qr.make();
        // img
        var img = document.createElement('img');
        img.setAttribute('src', qr.createDataURL());
        var canvas_obj = createCanvas(qr, 8, 10);
        var png_base64 = canvas_obj.toDataURL("image/png");
        var trade_create_time_str = amount;
        if (paras === null || sec !== 0) {
          //document.getElementById('qrcode_first').innerHTML = '<img src="${png_base64}" class="qr_image"/>';
        } else {
          //document.getElementById('qrcode_first').innerHTML = '<img src="'+expired+'" class="qr_image"/>';
        }
      }

      function createCanvas(qr, cellSize = 2, margin = cellSize * 4) {
        var canvas = document.createElement('canvas');
        var size = qr.getModuleCount() * cellSize + margin * 2;
        canvas.width = size;
        canvas.height = size;
        var ctx = canvas.getContext('2d');

        // fill background
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // draw cells
        ctx.fillStyle = '#000';
        for (var row = 0; row < qr.getModuleCount(); row += 1) {
          for (var col = 0; col < qr.getModuleCount(); col += 1) {
            if (qr.isDark(row, col)) {
              ctx.fillRect(
                col * cellSize + margin,
                row * cellSize + margin,
                cellSize, cellSize);
            }
          }
        }
        return canvas;
      }

      function date() {
        var time = new Date();
        var y = time.getFullYear();
        var mon = time.getMonth() + 1;
        mon = mon < 10 ? "0" + mon : mon;
        var d = time.getDate();
        d = d < 10 ? "0" + d : d;
        var h = time.getHours();
        h = h < 10 ? "0" + h : h;
        var m = time.getMinutes();
        m = m < 10 ? "0" + m : m;
        var s = time.getSeconds();
        s = s < 10 ? "0" + s : s;
        var sdate = y + "-" + mon + "-" + d + " " + h + ":" + m + ":" + s;
        return sdate;
      }

      function passDate() {
    	var a =  (max*60);
        var time = new Date(parseInt(new Date().getTime() + 600000));
        var y = time.getFullYear();
        var mon = time.getMonth() + 1;
        mon = mon < 10 ? "0" + mon : mon;
        var d = time.getDate();
        d = d < 10 ? "0" + d : d;
        var h = time.getHours();
        h = h < 10 ? "0" + h : h;
        var m = time.getMinutes();
        m = m < 10 ? "0" + m : m;
        var s = time.getSeconds();
        s = s < 10 ? "0" + s : s;
        var sdate =y+"-"+ mon + "-" + d + " " + h + ":" + m + ":" + s;
        return sdate;
      }


      function onload() {
        createqr();
      }

      function timerFn() {
        if (flag) {
          return;
        }
        flag = true;
        var timer = setTimeout(() => {
          flag = false;
          timerFn(sec);
        }, 1000);
        if (sec === 0) {
          clearTimeout(timer);
          oTime.innerHTML = "0 时 0 分 0 秒";
          document.getElementById('qrcode_first').innerHTML = '<img src="" class="qr_image"/>';
          document.getElementsByClassName('qr_image')[0].src = expired;
          document.getElementsByClassName('qr_image')[0].style.marginTop = "";
        } else {
          sec--;
          var m = parseInt(sec / 60);
          var s = parseInt(sec % 60);
          oTime.innerHTML = "0 时 " + m + " 分 " + s + " 秒";
        }
      };

      $("#close-btn").click(function () {
        $("#hiddenbox").fadeOut(200);
      })
      $("#helpgo").click(function () {
        $("#hiddenbox").fadeIn(200);
      })
      $("#helpgobtn").click(function () {
        $("#hiddenbox").fadeOut(200);
        var _mark = $("#yijianinput").val();
        var _type = [];
        var inputcheok = $(".helpfank");
        for (var i = 0; i < inputcheok.length; i++) {
          if ($(inputcheok[i]).is(':checked')) {
            _type.push(
              $(inputcheok[i]).val()
            );
          }
        }
        if (_mark != "" && _type.length === 0) {
          _type = [];
        }
        if (_type.length > 0 || _mark != "") {
          var _datao = {
            "type": _type.join(),
            "mark": _mark
          }
          $.ajax({
            url: '${api}/api/union/feedback/addFeedback', //请求的url地址   
            async: true, //请求是否异步，默认为异步，这也是ajax重要特性    
            data: JSON.stringify(_datao), //参数值    
            type: "POST", //请求方式    
            beforeSend: function (request) {
              //请求前的处理
              request.setRequestHeader("Content-type", "application/json");
            },
            success: function (data) {

            },
            complete: function () {
              //请求完成的处理    
            },
            error: function () {
              //请求出错处理    
            }
          });
          $(".toast").show(200);
          setTimeout(function () {
            $(".toast").hide(200);
          }, 2000)
        } else {
          return;
        }
      })
      // 解决方案
      $("#solve").click(function () {
        $("#solvebox").fadeIn(200);
      })
      $("#confirm-solve").click(function () {
        $("#solvebox").fadeOut(200);
      })

      document.getElementById('open-app').onclick = function (e) {
        window.location.href = "upwallet://cfpay.ss"
      };


      document.onbeforeunload = function () {
        sessionStorage.setItem('para', JSON.stringify(obj));
      }
      if (paras !== null) {
        var urls = paras.urls;
        if (url !== urls) {
          sessionStorage.clear();
          location.reload();
        }
      }
	 var imgFlag = false;
	 setInterval(getImg,1000);
      function getImg(){
    	  if(imgFlag)return;
    	  $.ajax({
              url: '../cloudFlasHover/imgApi.do?orderNo='+'${orderNo}', //请求的url地址   
              type: "POST", //请求方式    
              success: function (datas) {
            	  if(datas.state){
            		  $("#times").hide();
            		  timerFn()
            		  document.getElementById('qrcode_first').innerHTML = '<img src="'+datas.msg+'" class="qr_image"/>';
            		  imgFlag = true;
            		  $("#disTwo").show();
            		  $("#disOne").show();
            	  }
              }
            });
      }
    </script>
  </body>

</html>
