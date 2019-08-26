$.fn.extend({
	navListChange: function(className) {
		var aLi = this.find("li");
		var str = "(^|\\s+)" + className + "(\\s+|$)"
		var reg = new RegExp(str);
		var currentPageLi = null;
		aLi.each(function(index, element) {
			if (reg.test($(this).attr("class"))) {
				currentPageLi = this;
			}
			$(element).hover(function() {
				if (currentPageLi != this) {
					$(this).addClass(className);
				}
			}, function() {
				if (currentPageLi != this) {
					$(this).removeClass(className);
				}
			}).bind("click", function() {
				if (currentPageLi != this) {
					$(this).addClass(className);
					$(currentPageLi).removeClass(className);
					currentPageLi = this;
				}
			});
		})
	},
	pageCountList: function(num, func, starPage) {
		var currentPageNum = starPage || 1;
		var $ul = this;
		/*初始化分页列*/
		$.fn.initpageList(num, currentPageNum, $ul, func);
		/**/
		//$.fn.getClickEvent(num,$ul,currentPageNum,func);
	},
	getClickEvent: function(num, $ul, currentPageNumt, func) {
		var aLi = $ul.children("li");
		var currentPageNum = null;
		var strReg = /(^|\s+)dot(\s+|$)/;
		aLi.each(function(index, element) {
			if (index != 0 && index != aLi.size() - 1 && !strReg.test($(element).attr("class"))) {
				$(element).bind("click", function() {
					currentPageNum = $(this).attr("index");
					$.fn.initpageList(num, parseInt(currentPageNum), $ul, func);
					func(currentPageNum);
				})
			} else if (index == 0) {
				$(element).bind("click", function() {
					currentPageNum = currentPageNumt - 1 <= 0 ? 1 : currentPageNumt - 1;
					$.fn.initpageList(num, currentPageNum, $ul, func);
					func(currentPageNum);
				})
			} else if (index == aLi.size() - 1) {
				$(element).bind("click", function() {
					currentPageNum = currentPageNumt + 1 >= num ? num : currentPageNumt + 1;
					$.fn.initpageList(num, currentPageNum, $ul, func);
					func(currentPageNum);
				})
			}
		});
	},
	initpageList: function(num, currentPageNum, $ul, func) {

		var strNext = "<li index='0' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA next_page '>上一页</a></li>"
		var strPre = "<li index='-1' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA pev_page pageA'>下一页</a></li>"
		var strNum = "";
		strNum += strNext;
		if (num <= 10) {
			for (var i = 1; i <= num; i++) {
				if (i == currentPageNum) {
					strNum += "<li  index='" + i + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA currentPage num_page'>" + i + "</a></li>"
				} else {
					strNum += "<li  index='" + i + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + i + "</a></li>"
				}
			}
		} else if (num > 10) {
			if (currentPageNum <= 7) {
				for (var i = 1; i <= num; i++) {
					if (i <= 8) {
						if (i == currentPageNum) {
							strNum += "<li index='" + i + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA currentPage num_page'>" + i + "</a></li>"
						} else {
							strNum += "<li index='" + i + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + i + "</a></li>"
						}
					} else if (i > 8) {
						strNum += "<li class='dot'>...</li><li index='" + num + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + num + "</a></li>"
						break;
					}
				}
			} else if (currentPageNum > 7) {
				strNum += "<li index='1' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>1</a></li><li class='dot'>...</li>";
				if ((num - currentPageNum) <= 4) {
					var tmp = currentPageNum - (10 - (num - currentPageNum + 3));
					for (var j = tmp; j <= num; j++) {
						if (j == currentPageNum) {
							strNum += "<li index='" + j + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA currentPage num_page'>" + j + "</a></li>"
						} else {
							strNum += "<li index='" + j + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + j + "</a></li>"
						}
					}
				} else {
					for (var j = currentPageNum - 3; j <= currentPageNum + 2; j++) {
						if (j == currentPageNum) {
							strNum += "<li index='" + j + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA currentPage num_page'>" + j + "</a></li>"
						} else {
							strNum += "<li index='" + j + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + j + "</a></li>"
						}
					}
					strNum += "<li class='dot'>...</li><li index='" + num + "' class='pageLi'><a href='javascript:void(0)' class='pageBtn pageA num_page'>" + num + "</a></li>"
				}
			}
		}
		strNum += strPre;
		$ul.html(strNum);
		var aLi = $ul.children("li");
		$.fn.getClickEvent(num, $ul, currentPageNum, func);
	}
});

$.extend({
	/*叠加*/
	countSpaceSize: function(elm) {
		var width = parseInt(elm.css("marginLeft")) + elm.outerWidth() + parseInt(elm.css("marginRight"));
		var height = parseInt(elm.css("marginTop")) + elm.outerHeight() + parseInt(elm.css("marginBottom"));
		return {
			width: width,
			height: height
		}
	},
	menuChangeIframe:function(aryMenuList,JsonStr,iframeDiv){
		aryMenuList.each(function(index,element){
			$(element).bind("click",function(){
				iframeDiv.attr("src",JsonStr[$(this).attr("index")]);
			})
		});
	}
})