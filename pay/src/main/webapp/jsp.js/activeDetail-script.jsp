<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
	function CampusController($scope, $http) {
		var config = {
			'method' : 'GET',
			'Content-Type' : 'application/x-www-form-encoded;charset=UTF-8'
		};
		$scope.testUeditor = function() {
			var content = ue.getContent();
			console.log(content);
			//$http.post('testUeditor.do?content='+content,config).success(function(data, status, headers, config) {});
			document.location="testUeditor.do?content="+content;	
		}
		$scope.uidJS = function() {
			if ($scope.type == "0") {
				$scope.patTran = [];
				return;
			}
			ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
		}
		$http.get('activeModelLoad.do?id=' + $("#id").val(), config).success(
				function(data, status, headers, config) {
					$scope.list1 = data;
					ZENG.msgbox._hide();
				});
		
		$scope.refreshList1 = function() {
			document.location="active.do";		
		}

		$scope.addParam = function() {
			console.log($scope.list1);
			$scope.list1.push({
				"a" : ""
			});
		}

		$scope.deleteMsg = function(i) {
			$scope.list1.splice(i, 1);
		}

		$scope.submit = function() {
			$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
				if (data.state) {
					submit();
				}else{
					$('.popbox').show();
				}
			});
		};
		$scope.inputUpperScore = function(target) {	
			 var reg = new RegExp("^[0-9]*$");   	 
			   if($("#code").val().trim().length!=6 || !reg.test($("#code").val().trim())){
			    	alert("谷歌验证码必须为6位非负整数");			    	
			   }
			submit();
		}
		
		function submit(){
			var url = "addActive.do";
			var item = "";
			for (var i = 0; i < $scope.list1.length; i++) {
				if (i == $scope.list1.length - 1) {
					item += $scope.list1[i].a;
				} else {
					item += $scope.list1[i].a + ","
				}
			}
			var status=$("input[name=status]:checked").val();
			var content = ue.getContent();
			var img = $("#photoUrl").val();
			if ($("#id").val() != "")
				url = "updateActive.do";
			$http.get(
					url + '?id=' + $("#id").val() + '&title='
							+ $("#title").val() + '&content=' + encodeURIComponent(content)
							+ '&details=' + $("#details").val() + '&startTime='
							+ $("#startTime").val() + '&endTime='
							+ $("#endTime").val() + '&sort=' + $("#sort").val()
							+ "&code=" + $("#code").val() + "&list=" + item +"&img=" +encodeURIComponent(img)+ "&status=" + status,
					config).success(function(data, status, headers, config) {
				if (data.state) {
					ZENG.msgbox.show(data.msg, 4, 2000);					
					$scope.refreshList1();
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