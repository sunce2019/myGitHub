<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp"%>
<%@ include file="../../jsp.js/activeDetail-script.jsp"%>
<%@ include file="../../jsp.js/imgUpload-script.jsp"%>
<%@ include file="../../jsp.js/ueditor-script.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 引入ueditor -->
<script type="text/javascript" charset="utf-8"
	src="../../ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8"
	src="../../ueditor/ueditor.all.js">
	
</script>
<script type="text/javascript" charset="utf-8"
	src="../../ueditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript" charset="utf-8"
	src="../../ueditor/third-party/zeroclipboard/ZeroClipboard.js"></script>

<script type="text/javascript">
	var ue = UE.getEditor('editor');
	ue.addListener("ready", function() {
		var content = decodeURIComponent('${active.content}');
		ue.setContent(content);
	});
</script>

<style>
.itxt h3, .itxt p {
	margin: 20px 0;
}

.popbox {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, .6);
	display: none;
}

.popbox .bgwx {
	width: 380px;
	height: auto;
	overflow: hidden;
	background-color: #fff;
	border-radius: 3px;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.popbox .bgwx .bgimg {
	position: relative; /*  width: 100%; height: 100%;*/
	padding: 40px 50px;
	text-align: center;
	overflow: hidden;
}

.popbox .bgwx .bgimg span.close {
	position: absolute;
	right: 16px;
	top: 5px;
	font-size: 14px;
	display: block;
	cursor: pointer;
	height: 8px;
	width: 8px;
	color: #b0b8bf;
}

.sfbtn {
	display: inline-block;
	border: none;
	width: 100px;
	text-align: center;
	line-height: 30px;
	background-color: #f00;
	color: #fff;
}

.srinput {
	padding: 5px;
	width: 200px;
}

.qdbtn, .qxbtn {
	display: inline-block;
	width: 100px;
	text-align: center;
	line-height: 30px;
	border: none;
	color: #fff;
	border-radius: 2px;
	margin: 0 5px;
}

.qdbtn {
	background-color: #0095d9;
}

.qxbtn {
	background-color: #666;
}

.itxtnum {
	display: inline-block;
	margin-right: 5px;
	font-size: 14px;
	font-weight: bold;
	color: #000;
}
</style>
<div ng-controller='CampusController'>
	<div class="creatbox">
		<div class="middle">
			<p class="detile-title">
				<strong>编辑活动页面</strong>
			</p>
			<form id="infro" name="infro" action="addActive.do" method="post"
				enctype="multipart/form-data">
				<input type="hidden" id="id" value="${active.id}">
				<div class="input-label">
					<label class="label-text ">活动标题：</label>
					<div class="label-box form-inline control-group">
						<input type="text" id="title" value="${active.title}" /> <span
							class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">背景图片：</label>
					<div class="label-box form-inline control-group">
						<input type="hidden" name="img" value="${active.img}" id="photoUrl" /> <input
							type="file" name="logoFile" id="logoFile"
							onchange="setImg(this);"> <span><img
							id="photourlShow" src="${active.img}" width="150" height="98" /></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">活动详情：</label>
					<div class="label-box form-inline control-group">
						<script id="editor" type="text/plain"
							style="width: 1000px; height: 300px;"></script>
							<button type="button" class="btn btn-danger"
									ng-click="testUeditor()">查看效果
								</button>
						</div>
				</div>
				<div class="input-label">
					<label class="label-text ">活动细则：</label>
					<div class="label-box form-inline control-group">
						<input type="text" id="details" value="${active.details}" /> <span
							class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">开始时间：</label>
					<div class="label-box form-inline control-group">
						<input type="text" id="startTime"
							onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,maxDate:'#F{$dp.$D(\'endTime\')}'})"
							value="${active.startTime}" /> <span class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">结束时间：</label>
					<div class="label-box form-inline control-group">
						<input type="text" id="endTime"
							onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,minDate:'#F{$dp.$D(\'startTime\')}'})"
							value="${active.endTime}" /> <span class="help-inline"></span>
					</div>
				</div>

				<div class="input-label">
					<label class="label-text ">排序显示：</label>
					<div class="label-box form-inline control-group">
						<input type="text" id="sort" value="${active.sort}" /> <span
							class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">活动状态：</label>
					<div ng-if="${active.status}" class="label-box form-inline control-group" >
						<input name="status"  type="radio"  value="1"  checked="checked"/>启用&nbsp;&nbsp;&nbsp;
                        <input name="status"  type="radio" value="0" />禁用 <span class="help-inline"></span>	
					</div>
					<div ng-if="!${active.status}" class="label-box form-inline control-group" >
						<input name="status"  type="radio"  value="1" />启用&nbsp;&nbsp;&nbsp;
                        <input name="status"  type="radio" value="0"  checked="checked"/>禁用 <span class="help-inline"></span>	
					</div>
				</div>
				<div class="input-label">
					<div class="label-box form-inline control-group">
						<a class="btn btn-primary" href="#" ng-click="addParam()"><i
							class="icon-white icon-plus"></i>添加参数</a></br> <span
							ng-repeat="plist in list1"> 参数名：<input type="text"
							ng-model="plist.a" /> <span ng-if="$index>=0">
								<button type="button" class="btn btn-danger" ng-click="deleteMsg($index)">
									<i class="icon-white icon-remove"></i>删除
								</button>
						</span> </br>
						</span> <span class="help-inline"></span>
					</div>
				</div>

			</form>
		</div>
	</div>

	<div class="bottom-bar">
		<div class="con">
			<a class="btn btn-large btn-primary fr btn-first1"
				ng-click="submit()" href="javascript:;"><i
				class="icon-white icon-ok"></i> 确定</a> <a class="btn btn-large"
				href="active.do"><i class="icon-chevron-up"></i> 返回列表</a>
		</div>
	</div>

	<div class="popbox" style="z-index: 5000">
		<div class="bgwx">
			<div class="bgimg">
				<span class="close">✖</span>
				<div class="itxt">
					<h3>谷歌验证码</h3>
					<input type="text" class="srinput" id="code" value=""
						placeholder="请输入谷歌验证码"
						onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value"
						onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value"
						onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
						this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
						this.o_value=this.value}">
					<input type="hidden" id="upperId" value="">
					<p>
						<button class="qdbtn" ng-click="inputUpperScore()">确定</button>
						<button class="qxbtn">取消</button>
					</p>
				</div>
			</div>
		</div>
	</div>

</div>


