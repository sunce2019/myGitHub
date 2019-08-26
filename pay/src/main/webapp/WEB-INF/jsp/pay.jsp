<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<title>支付中请稍等。。。</title>
</head>
<body>
	<form action="${apiUrl}" method="POST" id="formid"> 
		<c:forEach items="${map}" var="mymap" > 
			<input type="hidden" name="${mymap.key}" value="${mymap.value}" />
		</c:forEach>
	<!-- <input type="submit" id="sub" value="3秒后自动提交" /> -->	
	</form>
	<c:if test="${jumpType==3}">
		<img alt="" src="data:image/jpg;base64,${apiUrl}">
	</c:if>
	
</body>
<script type="text/javascript">
if("${jumpType}"=="1"){
	document.getElementById("formid").submit();
}else if("${jumpType}"=="2"){
	window.location='${apiUrl}';
}
//var time = 3;
//var flag = false;
//setInterval("rediTime()", 1000);
//setInterval("redirect()", 3000); 
//function redirect(){ 
//	if(flag)return;
//	flag = true;
//	if("${jumpType}"=="1"){
//		document.getElementById("formid").submit();
//	}else if("${jumpType}"=="2"){
//		window.location='${apiUrl}';
//	}
//} 
//function rediTime(){
//	time = time - 1
//	document.getElementById("sub").value=time+"秒后自动提交";
//}
	
</script>
</html>