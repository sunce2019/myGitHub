<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
	function CampusController($scope, $http) {
		var config = {
			'method' : 'GET',
			'Content-Type' : 'application/x-www-form-encoded;charset=UTF-8'
		};
		
		
		if($("#id").val()==""){
			$scope.backParamList=[{key:"",value:""}];
		}else{
			$http.get('../sms/getMoBan.do?id='+$("#id").val(), config).success(
					function(data, status, headers, config) {
						$scope.backParamList = data;
			});
		}
		
		$scope.addbackParam = function(){
	    	$scope.backParamList.push({key:"",value:""})
	    };
	    $scope.delbackParam = function(index){
	    	$scope.backParamList.splice(index,1)
	    };
	    
	   /*$scope.changeBack = function(v,event){
	    	changeVla(v.type,'BACKSUC',event)
	    };
	    function changeVla(val1,val2,event){
	    	if(val1!=val2){
		    	$(event).next().hide();
		    	$(event).next().children(0).val("");
		    }else{
		    	$(event).next().show();
		    }
	    } */
	    	    
	    $http.get('../sms/getRegx.do', config).success(
				function(data, status, headers, config) {
					$scope.regxType = data;
		});		
	   /*  $http.get('../sms/getBank.do', config).success(
				function(data, status, headers, config) {
					$scope.BankType = data;
		});	 */
		/* $http.get('../sms/messageDetail.do?id=' + $("#id").val(), config).success(
				function(data, status, headers, config) {			
					$scope.message = data;
					ZENG.msgbox._hide();
				});	 */
		//跳转到列表
		 $scope.refreshList = function() {
				
			window.location="../sms/messageInfo.do";			
		} 
		$scope.deleteMsg = function(i) {
			$scope.list1.splice(i, 1);
		}
		$scope.submit = function() {
			$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
				if (data.state) {
					subm();
				}else{
					$('.popbox').show();
				}
			});			
		};		
		$scope.inputUpperScore = function(target) {			
			subm();
		}		
		function subm(){
			url = "../sms/addMessage.do";
			if ($("#id").val() != "")
				url = "../sms/updateMessage.do";
			var list=$scope.backParamList;
			var modul="";
			for(var i=0;i<list.length;i++){
				modul += list[i].key + list[i].value;				
			}	
			var modul_json  =JSON.stringify($scope.backParamList);
			//获取模板	
			$http.get(
					url + '?id=' + $("#id").val()+"&content=" + $("#content").val() +"&code=" + $("#code").val()+"&modul="+encodeURIComponent(modul_json)+"&ip=" + $("#ip").val(),config).success(
			  function(data, status, headers, config) {
					if (data.state) {
						ZENG.msgbox.show(data.msg, 4, 2000);					
						$scope.refreshList();
					} else {
						ZENG.msgbox.show(data.msg, 5, 2000);
					}
			  });
		}
	}
	$(document).ready(function() {
		$('.close').click(function() {
			$('.popbox').hide();
		})
		$('.qxbtn').click(function() {
			$('.popbox').hide();
		})

	});
</script>