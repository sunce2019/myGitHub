<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
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
    	
    	var url = "addIp.do";
    	if($("#id").val()!="")url = "addIp.action";
    	$http.get(url+'?remarks=' + $("#remarks").val()+'&ip='+$("#ip").val()+"&code="+$("#code").val(),config).success(function(data, status, headers, config){
    		$('.popbox').hide();
        	$("#upperId").val("")
        	$("#code").val("")
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