<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){

	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	
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
			$scope.patTran=data;
			ZENG.msgbox._hide();
	    });
	}
	
	
	$scope.deleteMsg = function(i){
		$scope.hiring.msgList.splice(i,1);
	}
    
    $scope.submit = function(){
    	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
			if (data.state) {
				subm()
			}else{
				$('.popbox').show();
			}
		});
    	
    };
    $scope.inputUpperScore = function(target){
    	subm()
    }
    
    function subm(){
    	if($("#content").val()==''){ZENG.msgbox.show("请输入值", 5, 2000);return}
    	if($("#flag").val()=='0'){ZENG.msgbox.show("请选择状态", 5, 2000);return}
    	$http.get('../system/updateSystconfig.do?flag=' + $("#flag").val()+'&content='+$("#content").val()+'&id='+$("#id").val()+"&code="+$("#code").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
        		refreshList();
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
    		
        });
    }
    
}
$(document).ready(function (){
	
	$('.close').click(function() {
	      $('.popbox').hide();
	    })
	    
		  $('.qxbtn').click(function() {
	      $('.popbox').hide();
	    })
	
});

</script>