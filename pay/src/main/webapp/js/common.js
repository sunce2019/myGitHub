function isNumber(oNum) {
    if (!oNum) return false;
    var strP = /^\d+(\.\d+)?$/;
    if (!strP.test(oNum)) return false;
    try {
        if (parseFloat(oNum) != oNum) return false;
    }
    catch (ex) {
        return false;
    }
    return true;
}

function isFloatNumber(oNum) {
	if (!oNum) return false;
	var strP = /^(-?\d+)(\.\d+)?$/;
	if (!strP.test(oNum)) return false;
	try {
		if (parseFloat(oNum) != oNum) return false;
	}
	catch (ex) {
		return false;
	}
	return true;
}

$.fn.numberLimit = function() {  
    $(this).css("ime-mode", "disabled");  
    this.bind("keypress",function(e) {  
    	var code = (e.keyCode ? e.keyCode : e.which);  //兼容火狐 IE   
        if(!$.browser.msie&&(e.keyCode==0x8))  //火狐下不能使用退格键  
        {  
             return ;  
        }  
        return code >= 48 && code<= 57;   
    });  
    this.bind("blur", function() {  
        if (this.value.lastIndexOf(".") == (this.value.length - 1)) {  
            this.value = this.value.substr(0, this.value.length - 1);  
        } else if (isNaN(this.value)) {  
            this.value = "";  
        }  
    });  
    this.bind("paste", function() {  
        var s = clipboardData.getData('text');  
        if (!/\D/.test(s));  
        value = s.replace(/^0*/, '');  
        return false;  
    });  
    this.bind("dragenter", function() {  
        return false;  
    });  
    this.bind("keyup", function() {  
    if (/(^0+)/.test(this.value)) {  
        this.value = this.value.replace(/^0*/, '');  
        }  
    });  
};

$.fn.digitLimit = function() {  
    $(this).css("ime-mode", "disabled");  
    this.bind("keypress",function(e) {  
    	if (event.keyCode == 46) {
			if (this.value.indexOf(".") != -1) {
				return false;
			}
		} else {
			return event.keyCode >= 46 && event.keyCode <= 57;
		}   
    });  
    this.bind("blur", function() {  
        if (this.value.lastIndexOf(".") == (this.value.length - 1)) {  
            this.value = this.value.substr(0, this.value.length - 1);  
        } else if (isNaN(this.value)) {  
            this.value = "";  
        }  
    });  
    this.bind("paste", function() {  
        var s = clipboardData.getData('text');  
        if (!/\D/.test(s));  
        value = s.replace(/^0*/, '0');  
        return false;  
    });  
    this.bind("dragenter", function() {  
        return false;  
    });  
    this.bind("keyup", function() {  
    if (/(^0+)/.test(this.value)) {  
        this.value = this.value.replace(/^0*/, '0');  
        }  
    });  
};

function getSelectPos(obj) {
    var esrc = document.getElementById(obj);
    if (esrc == null) {
        esrc = event.srcElement;
    }
    var rtextRange = esrc.createTextRange();
    rtextRange.moveStart('character', esrc.value.length);
    rtextRange.collapse(true);
    rtextRange.select();
}

function formatJsonDate(value) {
    if (value) {
        return eval("new " + value.substr(1, value.length - 2));
    }
}

function formatJsDate(value) {
    if (value) {
        return new Date(value).format('yyyy年MM月dd日');
    }
}
/**
 * 格式化json对象里的日期格式
 * @param dataFormate 
 * @param time
 * @return
 */
function formartDate(dataFormate, time) {
    var date = new Date();
    date.setTime(time);
    return date.pattern(dataFormate);
}

function getCheckedValue(labelName){
	var retValues = "";
    $("[name='" + labelName + "']").each(function() {
    	if($(this).attr('checked')){
        	retValues += $(this).val() + ",";
    	}
    })
    if (retValues.length > 0) {
    	retValues = retValues.substring(0, retValues.length - 1);
    }

    return retValues;
}

function setAllCheckStatus(labelName, selected){
	if (selected == true || selected == 'checked') {
        $("[name='" + labelName + "']").each(function() {
            $(this).attr('checked', true);
        })
    } else {
    	$("[name='" + labelName + "']").each(function() {
            $(this).attr('checked', false);
        })
    }
}

function getCheckedValueExtend(labelName){
	var retValues = "";
    $("[extend='" + labelName + "']").each(function() {
    	if($(this).attr('checked')){
        	retValues += $(this).val() + ",";
    	}
    })
    if (retValues.length > 0) {
    	retValues = retValues.substring(0, retValues.length - 1);
    }

    return retValues;
}

function setAllCheckStatusExtend(labelName, selected){
	if (selected || selected == 'checked') {
		$("[extend='" + labelName + "']").each(function() {
			$(this).attr('checked', true);
		})
	} else {
		$("[extend='" + labelName + "']").each(function() {
			$(this).attr('checked', '');
		})
	}
}

function formatDayOfWeek(dayOfWeek){
	switch(parseInt(dayOfWeek)){
	case 1:
		return "星期一";
	case 2:
		return "星期二";
	case 3:
		return "星期三";
	case 4:
		return "星期四";
	case 5:
		return "星期五";
	case 6:
		return "星期六";
	case 7:
		return "星期天";
	}
	
	return "";
}

function S4() {
   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
   return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

Date.prototype.pattern = function(fmt) {
    var o = {
        "M+" : this.getMonth() + 1, //月份     
        "d+" : this.getDate(), //日     
        "h+" : this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时     
        "H+" : this.getHours(), //小时     
        "m+" : this.getMinutes(), //分     
        "s+" : this.getSeconds(), //秒     
        "q+" : Math.floor((this.getMonth() + 3) / 3), //季度     
        "S" : this.getMilliseconds()
    //毫秒     
    };
    var week = {
        "0" : "日",
        "1" : "一",
        "2" : "二",
        "3" : "三",
        "4" : "四",
        "5" : "五",
        "6" : "六"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
                .substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1,
                ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "星期" : "周")
                        : "")
                        + week[this.getDay() + ""]);
    }
    if (/(e+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1,
                ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "星期" : "周")
                        : "")
                        + this.getDay());
    }
    for ( var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
                    : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

function isNull(exp){
	if(exp==undefined || exp==null || exp=="")
		return true
	return false;
}