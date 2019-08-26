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
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript">
function alipay_ts() {
	alert("①请截屏保存二维码\n②请手动打开支付宝\n③扫一扫从相册读取");
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
        <div class="order">
        </div>
        <div class="amount" id="money"></div>
		<div style="font-size: 18px;color:#FF0000;">请截屏保存二维码 打开扫一扫从相册读取</div>
		<div style="font-size: 18px;color:#01c310;">支付时请不要修改金额否则不能到帐</div>
		<div class="time-item" id="msg" style="color:#ff0000;"><h1>付款即时到账 未到账可联系我们-1</h1> </div><div class="time-item"><h1>订单:${orderNo}</h1> </div>		<div class="paybtn" style="margin:-30px 0px;display: none;"></div>
        <c:if test="${not empty alipayToBak}">
	        <div class="iospayweixinbtn" style="padding-top: 15px">
				<a href="${alipayToBak}" id="aid" style=";color: #fff;background-color: #01c310;border-color: #01c310" class="btn btn-primary">启动支付宝</a>
	        </div>
        </c:if>
        <div class="qrcode-img-wrapper" data-role="qrPayImgWrapper">
            <div data-role="qrPayImg" class="qrcode-img-area">
                <div class="ui-loading qrcode-loading" data-role="qrPayImgLoading" style="display: none;"></div>
                <div style="position: relative;display: inline-block;">
                    <img id="show_qrcode" width="100%" height="100%" src="data:image/jpg;base64,${map.apiUrl}" style="display: block; width: 310px; height: 310px;">
                    <c:if test="${type eq '微信'}">
						<img onclick="$(&#39;#use&#39;).hide()" id="use" src="../images/logo_weixin.png" style="position: absolute;top: 50%;left: 50%;width:32px;height:32px;margin-left: -16px;margin-top: -16px">
					</c:if>
					<c:if test="${type eq '支付宝'}">
						 <img onclick="$('#use').hide()" id="use" src="../images/logo_alipay.png" style="position: absolute;top: 50%;left: 50%;width:32px;height:32px;margin-left: -16px;margin-top: -16px">
					</c:if>
                    <div id="qrcode" style="display: none;"></div>
                    <canvas id="imgCanvas" width="310" height="270" style="display: none;"></canvas>
                </div>
            </div>


        </div>
		<div style="font-size: 18px;color:#FF0000;">支付时请不要修改金额否则不能到帐</div>
        <div class="payweixinbtn" style="display: none;"><a href="./微信支付 - 闪入宝_files/qrcode.php" target="_blank" id="downloadbtn" class="btn btn-primary">1.先常按二维码图片保存到手机</a></div>
        
        <div class="payweixinbtn" style="display: none;padding-top: 10px"><a href="weixin://" class="btn btn-primary">2.打开${type}，扫一扫本地图片</a></div>
        
        <div class="iospayweixinbtn" style="padding-top: 15px;">1.长按上面的图片然后"存储图像"</div>
        <div class="iospayweixinbtn" style="padding-top: 15px;">
	        <c:if test="${type eq '微信'}">
				<a href="${JumpURL}" class="btn btn-primary">2.打开微信，扫一扫本地图片</a>
			</c:if>
			<c:if test="${type eq '支付宝'}">
				<a href="${JumpURL}" class="btn btn-primary">2.打开支付宝，扫一扫本地图片</a>
			</c:if>
        </div>
		
		
        <div class="paybtn" style="display: none;"><a href="weixin://" id="alipaybtn" class="btn btn-primary" target="_blank">启动微信App支付</a></div>

        
        <div class="time-item" style="padding-top: 10px">
            <div class="time-item" id="msg" style="color:#ff0000;"><h1>付款即时到账 未到账可联系我们-1</h1> </div><div class="time-item"><h1>订单:${orderNo}</h1> </div>			<strong id="hour_show"><s id="h"></s>0时</strong>
            <strong id="minute_show"><s></s>40分</strong>
            <strong id="second_show"><s></s>00秒</strong>
        </div>

        <div class="tip">
            <div class="ico-scan"></div>
            <div class="tip-text">
                <p id="showtext">打开${type} [扫一扫]</p>
            </div>
        </div>

        <div class="tip-text">
        </div>


    </div>
    <div class="foot">
        <div class="inner" style="display:none;">
            <p>手机用户可保存上方二维码到手机中</p>
            <p>在打开${type}扫一扫中选择“相册”即可</p>
            <p></p>
        </div>
    </div>
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
                checkdata();
            }
            
        }, 1000);
    }
    
    function jumpApp(){
    	var qrcde = "${alipayToBak}";
    	if(qrcde!='')
    		window.location.href = qrcde
    }
    
    $(document).ready(function(){
    	jumpApp()
    });
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
					$("#msg").html("<h1>即将<a href='http://149.129.79.167:8080/SUCCESS.do'>跳转</a>回商家页</h1>");
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
                        $('#show_qrcode').attr('src', "https://img.shanrubao-api.com/qrcode.php?url=https%3A%2F%2Fpay.shanrubao-api.com%2Falipay-api.php%3Fuid%3D293%26orderNo%3D1554530982%26money%3D100%26memo%3D2019040610001%26wxp%3D100"); 
                    }
                }else{
                    $('#show_qrcode').attr('src', "https://img.shanrubao-api.com/qrcode.php?url=https%3A%2F%2Fpay.shanrubao-api.com%2Falipay-api.php%3Fuid%3D293%26orderNo%3D1554530982%26money%3D100%26memo%3D2019040610001%26wxp%3D100"); 
                    // $('#downloadbtn').attr('href', "https://img.shanrubao-api.com/qrcode.php?url=https%3A%2F%2Fpay.shanrubao-api.com%2Falipay-api.php%3Fuid%3D293%26orderNo%3D1554530982%26money%3D100%26memo%3D2019040610001%26wxp%3D100");
                }
            }else{
                $('#show_qrcode').attr('src', "https://img.shanrubao-api.com/qrcode.php?url=https%3A%2F%2Fpay.shanrubao-api.com%2Falipay-api.php%3Fuid%3D293%26orderNo%3D1554530982%26money%3D100%26memo%3D2019040610001%26wxp%3D100");     
                // $('#downloadbtn').attr('href', "https://img.shanrubao-api.com/qrcode.php?url=https%3A%2F%2Fpay.shanrubao-api.com%2Falipay-api.php%3Fuid%3D293%26orderNo%3D1554530982%26money%3D100%26memo%3D2019040610001%26wxp%3D100");
            }
        }
        
    });


    //
</script>

</body></html>