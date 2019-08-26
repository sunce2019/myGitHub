<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">

function CampusController($scope, $http){
	$scope.groups = "0";
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	$http.get('../getDate.do',config).success(function(data, status, headers, config){
		$scope.patTran=data.payTran;
		$scope.business = data.business;
		$scope.customer = data.customer;
    });
	$scope.addMsg = function(ss){
		ss.push("");
	}
	
	$http.get('../user/groupsLists.do?id='+$("#id").val(),config).success(function(data, status, headers, config){
        $scope.groupsList = data;
    });
	
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
    	
    	var url = "saveUser.do";
    	if($("#id").val()!="")url = "updateUser.do";
    	$http.get(url+'?loginname=' + $("#loginname").val()+'&name='+$("#name").val()+'&flag='+$("#flag").val()+"&id="+$("#id").val()+"&password="+$("#password").val()+"&code="+$("#code").val()+"&groups="+$("#groups").val(),config).success(function(data, status, headers, config){
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

function changeCustomer(){
	if($("#type").val()=="0" || $("#type").val()=="1"){
		$("#customer_id").val("0");
		$(".customerCss").hide();
	}else{
		$(".customerCss").show();
	}
}



</script>