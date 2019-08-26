<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>富文本框展示</title>
</head>
<body>
<div>
  ${content}
</div>  

	 <% String ref = request.getHeader("Referer"); %>
	 <a href="javascript:window.location='<%=ref%>'">返回</a>
</body>
</html>