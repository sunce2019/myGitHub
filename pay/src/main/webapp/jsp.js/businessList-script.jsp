<%@page contentType="text/html; charset=UTF-8"%>
<script language="javascript">
	var currentPage = 1;
    
    function CampusController($scope, $http){
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	$http.get('getDate.do',config).success(function(data, status, headers, config){
    		$scope.patTran=data.payTran;
    		$scope.business = data.business;
    		$scope.customer = data.customer;
        });
    	refreshList(currentPage);
        $scope.editCampus = function(target){
            var campusId = target.getAttribute('data');
            openDetailIframe('businessUpdatePage.do?id=' + campusId);
        }
        
        $scope.openClose = function(target,index){
        	if($("#uid").val()=="")return;
        	
        	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
    			if (data.state) {
    				subm();
    			}else{
    				 $(".boxopen").show()
    		         $("#uidTypes").val(index)
    			}
    		});
        	
           
        }
  
		$scope.gotoPage = function(step){
			currentPage = currentPage + step;
			
			if(currentPage>totalPage){
				currentPage = totalPage
				return
			}
				
			if(currentPage<1){
				currentPage = 1
				return
			}
			refreshList(currentPage);
		}

		$scope.gotoFirst = function(){
            $('#listFirst').addClass('disabled');
            $('#listPrev').addClass('disabled');
            if(totalPage > 1){
                $('#listNext').removeClass('disabled');
                $('#listLast').removeClass('disabled');
            }
			refreshList(1);
	    }

        $scope.gotoLast = function(){
        	if($("#listLast").hasClass("disabled"))
				return;
            $('#listNext').addClass('disabled');
            $('#listLast').addClass('disabled');
            if(totalPage > 1){
            	$('#listFirst').removeClass('disabled');
                $('#listPrev').removeClass('disabled');
            }
            refreshList(totalPage);
        }

        $scope.refreshCampus = function(){            
            refreshList(currentPage);
        }
        
        $scope.query = function(){
        	code=$('#code').val();
        	currentPage=1;
        	refreshList(currentPage);
        }

		function refreshList(pageNo){
			ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
			currentPage = pageNo;
			$('#pageText').html(currentPage);
            $http.get('businessList.do?pageNo=' + pageNo +'&uid='+$("#uid").val()+'&flag='+$("#flag").val()+"&customer_id="+$("#customer_id").val()+"&name="+$("#name").val(),config).success(function(data, status, headers, config){
                $scope.totalCount = data.totalCount;//更新总记录数
                totalPage=data.totalPage//更新总页数
                $scope.users = data.obj;
                changePageNo();
                ZENG.msgbox._hide();
                try{if(data.ONEOPEN.state)$("#open").show();} catch (e) {}
                try{if(data.ONECLOSE.state)$("#close").show();} catch (e) {}
                try{if(data.ADDMERC.state)$("#edit").show();} catch (e) {}
            });
		}
		
		$scope.upperScore = function(target){
        	$('.urlshow').show();
        	var customerid = target.getAttribute('data');
        	$("#customerid").val(customerid);
        	
        	var businessid = target.getAttribute('businessid');
        	$("#businessid").val(businessid);
        	
        	var payType = target.getAttribute('payType');
        	$("#payType").val(payType);
        	
        }

		function changePageNo(){
			if(currentPage < 1){
				currentPage = 1;
			}
			if(currentPage == 1){
				$('#listFirst').addClass('disabled');
			    $('#listPrev').addClass('disabled');
			}else{
                $('#listFirst').removeClass('disabled');
                $('#listPrev').removeClass('disabled');
			}

			if(currentPage > totalPage){
				currentPage = totalPage;
			}
			if(currentPage == totalPage){
				$('#listNext').addClass('disabled');
                $('#listLast').addClass('disabled');
			}else{
                $('#listNext').removeClass('disabled');
                $('#listLast').removeClass('disabled');
			}
		}
		
		$scope.inputUpperScore = function(target){
			$http.get('../test/testApi.do?customerid=' + $("#customerid").val() +'&businessid='+$("#businessid").val()+'&price='+$("#money").val(),config).success(function(data, status, headers, config){
				var payType = $("#payType").val();
				
				if(payType.indexOf("跳转")!=-1){
					$("#spanID").html(data.obj)
				}else{
					window.open(data.obj);
				}
				$scope.number  = data.obj2;
				$(".showParam").show();
				
            });
		}
		
		$scope.showParam = function(target){
			window.open('../auto/showParam.do?number='+$scope.number);
		}
		
		$scope.onekey = function(target){
			subm()
		}
		
		function subm(){
			$http.get('onekey.do?code=' + $("#uid").val() +'&flag='+$("#uidTypes").val(),config).success(function(data, status, headers, config){
				if(data.state){
	    			ZENG.msgbox.show(data.msg, 4, 2000);
	    			$('.boxopen').hide();
	        		refreshList(1);
	    		}else{
	    			ZENG.msgbox.show(data.msg, 5, 2000);
	    		}
            });
		}
		
    }
    
$(document).ready(function(){
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
})
 
$(function() { 
 	var copy_name = new Clipboard('#spanID',{
        text: function() {
        	console.log($('#spanID').html())
            return $('#spanID').html();
        }
    });
 	
    copy_name.on('success', function(e) {
    	ZENG.msgbox.show("复制成功", 4, 2000);
        e.clearSelection();
    });
});

</script>