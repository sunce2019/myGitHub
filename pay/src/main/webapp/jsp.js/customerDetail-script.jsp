<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	$http.get('getDate.do',config).success(function(data, status, headers, config){
		$scope.patTran=data.payTran;
		$scope.business = data.business;
		$scope.customer = data.customer;
    });
	$scope.addMsg = function(ss){
		ss.push("");
	}
	
	$scope.deleteMsg = function(i){
		$scope.hiring.msgList.splice(i,1);
	}
    
    $scope.submit = function(){
    	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
			if (data.state) {
				subm();
			}else{
				$('.popbox').show();
			}
		});
    	
    };
    
    $scope.inputUpperScore = function(target){
    	subm()
    }
    
    function subm(){
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	if($("#name").val()==''){ZENG.msgbox.show("请输入商户名称", 5, 2000);return}	
    	if($("#ip").val()==''){ZENG.msgbox.show("请输入商户ip", 5, 2000);return}	
    	
    	var url = "saveCustomer.do";
    	if($("#id").val()!="")url = "updateCustomer.do";
    	$http.get(url+'?id=' + $("#id").val()+'&name='+$("#name").val()+'&flag='+$("#flag").val()+'&ip='+$("#ip").val()+'&callbackType='+$("#callbackType").val()+"&code="+$("#code").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
        		refreshList();
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
    		
        });
    }
}
$(document).ready(function(){
    $('.close').click(function() {
      $('.popbox').hide();
    })
    
	  $('.qxbtn').click(function() {
      $('.popbox').hide();
    })
})


</script>