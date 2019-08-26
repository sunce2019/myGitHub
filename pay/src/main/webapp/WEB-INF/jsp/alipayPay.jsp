<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    <title>在线支付 - 支付宝 - 网上支付 安全快速！</title>
    <script>
        var doc = document;
        function isPC() {
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            return !isMobile
        }

        var docEl = doc.documentElement;
        var clientWidth = docEl.clientWidth - 0 || 360;
        if (clientWidth) {
            if (!isPC) {
                var devicePixelRatio = window.devicePixelRatio;
                devicePixelRatio < 1 ? devicePixelRatio = 1 : (devicePixelRatio > 3 ? devicePixelRatio = 3 : '');
                docEl.style.fontSize = 100 * (clientWidth * devicePixelRatio / 360) + 'px';
                var viewport = doc.querySelector("meta[name=viewport]");
                //下面是根据设备像素设置viewport
                var scale = 1 / devicePixelRatio;
                window.scale = scale;
                viewport.setAttribute('content', 'width=device-width,initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
            } else {
                docEl.style.fontSize = '100px';
            }
        }
    </script>


    <style>
        *{margin:0;padding:0;}
        body,html{width:100%;box-shadow:border-box;margin:0;padding:0;background: #c14443;}
        body{font-size:.16rem;background: #c14443;color: #fff;}
        button,input{outline:0;border:none}
        .clear {content:"";display:block;overflow:hidden; clear: both;}
        .self-container{background-color:#c14443}
        .self-container .self-header{border-bottom:1px dotted #aaa}
        .self-header{padding:.2rem;}
        .self-header__logo{float: left; margin-right: .2rem; width: .5rem; height: .5rem;background: url(http://47.52.102.21:9998/img/tx.jpeg) no-repeat center center; background-size: 100% 100%; border-radius: 50%;}
        .self-header__title{float:left;}
        .self-header__title h5{font-size:0.08rem; margin-bottom: 0.05rem;}
        .self-header__title h3{font-size:.15rem;margin-bottom: 0.05rem;}
        .self-info{height:1.1rem;margin:.1rem}
        .self-info__txt{text-align:center;font-size:.12rem;color:#fff;line-height:.3rem}
        .self-info__txt.strong{color: #fff;font-size: 0.1rem;margin: 0.1rem auto 0.2rem;}
        .self-info__price{text-align:center}
        .self-info__price span{color:#fff;font-size:.26rem;margin-right:.06rem}
        .self-submit{padding:0 .15rem;margin: 0.1rem auto;position: relative;}
        .self-submit__btn{display:block;width:100%;background-color:#f9a80e;color:#fff;font-size:.18rem;line-height:.44rem;border-radius:.04rem;border: none;padding: 0;}
        .self-submit__btn:disabled{background-color: #aaa;}
        .self-tips b{display:block;width:100%;text-align:center;line-height:.24rem}
        .self-qrcode img{display:none;width:1.6rem;height:1.6rem;margin:0 auto}
        .self-footer{color:#111;text-align:left;font-weight:600;padding:.05rem}
        .self-layer{display:none;z-index:111;background-color:rgba(0,0,0,.3);top:0;left:0;width:100%;height:100%;position:fixed;pointer-events:auto}
        .self-layer.active{display:block}
        .self-layer__dialog{position:absolute;left:.5rem;top:1rem;top:calc((100% - 4rem)/2);width:2.6rem;background-color:#fff;-webkit-background-clip:content;border-radius:2px;box-shadow:1px 1px 50px rgba(0,0,0,.3);margin:0;padding:0}
        .self-layer__title{background-color:#f8f8f8;border-bottom:1px solid #eee;height:.4rem;line-height:.4rem;color:#333;font-size:.14rem;padding:0 .2rem}
        .self-layer__title .title{display: block;text-align: center;color: red;font-size: 0.22rem}
        .self-layer__title .close{position:absolute;top:0;right:0.1rem;font-size:.18rem;color:#888}
        .self-layer__content{font-size:.14rem;font-weight:500;padding:.2rem}
        .self-layer__action{padding:0}

        .self-container__progress{position:absolute;top:0;height:.44rem;display: block;width: 0%;background-color: #eace99;line-height: 0.44rem;color: #111;border-top-left-radius: .04rem;border-bottom-left-radius: .04rem;text-align: center;font-weight: 600}
        .self-layer__sure{font-size:.14rem;background-color:#1E9FFF;color:#fff;float:right;padding:.1rem 0;width: 100%;}
        .self-layer__sure:disabled{background-color: #aaa;}
        .clearfix{overflow:auto;zoom:1}
        .self-img{margin: 0 auto 0.1rem;}
        .self-img img{display:block;width: 100%;margin: 0 auto;}
        .title{ margin: 0.2rem; color:#fff;}
        .title p{margin:0.08rem 0;}
    </style>
</head>

<body>
<div class="self-container">
    <div class="self-header clear">
        <div class="self-header__logo"></div>
        <div class="self-header__title">
            <h5>Ai充值机器人</h5>
            <h3>请使用支付宝扫码完成付款</h3>
            <h3>付款成功后将自动充值到账</h3>
        </div>
    </div>
    <div class="self-info">

        <div class="self-info__txt" id="textid">充值金额</div>
        <div class="self-info__price"><span>¥ ${price}</span></div>
    </div>
    <div class="self-submit">
     <button type="button" id="btn" class="self-submit__btn"  data-clipboard-text="657070735" onclick="jumpApp()">立即支付</button> 
         <!--   <input type="button" id="btn" class="self-submit__btn" value="正在请求授权...剩余10秒" disabled="disabled" />-->
        <span class="self-container__progress"></span>
    </div>
    <div class="title">
        <p><span style="color: #fff;font-weight: bold; font-size:0.16rem;">注意事项：</span></p>
        <p>* 支付完成，请勿重复支付</p>
        <p>* 按转账金额付款,禁止修改转账金额，否则无法上分</p>
        <p>* 支付成功，1-5分钟自动到账</p>
    </div>
</div>

<script>
function jumpApp(){
	var qrcde = "showBank.do?price="+GetUrlParam("price")+"&n="+GetUrlParam("n");
	window.open(qrcde);
}
function GetUrlParam(name){
  var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
  var r = encodeURI(window.location.search).substr(1).match(reg);
  if(r!=null)return decodeURI(unescape(r[2])); return null;
}
</script>  

</body>
</html>