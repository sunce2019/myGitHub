<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	$http.get('getLoginNames.do',config).success(function(data, status, headers, config){
		$scope.users=data.obj;
    });
	$scope.addMsg = function(ss){
		ss.push("");
	}
	
	$scope.deleteMsg = function(i){
		$scope.hiring.msgList.splice(i,1);
	}
    
    $scope.submit = function(){
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	$http.get('../user/updatepassword.do?password1=' + $("#password1").val()+"&password2="+$("#password2").val()+"&password3="+$("#password3").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			alert(data.msg)
    			document.location = "../user/updateDetail.do";
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
    		
        });
    };
    
}


</script>