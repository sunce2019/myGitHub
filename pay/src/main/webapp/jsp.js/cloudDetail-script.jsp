<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){

	var config = {
			'method' : 'GET',
			'Content-Type' : 'application/x-www-form-encoded;charset=UTF-8'
		};
	$scope.addMsg = function(ss){
		ss.push("");
	}
	$scope.refreshList = function() {
		document.location="cloud.do";		
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
				if($("#cardNo").val()==''){ZENG.msgbox.show("请输入银行卡号", 5, 2000);return}
		    	if($("#bankAccount").val()==''){ZENG.msgbox.show("请输入银行开户人", 5, 2000);return}
		    	if($("#bankName").val()==''){ZENG.msgbox.show("请输入银行名称", 5, 2000);return}
		    	if($("#maxLimit").val()==''){ZENG.msgbox.show("请输入每日上限金额", 5, 2000);return}
		    	if($("#useTime").val()==''){ZENG.msgbox.show("请输入银行卡使用时间范围", 5, 2000);return}
		    	$('.popbox').show();
			}
		});
    	
    };
    $scope.inputUpperScore = function(target){
    	subm()
    }
    
    function subm(){
    	var fd = new FormData();
    	var file = angular.element("#fils")[0].files[0];
        fd.append("file",file)
        var url = "addCloud.do";
    	if($("#id").val()!="")url = "updateCloud.do";
        $http.post(url+'?cardNo=' + $("#cardNo").val()+'&bankAccount='+$("#bankAccount").val()+'&bankName='+$("#bankName").val()+'&flag='+$("#flag").val()+'&maxLimit='+$("#maxLimit").val()+'&remarks='+$("#remarks").val()+'&id='+$("#id").val()+"&useTime="+$("#useTime").val()+"&code="+$("#code").val()+"&tail="+$("#tail").val()+"&pattern="+$("#pattern").val()+"&customerid="+$("#customerid").val(), fd, {
            transformRequest: angular.identity,
            headers: { "Content-Type": undefined }
          })
          .success(function(data){
        	  if(data.state){
      			ZENG.msgbox.show(data.msg, 4, 2000);
          		refreshList();
      		}else{
      			ZENG.msgbox.show(data.msg, 5, 2000);
      		}
          })
          .error( function(){
            // blabla...
          })
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