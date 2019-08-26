<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- saved from url=(0065)http://zfbpay.ibosser.com:9970/bank/fromalipay4/19082000612658713 -->
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">.swal-icon--error{border-color:#f27474;-webkit-animation:animateErrorIcon .5s;animation:animateErrorIcon .5s}.swal-icon--error__x-mark{position:relative;display:block;-webkit-animation:animateXMark .5s;animation:animateXMark .5s}.swal-icon--error__line{position:absolute;height:5px;width:47px;background-color:#f27474;display:block;top:37px;border-radius:2px}.swal-icon--error__line--left{-webkit-transform:rotate(45deg);transform:rotate(45deg);left:17px}.swal-icon--error__line--right{-webkit-transform:rotate(-45deg);transform:rotate(-45deg);right:16px}@-webkit-keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@-webkit-keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}@keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}.swal-icon--warning{border-color:#f8bb86;-webkit-animation:pulseWarning .75s infinite alternate;animation:pulseWarning .75s infinite alternate}.swal-icon--warning__body{width:5px;height:47px;top:10px;border-radius:2px;margin-left:-2px}.swal-icon--warning__body,.swal-icon--warning__dot{position:absolute;left:50%;background-color:#f8bb86}.swal-icon--warning__dot{width:7px;height:7px;border-radius:50%;margin-left:-4px;bottom:-11px}@-webkit-keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}@keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}.swal-icon--success{border-color:#a5dc86}.swal-icon--success:after,.swal-icon--success:before{content:"";border-radius:50%;position:absolute;width:60px;height:120px;background:#fff;-webkit-transform:rotate(45deg);transform:rotate(45deg)}.swal-icon--success:before{border-radius:120px 0 0 120px;top:-7px;left:-33px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:60px 60px;transform-origin:60px 60px}.swal-icon--success:after{border-radius:0 120px 120px 0;top:-11px;left:30px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:0 60px;transform-origin:0 60px;-webkit-animation:rotatePlaceholder 4.25s ease-in;animation:rotatePlaceholder 4.25s ease-in}.swal-icon--success__ring{width:80px;height:80px;border:4px solid hsla(98,55%,69%,.2);border-radius:50%;box-sizing:content-box;position:absolute;left:-4px;top:-4px;z-index:2}.swal-icon--success__hide-corners{width:5px;height:90px;background-color:#fff;padding:1px;position:absolute;left:28px;top:8px;z-index:1;-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}.swal-icon--success__line{height:5px;background-color:#a5dc86;display:block;border-radius:2px;position:absolute;z-index:2}.swal-icon--success__line--tip{width:25px;left:14px;top:46px;-webkit-transform:rotate(45deg);transform:rotate(45deg);-webkit-animation:animateSuccessTip .75s;animation:animateSuccessTip .75s}.swal-icon--success__line--long{width:47px;right:8px;top:38px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-animation:animateSuccessLong .75s;animation:animateSuccessLong .75s}@-webkit-keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@-webkit-keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@-webkit-keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}@keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}.swal-icon--info{border-color:#c9dae1}.swal-icon--info:before{width:5px;height:29px;bottom:17px;border-radius:2px;margin-left:-2px}.swal-icon--info:after,.swal-icon--info:before{content:"";position:absolute;left:50%;background-color:#c9dae1}.swal-icon--info:after{width:7px;height:7px;border-radius:50%;margin-left:-3px;top:19px}.swal-icon{width:80px;height:80px;border-width:4px;border-style:solid;border-radius:50%;padding:0;position:relative;box-sizing:content-box;margin:20px auto}.swal-icon:first-child{margin-top:32px}.swal-icon--custom{width:auto;height:auto;max-width:100%;border:none;border-radius:0}.swal-icon img{max-width:100%;max-height:100%}.swal-title{color:rgba(0,0,0,.65);font-weight:600;text-transform:none;position:relative;display:block;padding:13px 16px;font-size:27px;line-height:normal;text-align:center;margin-bottom:0}.swal-title:first-child{margin-top:26px}.swal-title:not(:first-child){padding-bottom:0}.swal-title:not(:last-child){margin-bottom:13px}.swal-text{font-size:16px;position:relative;float:none;line-height:normal;vertical-align:top;text-align:left;display:inline-block;margin:0;padding:0 10px;font-weight:400;color:rgba(0,0,0,.64);max-width:calc(100% - 20px);overflow-wrap:break-word;box-sizing:border-box}.swal-text:first-child{margin-top:45px}.swal-text:last-child{margin-bottom:45px}.swal-footer{text-align:right;padding-top:13px;margin-top:13px;padding:13px 16px;border-radius:inherit;border-top-left-radius:0;border-top-right-radius:0}.swal-button-container{margin:5px;display:inline-block;position:relative}.swal-button{background-color:#7cd1f9;color:#fff;border:none;box-shadow:none;border-radius:5px;font-weight:600;font-size:14px;padding:10px 24px;margin:0;cursor:pointer}.swal-button:not([disabled]):hover{background-color:#78cbf2}.swal-button:active{background-color:#70bce0}.swal-button:focus{outline:none;box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(43,114,165,.29)}.swal-button[disabled]{opacity:.5;cursor:default}.swal-button::-moz-focus-inner{border:0}.swal-button--cancel{color:#555;background-color:#efefef}.swal-button--cancel:not([disabled]):hover{background-color:#e8e8e8}.swal-button--cancel:active{background-color:#d7d7d7}.swal-button--cancel:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(116,136,150,.29)}.swal-button--danger{background-color:#e64942}.swal-button--danger:not([disabled]):hover{background-color:#df4740}.swal-button--danger:active{background-color:#cf423b}.swal-button--danger:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(165,43,43,.29)}.swal-content{padding:0 20px;margin-top:20px;font-size:medium}.swal-content:last-child{margin-bottom:20px}.swal-content__input,.swal-content__textarea{-webkit-appearance:none;background-color:#fff;border:none;font-size:14px;display:block;box-sizing:border-box;width:100%;border:1px solid rgba(0,0,0,.14);padding:10px 13px;border-radius:2px;transition:border-color .2s}.swal-content__input:focus,.swal-content__textarea:focus{outline:none;border-color:#6db8ff}.swal-content__textarea{resize:vertical}.swal-button--loading{color:transparent}.swal-button--loading~.swal-button__loader{opacity:1}.swal-button__loader{position:absolute;height:auto;width:43px;z-index:2;left:50%;top:50%;-webkit-transform:translateX(-50%) translateY(-50%);transform:translateX(-50%) translateY(-50%);text-align:center;pointer-events:none;opacity:0}.swal-button__loader div{display:inline-block;float:none;vertical-align:baseline;width:9px;height:9px;padding:0;border:none;margin:2px;opacity:.4;border-radius:7px;background-color:hsla(0,0%,100%,.9);transition:background .2s;-webkit-animation:swal-loading-anim 1s infinite;animation:swal-loading-anim 1s infinite}.swal-button__loader div:nth-child(3n+2){-webkit-animation-delay:.15s;animation-delay:.15s}.swal-button__loader div:nth-child(3n+3){-webkit-animation-delay:.3s;animation-delay:.3s}@-webkit-keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}@keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}.swal-overlay{position:fixed;top:0;bottom:0;left:0;right:0;text-align:center;font-size:0;overflow-y:auto;background-color:rgba(0,0,0,.4);z-index:10000;pointer-events:none;opacity:0;transition:opacity .3s}.swal-overlay:before{content:" ";display:inline-block;vertical-align:middle;height:100%}.swal-overlay--show-modal{opacity:1;pointer-events:auto}.swal-overlay--show-modal .swal-modal{opacity:1;pointer-events:auto;box-sizing:border-box;-webkit-animation:showSweetAlert .3s;animation:showSweetAlert .3s;will-change:transform}.swal-modal{width:478px;opacity:0;pointer-events:none;background-color:#fff;text-align:center;border-radius:5px;position:static;margin:20px auto;display:inline-block;vertical-align:middle;-webkit-transform:scale(1);transform:scale(1);-webkit-transform-origin:50% 50%;transform-origin:50% 50%;z-index:10001;transition:opacity .2s,-webkit-transform .3s;transition:transform .3s,opacity .2s;transition:transform .3s,opacity .2s,-webkit-transform .3s}@media (max-width:500px){.swal-modal{width:calc(100% - 20px)}}@-webkit-keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}@keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}</style>
    
    <title>在线支付 - 网上支付 安全快速！</title>
    <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" name="viewport">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <link href="../../css/QRCode.css" rel="stylesheet">
    <link href="../../css/bootstrap.min.css" rel="stylesheet">
    <script src="../../js/jquery.min.js"></script>
    <script src="../../js/bootstrap.min.js"></script>
    <script src="../../js/alipayjsapi.inc.min.js"></script>
    <script src="../../js/sweetalert.min.js"></script>
    <script type="text/javascript" src="../../js/qrcode.min2.js"></script>
    <script type="text/javascript" src="../../js/ailipaycommon.js"></script>
    <style>
        .swal-title {
            padding: 0px;
        }

        .swal-content {
            background-color: #FFFFCC;
            font-size: 20px !important;
            padding: 20px;
        }

        .swal-button-container {
            width: 100%;
        }

        .swal-button {
            font-size: 20px;
            width: 100%;
        }
    </style>
