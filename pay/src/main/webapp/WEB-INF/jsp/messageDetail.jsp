<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp"%>
<%@ include file="../../jsp.js/messageDetail-script.jsp"%>
<%@ include file="../../jsp.js/imgUpload-script.jsp"%>
<%@ include file="../../jsp.js/ueditor-script.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
				<strong>模板编辑</strong>
			</p>						
			<form id="infro" name="infro" action="updateMessage.do" method="post"
				enctype="multipart/form-data">			
				<input type="hidden" id="id" value="${message.id}">
				<!-- <div class="input-label">
					<label class="label-text ">隶属银行：</label>
					<div class="label-box form-inline control-group">					
					    <select id="bank" style="width: 100px">
					        <option  value="">请选择</option>
	            			<option ng-repeat='b in BankType' ng-selected="b.key==message.bank" value="{{b.key}}">{{b.value}}</option>
	            		</select>
						<span class="help-inline"></span>
						
					</div>
				</div> -->
				<div class="input-label">
				<label class="label-text ">短信样式：</label>
				    <div >				    
					   <textarea rows="5" cols="50" style="width:800px" id="content">${message.content}</textarea> 
					   <span class="help-inline"></span>
					</div>
				</div>
	            <div class="input-label">
				<label class="label-text ">模板样式：</label>
					<div class="label-box form-inline control-group">
					<a class="btn btn-primary" href="#" ng-click="addbackParam()" ><i class="icon-white icon-plus"></i>添加参数</a>
           	    	</br>
           	    	<span ng-repeat=" l in backParamList">
           	    		<input type="text" ng-model="l.key" value="{{l.key" />
           	    		追加类型：
	            		<select ng-model="l.value" style="width: 100px">
	            			<option ng-repeat='b in regxType' ng-selected="b.key==l.value" value="{{b.key}}">{{b.value}}</option>
	            		</select>	            		         	    		
	            			<button type="button" class="btn btn-danger" ng-click="delbackParam($index)"><i  class="icon-white icon-remove"></i>删除</button>	            		
           	    		</br>
           	    	</span>					
					<span class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
				<label class="label-text ">ip白名单：</label>
				    <div >
				    <input type="text" id="ip"  style="width:800px;height:50px" value="${message.ip}">
					   <%-- <textarea rows="2" cols="50" style="width:800px" id="ip">
					    ${message.ip}
					   </textarea> --%>
					   <%-- <input type="text" id="content" value="${message.content}" /> --%><span class="help-inline"></span>
					</div>
				</div>
			</form>
		</div>
	</div>           
	<div class="bottom-bar">
		<div class="con" >					
		     <a class="btn btn-large btn-primary fr btn-first1" ng-click="submit()" href="javascript:;">
		     <i class="icon-white icon-ok"></i> 确定</a>					 					
		<a class="btn btn-large" href="../sms/messageInfo.do" ><i class="icon-chevron-up"></i> 返回列表</a>
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


