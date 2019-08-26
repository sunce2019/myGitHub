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
    	if($("#useTime").val()==''){ZENG.msgbox.show("请输入银行卡使用时间范围", 5, 2000);return}
    	var url = "addBank.do";
    	if($("#id").val()!="")url = "updateBank.do";
    	$http.get(url+'?cardNo=' + $("#cardNo").val()+'&bankAccount='+$("#bankAccount").val()+'&bankName='+$("#bankName").val()+'&bankMark='+$("#bankMark").val()+'&flag='+$("#flag").val()+'&cardIndex='+$("#cardIndex").val()+'&maxLimit='+$("#maxLimit").val()+'&remarks='+$("#remarks").val()+'&id='+$("#id").val()+"&useTime="+$("#useTime").val()+"&tail="+$("#tail").val()+"&code="+$("#code").val()+"&customerid="+$("#customerid").val()+"&pattern="+$("input[name='pattern']:checked").val(),config).success(function(data, status, headers, config){
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