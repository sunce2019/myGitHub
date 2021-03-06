<%@page contentType="text/html; charset=UTF-8"%>
<script language="javascript">
	var currentPage = 1;
    
    function CampusController($scope, $http){
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	
    	/* $http.get("http://149.129.79.167:8080/plan/getGameType?type=1", config).success(
    			function(data, status, headers, config) {
    				console.log(data);
    			});
    	 */
    	refreshList(currentPage);
        $scope.editCampus = function(target){
            var campusId = target.getAttribute('data');
            //openDetailIframe('activeUpdatePage.do?id=' + campusId);
            document.location = 'activeUpdatePage.do?id=' + campusId;
            
        }
        $scope.huidiao = function(target){
        	var campusId = target.getAttribute('data');
        	$http.get('../system/checkGoogle.do',config).success(function(data, status, headers, config) {
				if (data.state) {
					$("#upperId").val(campusId);  
					subm();
				}else{					 
			         $('.popbox').show();
			         $("#upperId").val(campusId)  
				}
			});
        }       

        $scope.getCode = function(){
           alert( $("$code").src );           
        } 
        $scope.inputUpperScore = function(target){
        	subm();
        }
        
        function subm(){
        	if(confirm("确认删除吗？")){
            	$http.get('deleteactive.do?id=' + $("#upperId").val()+"&code="+$("#code").val(),config).success(function(data, status, headers, config){
            		$('.popbox').hide();
                	$("#upperId").val("")
                	$("#code").val("")
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
            $http.get('activeList.do?pageNo=' + pageNo +'&title='+$("#title").val()+'&startTime='+$("#startTime").val()+"&endTime="+$("#endTime").val()+"&status="+$("#flag").val(),config).success(function(data, status, headers, config){
                //$('#itemContText').html(data.totalCount);
                $scope.totalCount = data.totalCount;//更新总记录数
                totalPage=data.totalPage//更新总页数
                $scope.users = data.obj;
                //console.log(data.obj[0].title+" "+data.obj[0].id);
                changePageNo();
                ZENG.msgbox._hide();
                try{if(data.ADDactive.state)$("#ADDactive").show();} catch (e) {}
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
    
    $(document).ready(function (){    	
    	$('.close').click(function() {
    	      $('.popbox').hide();
    	    })    	    
    		  $('.qxbtn').click(function() {
    	      $('.popbox').hide();
    	    })   	
    });
</script>