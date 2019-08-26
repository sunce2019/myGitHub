<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
	function CampusController($scope, $http) {
		var config = {
			'method' : 'GET',
			'Content-Type' : 'application/x-www-form-encoded;charset=UTF-8'
		};
		$scope.uidJS = function() {
			if ($scope.type == "0") {
				$scope.patTran = [];
				return;
			}
			ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
		}
				
		$http.get('discountsSpeldLoad.do?id=' + $("#id").val(), config).success(
				function(data, status, headers, config) {
					console.log(22);
					$scope.listSpeild = data;
					ZENG.msgbox._hide();
				});	
		//跳转到列表
		 $scope.refreshList = function() {
			window.location="discounts.do";			
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
			subm()
		}
		
		function subm(){
			var status=$("input[name=status]:checked").val();
			if ($("#id").val() != "")
				url = "updateDiscounts.do";
			$http.get(
					url + '?id=' + $("#id").val()+"&status=" +status +"&code=" + $("#code").val(),config).success(function(data, status, headers, config) {
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