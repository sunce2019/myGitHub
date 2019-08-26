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
    	
    	$http.get('addBankOrUpdate.do?cardNo=' + $("#cardNo").val()+'&bankAccount='+$("#bankAccount").val()+'&bankName='+$("#bankName").val()+'&bankMark='+$("#bankMark").val()+'&flag='+$("#flag").val()+'&cardIndex='+$("#cardIndex").val()+'&maxLimit='+$("#maxLimit").val()+'&remarks='+$("#remarks").val()+'&id='+$("#id").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
        		refreshList();
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
    		
        });
    };
    
}


</script>