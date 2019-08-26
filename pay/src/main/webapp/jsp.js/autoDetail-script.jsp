<%@page contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">


function CampusController($scope, $http){
	ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
	var config={ 'method':'GET',
        	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
    	   };
	$scope.auto = "no";
	$http.get('autoModelLoad.do?id='+$("#id").val(),config).success(function(data, status, headers, config){
		ZENG.msgbox._hide();
		$scope.auto = data;
    });
	
	$scope.editCampus = function(target){
        var campusId = target.getAttribute('data');
        openDetailIframe('businessUpdatePage.do?id=' + campusId);
    }
	
	$scope.submit = function(){
    	if($("#uid").val()=='0'){ZENG.msgbox.show("请选择商户", 5, 2000);return}
    	
    	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
			if (data.state) {
				subm();
			}else{
				$('.boxopen').show();
			}
		});
    };
    
    $scope.test = function(target){
    	var o = angular.copy($scope.auto);
    	o.autoLoad = {};
    	$http.post('../test/auto.do',o,config).success(function(data, status, headers, config){
    		
        });
    }
    
    $scope.uidChange = function(target){
    	if($("#uid").val()=="" || $("#uid").val()=="0")return;
    	$http.post('getAuto.do?id='+$("#uid").val(),config).success(function(data, status, headers, config){
    		try {
    			if(JSON.stringify(data) != "{}"){
    				$scope.auto = data;
    			}
			} catch (e) {
				
			}
    		
        });
    }
    
    $scope.inputUpperScore = function(target){
    	subm()
    }
    
    function subm(){
    	var o = angular.copy($scope.auto);
    	o.autoLoad = {};
    	ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
    	var url = "addAuto.do";
    	if($("#id").val()!="" || $("#id").val()!="0")url = "updateAuto.do";
    	$http.post(url+'?code='+$("#code").val()+'&id='+$("#id").val(),o,config).success(function(data, status, headers, config){
    		ZENG.msgbox._hide();
    		if(data.state){
    			ZENG.msgbox.show(data.msg, 4, 2000);
        		refreshList();
    		}else{
    			ZENG.msgbox.show(data.msg, 5, 2000);
    		}
        });
    }
	
    
    $scope.addParam = function(){
    	$scope.auto.paramList.push({name:"",valType:"VARCHAR",type:"FIXED",val:"",flag:0,urlFlag:0})
    };
    
    
  // angular 不允许change事件 传递event  改用点击事件
    $scope.paramTypeChange = function(z,event){
    	changeVla(z.type,'FIXED',event)
    };
    $scope.sortChange = function(v,event){
    	changeVla(v,'CUSTOM',event)
    	if(v=='CUSTOM'){
    		$(".backsort").show();
    	}else{
    		$(".backsort").hide();
    	}
    };
    
    $scope.changeBack = function(v,event){
    	changeVla(v.type,'BACKSUC',event)
    };
    
    $scope.delParam = function(index){
    	$scope.auto.paramList.splice(index,1)
    };
    
    $scope.addbackParam = function(){
    	$scope.auto.backParamList.push({name:"",valType:"VARCHAR",type:"FIXED",val:"",flag:0,urlFlag:0})
    };
    $scope.delbackParam = function(index){
    	$scope.auto.backParamList.splice(index,1)
    };
    
    $scope.keySortChange = function(){
    	if($scope.auto.keySort=="0"){
    		$(".keyStr").show();
    	}else{
    		$(".keyStr").hide();
    	}
    };
    
    $scope.addLayer = function(){
    	$scope.auto.layerList.push({"val":""});
    };
    $scope.delLayer = function(index){
    	$scope.auto.layerList.splice(index,1)
    };
    
    $scope.addPayType = function(){
    	$scope.auto.payTypeList.push({"values":"","typeCode":"ZFB","jumpType":"FROMSUB"});
    };
    $scope.delPayType = function(index){
    	$scope.auto.payTypeList.splice(index,1)
    };
    
    function changeVla(val1,val2,event){
    	if(val1!=val2){
	    	$(event).next().hide();
	    	$(event).next().children(0).val("");
	    }else{
	    	$(event).next().show();
	    }
    }
    
}


$(document).ready(function (){
	
	$('.urlclose').click(function() {
	   	  $("#spanid").html("");
	   	  $("#money").val("");
	         $('.urlshow').hide();
	     })
	 	 $('.urlqx').click(function() {
	       $('.urlshow').hide();
	     })
	     
	     $('.opneclose').click(function() {
	         $('.boxopen').hide();
	     })
	 	 $('.qxclose').click(function() {
	       $('.boxopen').hide();
	     })
	
});


</script>