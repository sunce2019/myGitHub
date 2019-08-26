<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){

	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	
    $scope.submit = function(){
    	if($("#name").val()==''){ZENG.msgbox.show("请输入商户名字", 5, 2000);return}
    	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
			if (data.state) {
				subm();
			}else{
				$('.popbox').show(); 
			}
		});
    	
    	
    };
    $scope.inputUpperScore = function(target){
    	subm();
    }
    function subm(){
    	var url = "addGroups.do";
    	if($("#id").val()!="")url = "updateGroups.do";
    	$http.get(url+'?name=' + $("#name").val()+"&code="+$("#code").val()+"&id="+$("#id").val()+"&remarks="+$("#remarks").val(),config).success(function(data, status, headers, config){
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