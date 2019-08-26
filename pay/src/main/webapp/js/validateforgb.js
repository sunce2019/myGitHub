$.extend($.validator.messages, {
	required : "请输入",
	remote : "请修正该字段",
	email : "请输入正确格式的电子邮件",
	url : "请输入合法的网址",
	date : "请输入合法的日期",
	dateISO : "请输入合法的日期 (ISO).",
	number : "请输入合法的数字",
	digits : "只能输入整数",
	creditcard : "请输入合法的信用卡号",
	equalTo : "请再次输入相同的值",
	accept : "请输入拥有合法后缀名的字符串",
	maxlength : $.validator.format("请输入长度最多{0}的字符串"),
	minlength : $.validator.format("请输入长度最少{0}的字符串"),
	rangelength : $.validator.format("请输入长度{0}~{1}的字符串"),
	range : $.validator.format("请输入介于{0}~{1}之间的值"),
	max : $.validator.format("请输入最大为{0}的值"),
	min : $.validator.format("请输入最小为{0}的值")
});

jQuery.validator.addMethod("integerNumAndLetterPass", function(value, element) {
	var reg = /^[0-9a-zA-Z\[@]+$/;
	return this.optional(element) || reg.test(value);
}, "请输入数字或字母");

jQuery.validator.addMethod("integerNumAndLetter", function(value, element) {
	var reg = /^[0-9a-zA-Z]+$/;
	return this.optional(element) || reg.test(value);
}, "请输入数字或字母");


/**
 * 下拉框校验
 * 
 * @param options
 *            默认选中值（参数对象） {defaultVal:-1 缺省默认值【校验是否非空】 flag:true 是否必填【检验是否必填】 }
 */
jQuery.validator.addMethod("selectRequired", function(value, element, options) {
	var defaultValue = -1;
	if (options != undefined) {
		defaultValue = options;
	}
	return value != defaultValue;
}, "请输入");

/**
 * 纯英文
 */
jQuery.validator.addMethod("english", function(value, element, flag) {
	var reg = /^[a-zA-Z]*$/;
	return this.optional(element) || reg.test(value);
}, "请输入英文字母");

/**
 * 只能是字母、数字、减号、下划线组成
 */
jQuery.validator.addMethod("code", function(value, element, flag) {
	var reg = /^[0-9a-zA-Z\-_]+$/;
	return this.optional(element) || reg.test(value);
}, "只能是字母、数字、减号、下划线组成");


jQuery.validator.addMethod("idChecked", function(value, element) {
	return this.optional(element) || isIdCardNo(value);
}, "身份证号不合法");

jQuery.validator.addMethod("rangeDate", function(value, element, flag) {
	if(flag.begin){//end
		var ev=$(element).val();
		var bv=$("#"+flag.begin).val();
		if(ev=="" && bv!=""){
			return false;
		}
		if(ev=="" && bv==""){
			return true;
		}
		if(ev!="" && bv!=""){
			return true;
		}
		if(ev!="" && bv==""){//self != ""  return true
			return true;
		}
	}else{//begin
		var bv=$(element).val();
		var ev=$("#"+flag.end).val();
		if(bv=="" && ev!=""){
			return false;
		}
		if(bv=="" && ev==""){
			return true;
		}
		if(bv!="" && ev!=""){
			return true;
		}
		if(bv!="" && ev==""){//self != ""  return true
			return true;
		}
	}
}, "请选择时间");




function isIdCardNo(num) {
	num = num.toUpperCase();
	// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X。
	if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
		return false;
	}
	// 校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
	// 下面分别分析出生日期和校验位
	var len, re;
	len = num.length;
	if (len == 15) {
		re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
		var arrSplit = num.match(re);

		// 检查生日日期是否正确
		var dtmBirth = new Date('19' + arrSplit[2] + '/' + arrSplit[3] + '/'
				+ arrSplit[4]);
		var bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2]))
				&& ((dtmBirth.getMonth() + 1) == Number(arrSplit[3]))
				&& (dtmBirth.getDate() == Number(arrSplit[4]));
		if (!bGoodDay) {
			return false;
		} else {
			// 将15位身份证转成18位
			// 校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
			var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
					8, 4, 2);
			var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4',
					'3', '2');
			var nTemp = 0, i;
			num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
			for (i = 0; i < 17; i++) {
				nTemp += num.substr(i, 1) * arrInt[i];
			}
			num += arrCh[nTemp % 11];
			return true;
		}
	}
	if (len == 18) {
		re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
		var arrSplit = num.match(re);

		// 检查生日日期是否正确
		var dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/"
				+ arrSplit[4]);
		var bGoodDay;
		bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2]))
				&& ((dtmBirth.getMonth() + 1) == Number(arrSplit[3]))
				&& (dtmBirth.getDate() == Number(arrSplit[4]));
		if (!bGoodDay) {
			return false;
		} else {
			// 检验18位身份证的校验码是否正确。
			// 校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
			var valnum;
			var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
					8, 4, 2);
			var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4',
					'3', '2');
			var nTemp = 0, i;
			for (i = 0; i < 17; i++) {
				nTemp += num.substr(i, 1) * arrInt[i];
			}
			valnum = arrCh[nTemp % 11];
			if (valnum != num.substr(17, 1)) {
				return false;
			}
			return true;
		}
	}
	return false;
}
