<%@page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html ng-app>


  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><s:text name="sitetitle"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="../css/bootstrap.css" rel="stylesheet">
    <link href="../css/bootstrap-responsive.css" rel="stylesheet">
    <link href="../css/ui.css" rel="stylesheet">
    <link href="../css/hooraylibs.css" rel="stylesheet">
    <link href="../css/main.css" rel="stylesheet">
    <link rel="stylesheet" href="../js/css/zTreeStyle/zTreeStyle.css" />
    <style>
	</style>
    <!--[if lte IE 6]>
    <link rel="stylesheet" type="text/css" href="../css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="../css/ie.css">
    <![endif]-->
  </head>
  <body>
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="../js/html5.js"></script>
    <![endif]-->
    <script type="text/javascript" src="../js/jquery.js"></script>
    <script type="text/javascript" src="../js/angular.js"></script>
    <script type="text/javascript" src="../js/bootstrap/bootstrap-tooltip.js"></script>
    <script type="text/javascript" src="../js/bootstrap/bootstrap-ie.js"></script>
    <script type="text/javascript" src="../js/artDialog/jquery.artDialog.js?skin=simple"></script>
    <script type="text/javascript" src="../js/artDialog/plugins/iframeTools.js"></script>
    <script type="text/javascript" src="../js/Validform.js"></script>
    <script type="text/javascript" src="../js/jquery.form.js"></script>
    <script type="text/javascript" src="../js/hooraylibs.js"></script>
    <script type="text/javascript" src="../js/jquery.ztree.all-3.5.min.js"></script>
    <script type="text/javascript" src="../js/jquery.validate.min.js"></script>
    <script type="text/javascript" src="../js/validateforgb.js"></script>
    <script type="text/javascript" src="../js/commonFormSubmit.js"></script>
    <script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js" ></script>
    <script type="text/javascript">
    function isNull(exp){
    	if(exp==undefined || exp==null || exp=="")
    		return true
    	return false;
    }
	    $().ready(function(){
	        //配置artDialog全局默认参数
	        (function(config){
	            config['lock'] = true;
	            config['fixed'] = true;
	            config['resize'] = false;
	            config['background'] = '#000';
	            config['opacity'] = 0.5;
	        })($.dialog.defaults);
	        //toolTip
	        $('[rel="tooltip"]').tooltip();
	        //表单提示
	        $("[datatype]").focusin(function(){
	            $(this).parent().addClass('info').children('.infomsg').show().siblings('.help-inline').hide();
	        }).focusout(function(){
	            $(this).parent().removeClass('info').children('.infomsg').hide().siblings('.help-inline').show();
	        });
	        //detailIframe
	        openDetailIframe = function(url){
	            ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
	            $('#detailIframe iframe').attr('src', url).load(function(){
	                $('body').css('overflow', 'hidden');
	                ZENG.msgbox._hide();
	                $('#detailIframe').animate({
	                    'top' : 0,
	                    'opacity' : 'show'
	                }, 500);
	            });
	        };
	        closeDetailIframe = function(callback){
	            $('body').css('overflow', 'auto');
	            $('#detailIframe').animate({
	                'top' : '-100px',
	                'opacity' : 'hide'
	            }, 500, function(){
	                callback && callback();
	            });
	        };
	    });
    </script>
     <div class="well-small">
      <tiles:insertAttribute name="content"></tiles:insertAttribute>
    </div>
    <div id="detailIframe" style="background:#fff;position:fixed;z-index:1;top:-100px;left:0;width:100%;height:100%;display:none">
      <iframe frameborder="0" style="width:100%;height:100%"></iframe>
    </div>
    <tiles:insertAttribute name="script"></tiles:insertAttribute>
  </body>
</html>