/*公共表单提交（包括检验）*/
/*给确定按钮绑定事件*/
$(function(){
	$("#btn-submit").on("click",function(){
		var thisForm = $(this).closest("form");
		if(thisForm.valid()){
			thisForm.submit();
		}
	});
})

/**
 * 验证通过后，表单提交
 * @param options
 * @return
 */
	function ajaxSubmit_x(form,options){
		var defaultOptions = {
			addORedit:false,//true为编辑,false为新增
			validData:getValidInput(form),
			validUrl:null,
			submitData:getFormInput(form),
			submitUrl:null
		}
		defaultOptions = $.extend(defaultOptions,options);
		
		//var validData = getValidInput(defaultOptions.validForm);
		//var submitData = getFormInput(defaultOptions.validForm);
		
		
		if(defaultOptions.addORedit){
			//ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,true);		
			if(defaultOptions.validData){
				$.post(defaultOptions.validUrl,defaultOptions.validData,function(data){
					if(data.success){
						ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,true);
					}else{
				    	ZENG.msgbox.show(data.message, 5, 1000);
					}	
				},"json");
			}else{
				ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,true);
			}		
		}else{
			if(defaultOptions.validData){
				$.post(defaultOptions.validUrl,defaultOptions.validData,function(data){
					if(data.success){
						ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,false);
					}else{
				    	ZENG.msgbox.show(data.message, 5, 1000);
					}	
				},"json");
			}else{
				ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,false);
			}
		}
		

		/*
		if(defaultOptions.addORedit){
			ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,true);
		}else{
			if(defaultOptions.valid){
				$.post(defaultOptions.validUrl,defaultOptions.validData,function(data){
					if(data.success){
						ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,false);
					}else{
				    	ZENG.msgbox.show(data.message, 5, 1000);
					}	
				},"json");
			}else{
				ajaxSaveSubmit_x(defaultOptions.submitUrl,defaultOptions.submitData,false);
			}
		}*/
	}
	
/**
 * 提交表单，回调
 * @param url
 * @param submitData
 * @param addORedit
 * @return
 */
	function ajaxSaveSubmit_x(url,submitData,addORedit){
		$.post(url,submitData,function(dataJson){
			if(addORedit){//exists
	            if(dataJson.success){
                    $("html").delay(2000).queue(function(){
                        refreshList();
                        $(this).dequeue();
                    }); 
                    ZENG.msgbox.show(dataJson.message, 4, 1000);                   	            	
	            }else{
                    ZENG.msgbox.show(dataJson.message, 5, 1000);
	            }
	        }else{
	            if(dataJson.success){
	                $.dialog({
	                    id : 'ajaxedit',
	                    content : '添加成功，是否继续添加？',
	                    okVal: '是',
	                    ok : function(){
	                        location.reload();
	                        return false;
	                    },
	                    cancel : function(){
	                    	refreshList();
	                    }
	                });
	            }else{
                    ZENG.msgbox.show(dataJson.message, 5, 1000);
                }
	        };
		   },"json");
		}

	/**
	 * 列表返回刷新
	 * @return
	 */
    function refreshList(){
    	window.parent.closeDetailIframe(function(){
    		window.parent.$('#btnRefresh').trigger('click');
        });    	
    }
    
    /**
     * 获取form中要提交的数据
     * @param form
     * @return
     */
    function getFormInput(form){
		if(!form)return null;
		var aryInputElement = $("[data-input='true']",form);
		if(aryInputElement.size()==0)return null;
		var dateInput={};
		aryInputElement.each(function(i,e){
			dateInput[$(e).attr("name")] = $(e).val();
		})
		return dateInput;
	}
  
    function getValidInput(form){
		if(!form)return null;
		var aryInputElement = $("[data-valid='true']",form);
		if(aryInputElement.size()==0)return null;
		var dateInput={};
		aryInputElement.each(function(i,e){
			dateInput[$(e).attr("name")] = $(e).val();
		})
		return dateInput;
	}
    
    /**
     * 条件查询参数收集
     * @param form
     * @return
     */
    function getQueryFormInput(form){
		if(!form)return null;
		var aryInputElement = $("[data-input='true']",form);
		if(aryInputElement.size()==0)return null;
		var dataInput="&";
		aryInputElement.each(function(i,e){
			if($(e).val()!=null && $(e).val()!=""){
				dataInput=dataInput+$(e).attr("name")+"="+encodeURIComponent($(e).val())+"&";
			}
		});
		return dataInput.substring(0,dataInput.length-1);
	}
