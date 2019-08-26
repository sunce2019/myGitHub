<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0,viewport-fit=cover">
<title>快捷支付</title>
<link href="../css/pay.css" rel="stylesheet" media="screen">
<link rel="stylesheet" href="../css/aurochs.css" />
<script src="../js/jquery.min.js"></script>
<script src="../js/clipboard.min.js"></script>
<script src="../js/alipaytransfer.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<style>
        .weui-input {background-color: #eeeeee;}     
        .weui-label {width: 86px;}
        .weui-vcode-btn {padding: 0 1.3em;}
        .weui-vcode-btn {color: #ffffff; color: #f00;}
        .weui-vcode-btn:not(.weui-btn_disabled):active {color: #ffffff;color: #f00;}
        .weui-cell__bd img {vertical-align: middle;padding: 0;margin: 0;bottom: 0;}
        .page, body {background-color: #f8f8f8;}
        .weui-header {position: relative;display: block;margin-left: auto;margin-right: auto;
            padding-left: 14px;padding-right: 14px;box-sizing: border-box; font-size: 18px;
            text-align: center;text-decoration: none;color: #fff;line-height: 2.55555556;
            -webkit-tap-highlight-color: rgba(0,0,0,0);overflow: hidden;background-color: #00a0e9;}
		.line {border-bottom: 1px dashed #e5e5e5;padding:5px;padding-bottom:5px;margin-bottom:5px; font-size:13px;font-weight:bold;}
		h2{font-weight:bold;}
    </style>
<script type="text/javascript">
function alipay_ts() {
	alert("①请截屏保存二维码\n②请手动打开支付宝\n③扫一扫从相册读取");
}
</script>
 <script>
        function openWechat() {
            window.location.href = 'weixin://';
        }
    </script>
</head>
<body>
<div class="body">
    <h1 class="mod-title">
        <c:if test="${type eq '微信'}">
			<span class="ico_log ico-3"></span>
		</c:if>
		<c:if test="${type eq '支付宝'}">
			<span class="ico_log ico-1"></span>
		</c:if>
		<c:if test="${type eq '银联'}">
			<span class="ico_log ico-3"></span>
		</c:if>
    </h1>
    <div class="mod-ct">
	<div class="line">
		<h1>-您可通过<span style="color:red">复制、粘贴</span>银行信息<span style="color:red">微信转银行</span>-</h1>
		<h2>*一定要复制<span style="color:red">带小数点</span>金额进行支付，否则<span style="color:red">无法及时到账</span>*</h2>
	</div>
	<div class="line">
		<h2>复制【银行卡信息】，进微信转银行页面，粘贴支付：</h2>
    	<div class="page">
        <div class="weui-cells weui-cells_form">
            <div class="weui-cell weui-cell_vcode">
                <div class="weui-cell__hd">
                    <label class="weui-label">存款户名:</label>
                </div>
                <div class="weui-cell__bd">
                    <input class="weui-input" id="hm" type="text" disabled value="${bank.bankAccount}" />
                </div>
                <div class="weui-cell__ft">
                    <button class="weui-vcode-btn" id="copy_name">复制</button>
                </div>
            </div>
            <div class="weui-cell weui-cell_vcode">
                <div class="weui-cell__hd">
                    <label class="weui-label">收款账号:</label>
                </div>
                <div class="weui-cell__bd">
                    <input class="weui-input" id="zh" type="text" disabled value="${bank.cardNo}" />
                </div>
                <div class="weui-cell__ft">
                    <button class="weui-vcode-btn" id="copy_zh">复制</button>
                </div>
            </div>
            <div class="weui-cell weui-cell_vcode">
                <div class="weui-cell__hd">
                    <label class="weui-label">开户银行:</label>
                </div>
                <div class="weui-cell__bd">
                    <input class="weui-input" id="yh" type="text" disabled value="${bank.bankName}" />
                </div>
                <div class="weui-cell__ft">
                    <button class="weui-vcode-btn" id="copy_yh">复制</button>
                </div>
            </div>           
            <div class="weui-cell weui-cell_vcode">
                <div class="weui-cell__hd">
                    <label class="weui-label">充值金额:</label>
                </div>
                <div class="weui-cell__bd">
                    <input class="weui-input" id="je" type="text" style="background-color:red; color:white;" disabled value="${price}" />
                </div>
                <div class="weui-cell__ft">
                    <button class="weui-vcode-btn" id="copy_je">复制</button>
                </div>
            </div>
        </div>
		<c:if test="${not empty alipayToBak}">
	        <div class="iospayweixinbtn" style="padding-top: 15px;">
				<a href="alipay://" id="aid" style=";color: #fff;background-color: #01c310;border-color: #01c310" class="btn btn-primary">打开微信，粘贴支付</a>
	        </div>
        </c:if>
        <div class="weui-cells__title line" style="font-size:18px;font-align:left">
            第一步: 打开<span style="color:red">【微信】</span>点击右上角㊉<br/>
			第二步: 点击㊉下拉<span style="color:red">【收付款】</span>按钮<br/>
			第三步: 点击最后<span style="color:red">【转账到银行卡】</span><br/>
			第四步: 复制本页<span style="color:red">【卡号信息】</span>粘贴
        </div>
        <div class="weui-cells__tips" style="font-size:19px; color:red; font-weight:bold;">
            请务必<span style="color:#228b22">复制</span>订单随机生成的<span style="color:#228b22">带小数点金额</span>进行转账否则平台无法及时为您入款(3-5分钟内)如不到账请及时联系平台在线客服截图订单为您处理。
        </div>
		</div>	
			
		<div class="js_dialog" id="dialog2" style="display: none;">
			<div class="weui-mask"></div>
			<div class="weui-dialog">
				<div class="weui-dialog__bd">复制成功!</div>
				<div class="weui-dialog__bd" style="color:red;">请务必<span style="color:#228b22">复制</span>订单随机生成的带小数点金额进行转账!</div>
				<div class="weui-dialog__ft">
					<a href="weixin://" class="weui-dialog__btn weui-dialog__btn_primary" id="btn_affirm">打开微信，粘贴支付</a>
				</div>
			</div>
		</div>
	</div>
	
	<!-- <div class="foot"> -->
        <!-- <div class="inner"> -->
            <!-- <p>版权所有 &copy; 2019</p> -->
        <!-- </div> -->
    <!-- </div> -->	
	</div>
	<style>
    	   .swiper-container {width: 60%;}
           .swiper-container img {display: block;width: 100%;}

    </style>


    <div class="weui-btn-area" onclick="openWechat()">
        <a class="weui-btn weui-btn_primary" href="weixin://" style="display: block;">打开微信，粘贴支付</a>
    </div>
    <div class="weui-cells__title" style="text-align: center;">请左右滑动查看转账流程</div>
    <div class="swiper-container" data-space-between='10' data-pagination='.swiper-pagination' data-autoplay="1000" style="margin-bottom: 28px;">
        <div class="swiper-wrapper">
            <div class="swiper-slide"><img src="../images/01.jpg" alt=""></div>
            <div class="swiper-slide"><img src="../images/02.jpg" alt=""></div>
            <div class="swiper-slide"><img src="../images/03.jpg" alt=""></div>
            <div class="swiper-slide"><img src="../images/04.jpg" alt=""></div>
            <div class="swiper-slide"><img src="../images/05.jpg" alt=""></div>
        </div>
        <div class="swiper-pagination"></div>
    </div>
<script type='text/javascript' src='../js/jquery.js' charset='utf-8'></script>
<script type='text/javascript' src='../js/jquery-weui.min.js' charset='utf-8'></script>
<script type='text/javascript' src='../js/swiper.min.js' charset='utf-8'></script>
<link rel="stylesheet" href="../css/jquery-weui.min.css">

<script>
    $(".swiper-container").swiper({
        loop: true,
        autoplay: 2000
    });
</script>
</div>
<script type="text/javascript">    
    var myTimer;
    var strcode = '';
    function timer(intDiff) {
        myTimer = window.setInterval(function () {
            var day = 0,
                hour = 0,
                minute = 0,
                second = 0;//时间默认值
            if (intDiff > 0) {
                day = Math.floor(intDiff / (60 * 60 * 24));
                hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
                minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
                second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
            }
            if (minute <= 9) minute = '0' + minute;
            if (second <= 9) second = '0' + second;
            $('#hour_show').html('<s id="h"></s>' + hour + '时');
            $('#minute_show').html('<s></s>' + minute + '分');
            $('#second_show').html('<s></s>' + second + '秒');
            if (hour <= 0 && minute <= 0 && second <= 0) {
                qrcode_timeout();
                clearInterval(myTimer);
            }
            intDiff--;
            if (strcode != ""){
                //checkdata();
            }
        }, 1000);
    }
    function checkdata(){
        $.post("get.php",{id : "6180096"},function(data){
			//var data = jQuery.parseJSON(data);	//格式化JSON
			if (data.code == 3){
				window.clearInterval(timer);
				$("#show_qrcode").attr("src","images/pay_ok.png");
				$("#use").remove();
				$("#money").text("支付成功");
				$("#msg").html("<h1>即将返回商家页</h1>");
				if (isMobile() == 1){
					$(".paybtn").html('<a href="' + data.url + '" class="btn btn-primary">返回商家页</a>');
					setTimeout(function(){
						// window.location = data.url;
						location.replace(data.url)
					}, 3000);
				}else{
					$("#msg").html("<h1>即将<a href='http://149.129.94.177:8686/success.do'>跳转</a>回商家页</h1>");
					setTimeout(function(){
						// window.location = data.url;
						location.replace(data.url)
					}, 3000);
				}
			}
		});
	}
    function qrcode_timeout(){
        $('#show_qrcode').attr("src","images/qrcode_timeout.png");
        $("#use").hide();
        $('#msg').html("<h1>过期后请勿支付，不自动到账</h1>");
    }
    function isWeixin() { 
        var ua = window.navigator.userAgent.toLowerCase(); 
        if (ua.match(/MicroMessenger/i) == 'micromessenger') { 
            return 1;
        } else { 
            return 0;
        } 
    }
    function isMobile() {
        var ua = navigator.userAgent.toLowerCase();
        _long_matches = 'googlebot-mobile|android|avantgo|blackberry|blazer|elaine|hiptop|ip(hone|od)|kindle|midp|mmp|mobile|o2|opera mini|palm( os)?|pda|plucker|pocket|psp|smartphone|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce; (iemobile|ppc)|xiino|maemo|fennec';
        _long_matches = new RegExp(_long_matches);
        _short_matches = '1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|e\-|e\/|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\-|2|g)|yas\-|your|zeto|zte\-';
        _short_matches = new RegExp(_short_matches);
        if (_long_matches.test(ua)) {
            return 1;
        }
        user_agent = ua.substring(0, 4);
        if (_short_matches.test(user_agent)) {
            return 1;
        }
        return 0;
    }
    //本地生成二维码
    function showCodeImage(){
        var qrcode = $('#qrcode').qrcode({  
            text: '',  
            width: 200,  
            height: 200,
        }).hide();  
        //添加文字  
        var outTime = '过期时间：2019-04-06 14:49:42';//过期时间
        var canvas = qrcode.find('canvas').get(0);  
        var oldCtx = canvas.getContext('2d');  
        var imgCanvas = document.getElementById('imgCanvas');  
        var ctx = imgCanvas.getContext('2d');  
        ctx.fillStyle = 'white';  
        ctx.fillRect(0,0,310,270);  
        ctx.putImageData(oldCtx.getImageData(0, 0, 200, 200), 55, 20);  
        //ctx.stroke = 3;  
        ctx.textBaseline = 'middle';  
        ctx.textAlign = 'center';  
        ctx.font ="20px Arial";  
        ctx.fillStyle = 'red';
        ctx.strokeStyle = 'red'
        ctx.fillText(outTime, imgCanvas.width / 2, 235 );  
        ctx.strokeText(outTime, imgCanvas.width / 2, 235);  
        var about = '过期后请勿支付，不自动到账'; 
        ctx.fillText(about, imgCanvas.width / 2, 260 );  
        ctx.strokeText(about, imgCanvas.width / 2, 260);  
        imgCanvas.style.display = 'none';  
        $('#show_qrcode').attr('src', imgCanvas.toDataURL('image/png')).css({  
            width: 310,height:270  
        }); 
        // $('#downloadbtn').attr('href', imgCanvas.toDataURL('image/png'));
    }
    $().ready(function(){
        //如果百度图片加载失败,就在本地生成图片
        // $('#show_qrcode').error(function(){
            // showCodeImage();
        // });
        //默认6分钟过期
        timer("2400");
        var istype = "2";
        var suremoney = "0";
        var uaa = navigator.userAgent;
        var isiOS = !!uaa.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if (isMobile() == 1){
            if (isWeixin() == 1 && istype == 2){
                //微信内置浏览器+微信支付
                $("#showtext").text("长按二维码识别");
            } else{
                //其他手机浏览器+支付宝支付
                if (isWeixin() == 0 && istype == 1){
                    $(".paybtn").attr('style','');
                    var goPay = '<span id="goPay"> <span>';
                    //给A标签中的文字添加一个能被jQuery捕获的元素
                    $('#alipaybtn').append(goPay);
                    //模拟点击A标签中的文字
                    // $('#goPay').click();
                    $('#msg').html("<h1>支付完成后，请返回此页</h1>");
                    $(".qrcode-img-wrapper").remove();
                    $(".tip").remove();
                    $(".foot").remove();                                      
                    //$(location).attr('href', '');
                } else {
                    if (isWeixin() == 0 && istype == 2){
                        //其他手机浏览器+微信支付
                        //IOS的排除掉
                        if (isiOS){
                            // showCodeImage();
                            $('.iospayweixinbtn').attr('style','padding-top: 15px;');
                        }else{
                            $(".payweixinbtn").attr('style','padding-top: 15px;');
                        }                    
                        //$("#showtext").html("请保存二维码到手机<br>扫一扫点右上角-从相册选取");
                    }
                }
            }
        }
        if (isiOS){
            $('#show_qrcode').css({width: 310,height:310});  
        }else{
            var show_expire_time = '1554533382';
            if(show_expire_time!='0'){
                if (document.getElementById("imgCanvas").getContext){
                    try {
						var list_wxp_s_s = 2*1;	//赞赏码模式
                        if (list_wxp_s_s  > 1) {	//非赞赏码模式
							//showCodeImage();
						}
                    } catch (error) {  
                        $('#show_qrcode').attr('src', "##"); 
                    }
                }else{
                    $('#show_qrcode').attr('src', "##"); 
                }
            }else{
                $('#show_qrcode').attr('src', "##");     
            }
        }
    });
    //
</script>
</body></html>