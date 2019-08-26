<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	$scope.orderType = "0";
	$scope.customerid = "0";
	var a = "${business.customer_id}";
	if(a!="")$scope.customerid = a;
	
	$http.get('getDate.do',config).success(function(data, status, headers, config){
		$scope.business = data.business;
		var index = 0;
		$scope.customer = data.customer;
		$scope.bank = data.bank;
		$scope.patTran = data.payTran;
		$scope.patTrans = data.payTran;
		ZENG.msgbox._hide();
		$scope.type="0";
    });
	$scope.addMsg = function(ss){
		ss.push("");
	}
	
	
	$scope.uidJS = function(){
		if($scope.type=="0"){
			$scope.patTran = [];
			return;
		}
		ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
		$http.get('../api/getPayTypeApi.do?type='+$scope.type,config).success(function(data, status, headers, config){
			//$scope.patTran=data;
			$scope.orderType = "0";
			ZENG.msgbox._hide();
	    });
		loadBank();
	}
	
	$scope.changeorderType = function(){
		loadBank();
	}
	$scope.customerchange = function(){
		loadBank();
	}
	
	if($("#id").val()!='0'){
		$http.get('../loadBank.do?payType='+$scope.orderType+'&customerid='+$("#customerid").val()+"&id="+$("#id").val(),config).success(function(data, status, headers, config){
			$scope.bankList=data;
	    });
	}
	
	function loadBank(){
		var payType = '${business.payType}';
		if(payType=='')payType = $scope.orderType;
		if($("#uid").val()=="SUBAO" && (payType=="支付宝转银行" || payType=="微信转银行" 
				|| payType=="云闪付" || payType=="微信买单") && $("#customerid").val()!='0'){
			$(".bank").show();
		}else{
			$(".bank").hide();
			return
		}
		
		ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
		$http.get('../loadBank.do?payType='+$scope.orderType+'&customerid='+$("#customerid").val(),config).success(function(data, status, headers, config){
			$scope.bankList=data;
			
			ZENG.msgbox._hide();
	    });
	}
	
	$scope.deleteMsg = function(i){
		$scope.hiring.msgList.splice(i,1);
	}
    
    $scope.submit = function(){
    	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
			if (data.state) {
				subm();
			}else{
				if($('input[name="auto"]:checked').val()=="yes"){
		    		if($("#name").val()==''){ZENG.msgbox.show("请输入商户名称", 5, 2000);return}
		    	}else{
		    		if($("#uid").val()=='0'){ZENG.msgbox.show("请选择商务名称", 5, 2000);return}
		    	}
		    	
		    	if($("#orderType").val()=='0'){ZENG.msgbox.show("请选择支付类型", 5, 2000);return}	
		    	if($("#payCode").val()==''){ZENG.msgbox.show("请输入支付编码", 5, 2000);return}
		    	if($("#id").val()=='' && $("#uids").val()==''){ZENG.msgbox.show("请选择商务号", 5, 2000);return}
		    	if($("#customer").val()=='0'){ZENG.msgbox.show("请选择所属用户", 5, 2000);return}
		    	if($("#id").val()!='' && $("#token").val()==''){ZENG.msgbox.show("请输入商务秘钥", 5, 2000);return}
		    	if($("#id").val()!='' && $("#apiUrl").val()==''){ZENG.msgbox.show("请输入api地址", 5, 2000);return}
		    	if($("#maxLimit").val()==''){ZENG.msgbox.show("请输入每日最大额度", 5, 2000);return}	
		    	var maxLimit = $("#maxLimit").val()
		    	if(parseInt(maxLimit)>9999999){ZENG.msgbox.show("最大额度必须小于等于9999999", 5, 2000);return}
		    	if(parseInt($("#range_min").val())<1 ||parseInt($("#range_min").val())>9999999){ZENG.msgbox.show("请输入正确的范围最小金额", 5, 2000);return}	
		    	if(parseInt($("#range_max").val())<1 ||parseInt($("#range_max").val())>9999999){ZENG.msgbox.show("请输入正确的范围最大金额", 5, 2000);return}	
		    	if($("#vip").val()==''){ZENG.msgbox.show("请选择VIP", 5, 2000);return}
		    	$('.popbox').show();
			}
		});
    	
    };
    
    $scope.autoChange = function(target){
    	var this_ = $('input[name="auto"]:checked').val();
    	if (this_ == 'no') {
	        $(".no").show();
	        $(".yes").hide();
	        $scope.patTran = [];
	    }
	    else if (this_ == 'yes') {
	    	$(".no").hide();
	        $(".yes").show();
	        $(".ysf").hide();
			$(".bank").hide();
			$(".wxmd").hide();
	        $scope.patTran = $scope.patTrans;
	    }
    }
    
    $scope.inputUpperScore = function(target){
    	subm()
    }
    
    function subm(){
    	//return
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	
    	var url = "saveBusiness.do";
    	if($("#id").val()!="")url = "updateBusiness.do";
    	$http.get(url+'?id=' + $("#id").val()+'&uid='+$("#uid").val()+'&orderType='+$("#orderType").val()+'&maxLimit='+$("#maxLimit").val()+'&flag='+$("#flag").val()+'&fixed_price='+$("#fixed_price").val()+'&range_min='+$("#range_min").val()+'&range_max='+$("#range_max").val()+"&uids="+$("#uids").val()+"&token="+$("#token").val()+"&customer="+$("#customerid").val()+"&public_key="+encodeURIComponent($("#public_key").val())+"&private_key="+encodeURIComponent($("#private_key").val())+"&bankId="+$("#bankId").val()+"&callbackip="+$("#callbackip").val()+"&apiUrl="+$("#apiUrl").val()+"&code="+$("#code").val()+"&vip="+$("#vip").val()+"&bankPattern="+$("#bankPattern").val()+"&name="+$("#name").val()+"&payCode="+$("#payCode").val()+"&jumpType="+$("#jumpType").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
        		refreshList(1);
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
    		
        });
    }
    
}


$(document).ready(function (){
	
	var str="${businessids}";//后台取得的数据，可以ajax或获取隐藏域的值
	if(str!=''){
		var arr=str.split(',');
		var sel=document.getElementById("bankId");
		var len=sel.options.length;
		for(var i=0;i<arr.length;i++){
		    for(var j=0;j<len;j++){
		        if(sel.options[j].value==arr[i]){
		            sel.options[j].setAttribute("selected",true);
		            break;
		        }
		    }
		}
	}
	
	$('.close').click(function() {
	      $('.popbox').hide();
	    })
	    
		  $('.qxbtn').click(function() {
	      $('.popbox').hide();
	    })
	    
	
});



</script>