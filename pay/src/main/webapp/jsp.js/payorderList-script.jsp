<%@page contentType="text/html; charset=UTF-8"%>
<script language="javascript">
	var currentPage = 1;
    var useragentHTML="";
    function CampusController($scope, $http,$interval){
    	$scope.upperId = "";
    	$scope.upperMoney = "";
    	var config={ 'method':'GET',
            	'Content-Type': 'application/x-www-form-encoded;charset=UTF-8'
        	   };
    	$http.get('getDate.do',config).success(function(data, status, headers, config){
    		$scope.patTran=data.payTran;
    		$scope.business = data.business;
        });
    	$http.get('getOrderDate.do',config).success(function(data, status, headers, config){
    		$scope.customer = data;
        });
    	var p;
    	$scope.auto = function(target){
    		$interval.cancel(p);
    		if($scope.autos!='0'){
    			p = $interval(function() {
        			refreshList(currentPage);
            	}, $scope.autos);
    		}
        }
    	
    	refreshList(currentPage);
        $scope.mining = function(target){
            var campusId = target.getAttribute('data');
            openDetailIframe('wechatDetail.action?id=' + campusId);
        }
        
        $scope.useragent = function(target){
            var campusId = target.getAttribute('data');
            useragentHTML = $(".itxt").html();
            $(".itxt").html(campusId);
            $('.popbox').show();
            
        }
        
        $scope.download = function(target){
        	var a = $("#uses").val()==''?"0":$("#uses").val();
        	var url = 'download_excel.do?pageNo=1&orderNumber='+$("#orderNumber").val()+'&userName='+$("#userName").val()+'&uid='+$("#uid").val()+'&customer_id='+$("#customer_id").val()+'&orderType='+$("#orderType").val()+'&flag='+$("#flag").val()+'&gameOrderNumber='+$("#gameOrderNumber").val()+'&start='+$("#start").val()+'&end='+$("#end").val();
        	window.open(url);
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
        
        $scope.showHuidiao2 = function(id){
            $("#backId").val(id);
            $('.boxopen').show();
        }
        
        $scope.huidiao2 = function(){
        	if($("#backMoney").val()==''){
        		ZENG.msgbox.show("请填写金额", 5, 2000);
        		return;
        		
        	}
        	if(confirm("确认回调吗？")){
        		ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
                $http.get('manualCallBackGame2.do?id=' + $("#backId").val()+"&money="+$("#backMoney").val(),config).success(function(data, status, headers, config){
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
        
        

        
        $scope.upperScore = function(target){
        	var campusId = target.getAttribute('data');
        	$("#upperId").val(campusId);
        	$('.popbox').show();
        	$(".boxopen").hide();
        }
        
        $scope.inputUpperScore = function(target){
        	subm()
        }
        
        function subm(){
        	var upperMoney = $("#upperMoney").val()
        	var upperId = $("#upperId").val()
        	if(upperId==""){ZENG.msgbox.show("系统错误", 5, 2000);return;}
        	if(upperMoney==""){ZENG.msgbox.show("请输入上分金额", 5, 2000);return;}
        	if(confirm("请确定此订单是否到账？请输入实际收到的金额(包括小数点)！！！")){
        		ZENG.msgbox.show('正在载入中，请稍后...', 6, 100000);
                $http.get('upperScore.do?id=' + upperId+"&upperMoney="+upperMoney,config).success(function(data, status, headers, config){
                	ZENG.msgbox._hide();
                	if(data.state){
                		$('.popbox').hide();
                		$("#upperMoney").val("")
                    	$("#upperId").val("")
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
		$scope.showApiError = function(target){
			var campusId = target.getAttribute('data');
			window.open("showApiError.do?id="+campusId);
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
            $http.get('payorderList.do?pageNo=' + pageNo +'&orderNumber='+$("#orderNumber").val()+'&userName='+$("#userName").val()+'&uid='+$("#uid").val()+'&customer_id='+$("#customer_id").val()+'&orderType='+$("#orderType").val()+'&flag='+$("#flag").val()+'&gameOrderNumber='+$("#gameOrderNumber").val()+'&start='+$("#start").val()+'&end='+$("#end").val()+'&minPrice='+$("#minPrice").val()+'&priceType='+$("#priceType").val()+'&maxPrice='+$("#maxPrice").val()+'&remarks='+$("#remarks").val(),config).success(function(data, status, headers, config){
                $scope.totalCount = data.totalCount;//更新总记录数
                $scope.payOrderCount = data.PayOrderCount;
                totalPage=data.totalPage//更新总页数
                $scope.users = data.obj;
                changePageNo();
                try{if(data.EXPORT.state)$("#export").show();} catch (e) {}
                ZENG.msgbox._hide();
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
    
    $(document).ready(function(){
	      $('.close').click(function() {
	    	  if(useragentHTML!='')
	    	  	$(".itxt").html(useragentHTML);
	    	  useragentHTML="";
	    	  $("#upperId").val("");
	    	  $("#upperMoney").val("");
	    	  
	          $('.popbox').hide();
	      })
	      
	      $('.qxbtn').click(function() {
	    	  if(useragentHTML!='')
	    	  	$(".itxt").html(useragentHTML);
	    	  useragentHTML="";
	          $('.popbox').hide();
	      })
	      
	      $('.opneclose').click(function() {
	    	  if(useragentHTML!='')
	    	  	$(".itxt").html(useragentHTML);
	    	  useragentHTML="";
	    	  $("#backMoney").val("");
	    	  $("#backId").val("");
	          $('.boxopen').hide();
	      })
	      	
	      $('.qxclose').click(function() {
	    	  if(useragentHTML!='')
	    	  	$(".itxt").html(useragentHTML);
	    	  useragentHTML="";
	          $('.boxopen').hide();
	      })
	      
	      
     })
     
     function qxbtnclose(){
    	$('.popbox').hide();
    	$("#upperId").val("");
  	   $("#upperMoney").val("");
  	 $("#backMoney").val("");
	  $("#backId").val("");
    }
     
     function orders(){
    	if($("#customer_id").val()!='0'){
    		$("#tname").html($("#customer_id").find("option:selected").text());
    	}else{
    		$("#tname").html("");
    	}
    }
    
    
</script>