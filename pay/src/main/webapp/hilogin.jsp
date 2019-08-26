<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>欢迎登录</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>

<link rel="stylesheet" type="text/css" href="../css/themes/icon.css"/>
<link rel="stylesheet" type="text/css" href="../css/login.css"/>
</head>

<body>
<div class="loginbox">
<h2>登录</h2>
	<div id="loginWin" class="easyui-window" title="登录" minimizable="false" maximizable="false" resizable="false" collapsible="false">
		<div class="easyui-layout" fit="true">
				<div region="center" border="false">
			<form id="loginForm" method="post">
				<div class="clearfloat list_it">
					<label for="login">帐号:</label>
					<input type="text" name="loginname" class="itxt"></input>
				</div>
				<div class="clearfloat list_it">
					<label for="password">密码:</label>
					<input type="password" name="pwd" class="itxt"></input>
				</div>
				<div class="clearfloat list_it">
					<label for="password">谷歌验证码:</label>
					<input type="text" name="Vcode" class="yzmitxt" ></input>
				</div>
				 <div id="showMsg"></div>
			</form>
				</div>
				<div region="south" border="false" class="loginbtn">
					<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="login()">登录后台</a>
				</div>
		</div>
	</div>

</div>
</body>

<script type="text/javascript">
document.onkeydown = function(e){
    var event = e || window.event;  
    var code = event.keyCode || event.which || event.charCode;
    if (code == 13) {
        login();
    }
}
$(function(){
    $("input[name='login']").focus();
});
function cleardata(){
    $('#loginForm').form('clear');
}
function login(){
	 $("#showMsg").html("");
     if($("input[name='login']").val()=="" || $("input[name='password']").val()==""|| $("input[name='Vcode']").val()==""){
         $("#showMsg").html("用户名或密码 谷歌验证码为空，请输入");  
         $("input[name='login']").focus();
     }
     if(!$("input[name='Vcode']").val().trim()==""){
    	 var reg = new RegExp("^[0-9]*$");   	 
	     if($("input[name='Vcode']").val().trim().length!=6 || !reg.test($("input[name='Vcode']").val().trim())){
	    	 $("#showMsg").html("谷歌验证码必须为6位非负整数");
	    	 $("input[name='Vcode']").focus();
	      }else{
	            //ajax异步提交  
	           $.ajax({            
	                  type:"POST",   //post提交方式默认是get
	                  url:"../login/login.do", 
	                  data:$("#loginForm").serialize(),   //序列化               
	                  error:function(request) {      // 设置表单提交出错         
	                      $("#showMsg").html(request);  //登录错误提示信息
	                  },
	                  success:function(data) {
	                	  console.log(data)
	                	  if(data.state){
	                		  document.location = "index.do";
	                	  }else{
	                		  alert(data.msg);
	                	  }
	                  }            
	            });       
	        } 
     }
}

</script>
</html>