</head>
<body>
    <div style="width: 100%; text-align: center;font-family:微软雅黑;background-color:white;min-height:1000px;">
        <div id="panelWrap" class="panel-wrap">
            <div class="panel panel-easypay">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-xs-12 text-center">
                            <img src="../../images/alipay.png" class="img-responsive center-block">
                        </div>
                    </div>
                </div>
                <div class="panel-body" style="background:#fff url(&#39;../../images/wave.png&#39;) top center repeat-x">
                    <h3>
                        <small>订单号：${orderNo}</small>
                    </h3>
                    <div class="money">
                        <span class="price">
                            <span class="price">${price}</span>
                        </span>
                        <span class="currency">元</span>
                        <h4 style="display:none">
                            <small style="color:red;">
                                请按照“付款步骤”引导安全付款
                            </small>
                        </h4>
                    </div>

                        <div class="tips">
                            <span style="color:red;font-size:1.5em;border:solid 2px;border-color:green;background:#faffb8;width:100%;display:block;border-radius:15px;padding:20px;">
                                先开【飞行模式】<br>
                                再点“立即支付”进入支付宝<br>
                                <small style="color:darkblue">进入支付宝3秒后再关闭飞行模式</small>
                                <a href="javascript:openAlipay()" class="btn btn-primary btn-lg btn-block" style="font-size:28px;" id="alipay">立即支付</a>
                            </span>
                        </div>
                        <div class="panel-body">
                            <span style="border-radius:10px;">
                                <img src="../../images/fly_mode.jpg" class="img-responsive">
                            </span>
                        </div>
                </div>
                <div class="panel-footer">
                    <span><small></small><span id="countdown" class="warning" style="color:red;font-size:1.2em">9分16秒</span></span>
                </div>
                <div class="panel-footer text-left">
                    <p style="color: #040460;">【支付协议】</p>
                    <p style="padding-left:10px;">
                        1.请在规定的时间内完成付款，超时请放弃并重新下单<br>
                        2.请核对正确的金额，切勿修改<br>
                        3.请勿保存二维码或者支付链接重复支付
                    </p>
                    <p style="color: #FF508B;">*警告：请必须按照以上要求进行转账，如不按规定，您的资金将无法找回。</p>
                </div>
            </div>
        </div>
    </div>
    <script>
        var waitInterval;
        var waitCount = 5;
        var clickCount = 0;
        var dot = [".", "..", "..."];

        openAlipay = function () {
            var slider = document.createElement("div");
            if (navigator.onLine) {
                slider.innerHTML = "<div>请先开启手机“飞行模式”<br/>再点“我已开启”<br/><small style='color:#888'>进入支付宝【转帐】前请勿开启网络</small></div>";
                swal({
                    dangerMode: true,
                    content: slider,
                    title: "网络还是通的",
                    icon: false,
                    button: "我已开启"
                }).then(openAlipay);
            }
            else {
                slider.innerHTML = '<div>点击启动支付宝，耐心等待出现<br/>【转帐到银行卡】后<br/>再“关闭飞行模式”付款<br/>↓↓↓<br/><a href="javascript:goPay();" class="btn btn-success btn-lg btn-block" style="font-size:20px;">启动支付宝转帐</a></div>';
                swal({
                    content: slider,
                    title: "飞行模式成功，下一步：",
                    icon: false,
                    button: false
                });
            }
        };

        function goPay() {
            setTimeout(function () {
                if (navigator.onLine) {
                    openAlipay();
                }
                else {
                    window.location.href = "${alipayToBak}";
                }
            }, 1000);
        };

        InitQrcode = function () {
            $('#qrcode_load').remove();
            var qrcode = new QRCode('qrcode', {
                text: "http://zfbpay.ibosser.com:9970/bank/fromalipay4/19082000618050354",
                width: 256,
                height: 256,
                colorDark: '#000000',
                colorLight: '#ffffff',
                correctLevel: QRCode.CorrectLevel.H
            });
        };

        //带天数的倒计时
        var timer = null;
        var times = parseInt('${times}');
        countDown = function () {
            timer = setInterval(function () {
                if (times <= 0) {
                    clearInterval(timer);
                    window.location.reload();
                }
                else {
                    var day = 0,
                        hour = 0,
                        minute = 0,
                        second = 0;//时间默认值
                    if (times > 0) {
                        day = Math.floor(times / (60 * 60 * 24));
                        hour = Math.floor(times / (60 * 60)) - (day * 24);
                        minute = Math.floor(times / 60) - (day * 24 * 60) - (hour * 60);
                        second = Math.floor(times) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
                    }

                    if (day <= 0) day = ''; else day = day + '天';
                    if (hour <= 0) hour = ''; else hour = hour + '时';
                    if (minute <= 0) minute = ''; else minute = minute + '分';
                    $("#countdown").text(day + hour + minute + second + '秒');
                    if (Math.ceil(times) % 3 == 0) {
                    	/**
                    	
                    	
                        $.ajax({
                            type: "POST",
                            async: true,
                            url: "/order/query/19082000618050354",
                            contentType: "application/x-www-form-urlencoded;charset=utf-8",
                            data: {},
                            success: function (rdata) {
                                if (rdata == "success") {
                                    if ("http://www.baidu.com" == "")
                                        window.location.reload();
                                    else
                                        window.location.href = "http://www.baidu.com";
                                }
                                else if (rdata == "timeout") {
                                    window.location.reload();
                                }
                                else {
                                    console.log(rdata);
                                }
                            }
                        });**/
                    }
                }
                times--;
            }, 1000);
        }();

        function changeState() {
            clearInterval(waitInterval);
            $("#alipay").attr("href", "javascript:goPay()").text("立即支付").removeClass("btn-success").addClass("btn-primary");
            //goPay();
        }

        function waitSecond() {
            $("#alipay").text("支付宝授权中(" + Math.ceil(100-waitCount) + "%)" + dot[waitCount%3]);
            waitCount--;
            if (waitCount <= 0)
                changeState();
        }

        function returnApp() {
            AlipayJSBridge.call('exitApp', { closeActionType: "exitSelf", animated: false });
        }

        window.onload = function () {
            if (isAlipay()) {
                document.addEventListener('AlipayJSBridgeReady', function () {
                    AlipayJSBridge.call('setTitle', {
                        title: '支付宝收银台',
                    });
                    AlipayJSBridge.call('hideOptionMenu');
                    document.addEventListener("pageResume", function (a) {
                        returnApp();
                    });
                    document.addEventListener("resume", function (a) {
                        //returnApp();
                    });
                });
                waitInterval = setInterval('waitSecond()', 200);
            }
            else if (!isMobile()) {
                InitQrcode();
            }
        }
    </script>

</body></html>