<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<meta name="viewport" content="width=device-width,user-scalable=yes, minimum-scale=0.4, initial-scale=0.8,target-densitydpi=low-dpi" />
<meta content="telephone=no" name="format-detection" />
<link rel="stylesheet" type="text/css" href="css/style.css">
<title>支付成功</title>
<script type="text/javascript">
// 根据设计稿的宽度来传参 比如640 750 1125  然后制作稿跟PC上一样的制作就行
!function(e){if(/Android(?:\s+|\/)(\d+\.\d+)?/.test(navigator.userAgent)){var t=parseFloat(RegExp.$1);if(t>2.3){var i=parseInt(window.screen.width)/e;document.write('<meta name="viewport" content="width='+e+",minimum-scale="+i+",maximum-scale="+i+', target-densitydpi=device-dpi">')}else{document.write('<meta name="viewport" content="width='+e+',target-densitydpi=device-dpi">')}}else{document.write('<meta name="viewport" content="width='+e+',user-scalable=no,target-densitydpi=device-dpi,minimal-ui,viewport-fit=cover">')}}(750);
</script>
</head>
<body>
	<div class="main">
		<header>支付成功</header>
		<div class="succ_cont">
			<div class="suc_img">
				<img src="images/1.png" width="410" height="410">
			</div> <!-- en suc_img -->
			<p>您已成功支付</p>
		</div>   <!-- end cont -->
	</div> <!--  end  main -->
</body>
</html>