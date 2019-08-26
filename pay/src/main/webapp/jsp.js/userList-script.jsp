<%@page contentType="text/html; charset=UTF-8"%>
<script language="javascript">
	var currentPage = 1;
    
    function CampusController($scope, $http){
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	$http.get('../getDate.do',config).success(function(data, status, headers, config){
    		$scope.patTran=data.payTran;
    		$scope.business = data.business;
    		$scope.customer = data.customer;
        });
    	refreshList(currentPage);
        $scope.mining = function(target){
            var campusId = target.getAttribute('data');
            openDetailIframe('../user/userUpdatePage.do?id=' + campusId);
        }
        
        $scope.huidiao = function(target){
        	var campusId = target.getAttribute('data');
        	if(confirm("确认回调吗？")){
        		ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
                $http.get('manualCallBackGame.do?orderNumber=' + campusId,config).success(function(data, status, headers, config){
                	ZENG.msgbox._hide();
                	if(data.state){
            			ZENG.msgbox.show(data.msg, 4, 2000);
                		refreshList(currentPage);
            		}else{
            			ZENG.msgbox.show(data.msg, 5, 2000);
            		}
                });
        	}
            
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
			//if($("#listFirst").hasClass("disabled"))
			//	return;
				
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
			var config={ 'method':'GET',
	            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
	        	   };
            $http.get('../user/userList.do?pageNo=' + pageNo +'&loginname='+$("#loginname").val()+'&name='+$("#name").val(),config).success(function(data, status, headers, config){
                //$('#itemContText').html(data.totalCount);
                $scope.totalCount = data.totalCount;//更新总记录数
                totalPage=data.totalPage//更新总页数
                $scope.users = data.obj;
                changePageNo();
                ZENG.msgbox._hide();
                try{if(data.ADDUSER.state)$("#ADDUSER").show();} catch (e) {}
            });
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
    }
</script>