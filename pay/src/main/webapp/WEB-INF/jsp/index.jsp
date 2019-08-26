<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html ng-app>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>v6</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="../css/bootstrap.css" rel="stylesheet">
    <link href="../css/bootstrap-responsive.css" rel="stylesheet">
    <link href="../css/main.css?time=888" rel="stylesheet">
	<link href="../css/font.css" rel="stylesheet">
    <link href="../css/font-awesome.min.css" rel="stylesheet" >
    <link href="../css/leftnav.css?d=12" rel="stylesheet" />
    <script type="text/javascript" src="../js/jquery.js"></script>
    <script type="text/javascript" src="../js/angular.js"></script>
    <!--[if lte IE 6]>
    <link rel="stylesheet" type="text/css" href="../css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="../css/ie.css">
    <![endif]-->
  </head>
<body>
  <div id="wrap">
    <div class="navbar navbar-inverse" style="margin:0px;"> 
      <div class="navbar-inner">
	  <span class="left_open"><i class="iconfont">&#xe6fa;</i>欢迎进入后台</span>
        <div class="pull-left container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar">1</span>
            <span class="icon-bar">2</span>
            <span class="icon-bar">3</span>
          </button>
        </div>
      </div>
    </div> 
    <div class="container-fluid" style="margin-top:10px;">
	  <div class="row-fluid">
	    <div class="span2" >
	      <div class="left-nav accordion" id="accordion">
	        <div class="accordion-group">
	          <div id="baseinfoCollapse" class="accordion-body collapse in">
	            <div ng-controller='controller' class="accordion-inner">
	            <ul  id="nav_menu" class="nav_menu">  
	           		<!--    <c:if test="${user.type==1 || user.type==2}">
	                  	<li><a href="../payorder.do" target="mainFrame"><i class="iconfont">&#xe69e;</i>订单查询</a></li>
	                  </c:if>
	                  <c:if test="${user.type==3}">
	                  	<li><a href="../businessPayOrder.do" target="mainFrame"><i class="iconfont">&#xe69e;</i>订单查询</a></li>
	                  </c:if>
	                  <c:if test="${user.type==1}">
		                  <li><a href="../business.do" target="mainFrame"><i class="iconfont">&#xe6b8;</i>商户管理</a></li>
		                  <li><a href="../auto.do" target="mainFrame"><i class="iconfont">&#xe6b8;</i>自动对接</a></li>
		                  <li><a href="../customer.do" target="mainFrame"><i class="iconfont">&#xe726;</i>接入管理</a></li>
		                  <li><a href="../user/user.do" target="mainFrame"><i class="iconfont">&#xe753;</i>账号管理</a></li>
		                  <li><a href="../ip.do" target="mainFrame"><i class="iconfont">&#xe6eb;</i>ip白名单</a></li>
		                  <li><a href="../bank.do" target="mainFrame"><i class="iconfont">&#xe753;</i>银行管理</a></li>
		                  <li><a href="../cloudFlasHover/cloud.do" target="mainFrame"><i class="iconfont">&#xe723;</i>云闪付码</a></li>
		                  <li><a href="../wechat/wechat.do" target="mainFrame"><i class="iconfont">&#xe723;</i>微信买单</a></li>
	                  </c:if>
	                  <li><a href="../user/updateDetail.do" target="mainFrame"><i class="iconfont">&#xe82b;</i>修改密码</a></li>		  
	                  <li>
							<a href="#" class="link"><i class="iconfont">&#xe6ae;</i>系统设置</a>
							<dl class="submenu">
								<dd><a href="../system/groups.do" target="mainFrame">角色管理</a></dd>
								<dd><a href="../system/systemPermiDetail.do" target="mainFrame">权限管理</a></dd>
							</dl>
                     </li>-->
	                 <li><a href="../user/out.do" ><i class="iconfont">&#xe6ae;</i>退出系统</a></li>
	                 
	            </ul>
	            </div>
	          </div>
	        </div>
	      </div>
	    </div>
		
		<div class="page-content">
			<div class="span10" id="divFrame"  style="width:100%;height: 100%;"><iframe id="mainFrame" name="mainFrame" width="100%" frameborder="0"></iframe></div>
		</div>
		
	  </div>
	  <div id="push"></div>
	</div>
  </div>

  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
  <script src="../js/html5.js"></script>
  <![endif]-->
  <script src="../js/jquery.js"></script>
  <script type="text/javascript" src='../js/leftnav.js'></script>
  <script type="text/javascript" src="../js/bootstrap/bootstrap-collapse.js"></script>
  <script type="text/javascript" src="../js/bootstrap/bootstrap-ie.js"></script>
  
  <script language="javascript">
      $().ready(function(){
          $('#mainFrame').height($('#wrap').height() - 110);
      });
	  $('.left_open i').click(function(event) {
        if ($('.left-nav').css('left') == '0px') {
            $('.left-nav').animate({
                left: '-200px'
            },
            100);
            $('.page-content').animate({
                left: '20px'
            },
            100);        
        } else {
            $('.left-nav').animate({
                left: '0px'
            },
            100);
            $('.page-content').animate({
                left: '150px'
            },
            100);
        }
    });
	  var menuHTML=""
	  function controller($scope, $http){
		  var config={ 'method':'GET',
	            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
	        	   };
		  $http.get('../system/getUsersMenu.do',config).success(function(data, status, headers, config){
			  $.each(data,function(i,item){
				  if(item.parentid==0){
					  if(item.url!="#"){
						  menuHTML+='<li><a href="'+item.url+'" target="mainFrame"><i class="iconfont">&#xe6b8;</i>'+item.name+'</a></li>';
					  }else{
						  showMenu(item);
					  }
				  }
			　　});
			  $(".nav_menu").html(menuHTML)
          });
	  }
	  
	  function showMenu(data){
		  menuHTML += '<li><a href="#" class="link" onclick="leftClick(this)"><i class="iconfont" >&#xe6ae;</i>'+data.name+'</a><ul class="two_bar submenu" style="display: none;">';
		  $.each(data.menuList,function(i,item){
			  if(item.url!="#"){
				  menuHTML+='<li><a href="'+item.url+'" target="mainFrame">'+item.name+'</a><li>';
			  }else{
				  showMenu(item.menuList)
			  }
		　　});
		  menuHTML+='</ul></li> ';
	  }
	  
	  function leftClick (this_) {
		  $(this_).next().slideToggle();
          $(this_).parent().siblings().children("ul").slideUp();
		};
  </script>
</body>
</html>
