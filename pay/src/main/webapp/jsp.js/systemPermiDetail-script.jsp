<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	$scope.groups = 0;
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	
	$http.get('../user/groupsLists.do',config).success(function(data, status, headers, config){
        $scope.groupsList = data;
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
    	var list = $("#tt").tree("getChecked");
    	var checked = "";
    	list.forEach(function(o){checked+=o.dbid+",";});
    	checked = checked.substring(0,checked.length-1);
    	$http.get('addSystemPermiOrUpdate.do?id='+$("#groups").val()+"&menuid="+checked+"&code="+$("#code").val(),config).success(function(data, status, headers, config){
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
    			$('.popbox').hide();
    			$("#code").val("");
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

function groupsChange(){
	if($("#groups").val()=="0")return;
	$("#tt").tree({
	    url: "menuList.do?groupsId="+$("#groups").val(),
	    required: false,
	    panelHeight: 120,
	    cascadeCheck:false,
	    checkbox:true
	});
}



</script>