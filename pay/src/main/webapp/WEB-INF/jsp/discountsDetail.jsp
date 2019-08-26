<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp"%>
<%@ include file="../../jsp.js/discountsDetail-script.jsp"%>
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
				<strong>优惠审核</strong>
			</p>						
			<form id="infro" name="infro" action="updateDiscounts.do" method="post"
				enctype="multipart/form-data">			
				<input type="hidden" id="id" value="${discounts.id}">
				<div class="input-label">
					<label class="label-text ">申请主题：</label>
					<div class="label-box form-inline control-group">
					${discounts.theme}
						<input type="hidden" id="theme" value="${discounts.theme}" /> <span
							class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
				<label class="label-text ">会员账号：</label>
					<div class="label-box form-inline control-group">
					${discounts.vipAccount}
						<input type="hidden" id="vipAccount" value="${discounts.vipAccount}" /> <span
							class="help-inline"></span>
					</div>
				</div>
				<div class="input-label">
					<label class="label-text ">审核状态：</label>							
						<div class="label-box form-inline control-group">	
						    <c:if test="${ discounts.status==0}">
							     <span style="color: blue">待审核</span> 
							</c:if>
							<c:if test="${ discounts.status==1}">
							      已通过
							</c:if>
							<c:if test="${ discounts.status==2}">
							      <span style="color: red">未通过</span> 
							</c:if>
						    <input type="hidden" id="status" value="${discounts.status}" /> 														
							<span class="help-inline"></span>
						</div>
				</div>
				<div class="input-label">
					<label class="label-text ">申请时间：</label>
					<div class="label-box form-inline control-group">
					${discounts.disTime}
						<input type="hidden" id="disTime" value="${discounts.disTime}" /> <span
							class="help-inline"></span>
					</div>
				</div>
                <div class="input-label" ng-repeat="plist in listSpeild">
                   <label class="label-text ">{{plist.key}}：</label>
					<div class="label-box form-inline control-group">
						{{plist.value}}
						<span class="help-inline"></span>
					</div>
				</div>
                 <div class="input-label">
					<label class="label-text ">审核状态：</label>
					<div class="label-box form-inline control-group">					
					<c:choose> 					
					   <c:when test='${ discounts.status==2}'> 
					         <input name="status"  type="radio" value="1" />通过
                             <input name="status"  type="radio" value="2" checked="checked"/>驳回 <span class="help-inline"></span>
					   </c:when> 					   
					   <c:otherwise> 
					         <input name="status"  type="radio" checked="checked" value="1" />通过&nbsp;&nbsp;&nbsp;
                             <input name="status"  type="radio" value="2" />驳回 <span class="help-inline"></span>					  
					   </c:otherwise> 					  
					</c:choose> 	
					</div>
				</div>
			</form>
		</div>
	</div>           
	<div class="bottom-bar">
		<div class="con" >			
		<c:if test="${ discounts.status==0}">
		     <a  class="btn btn-large btn-primary fr btn-first1"
				ng-click="submit()" href="javascript:;"><i
				class="icon-white icon-ok"></i> 确定</a>	
		</c:if>				 					
		<a class="btn btn-large" href="javascript:refreshList();" ><i class="icon-chevron-up"></i> 返回列表</a>
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


