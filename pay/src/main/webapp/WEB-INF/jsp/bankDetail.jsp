<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/bankDetail-script.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<style>
.itxt h3,.itxt p{margin: 20px 0;}
.popbox {position: fixed;top: 0;left: 0;width: 100%;height: 100%;background: rgba(0,0,0,.6);display: none ;}
.popbox .bgwx {width: 380px;height: auto;overflow: hidden;background-color: #fff;border-radius: 3px;position: absolute; top:50%;left: 50%;transform: translate(-50%,-50%);}
.popbox .bgwx .bgimg {position: relative;/*  width: 100%; height: 100%;*/padding: 40px 50px;text-align: center;overflow: hidden;}
.popbox .bgwx .bgimg span.close {position: absolute;right: 16px;top: 5px;font-size: 14px;display: block;cursor: pointer;height: 8px; width: 8px; color: #b0b8bf;}
.sfbtn{display: inline-block; border: none; width: 100px; text-align: center; line-height: 30px; background-color: #f00; color: #fff;}
.srinput{padding: 5px; width: 200px;}
.qdbtn,.qxbtn{display: inline-block; width: 100px;text-align: center; line-height: 30px; border: none; color: #fff; border-radius: 2px;
margin: 0 5px;}
.qdbtn{background-color: #0095d9;}
.qxbtn{background-color: #666;}
 .itxtnum{display: inline-block; margin-right:5px; font-size: 14px; font-weight: bold; color: #000;}
</style>
<div ng-controller='CampusController'>
  <div class="creatbox">
    <div class="middle">
        <p class="detile-title">
            <strong>编辑银行卡</strong>
        </p>
        <form id="infro" name="infro" action="threeFile.do"  
        method="post" enctype="multipart/form-data"> 
        <input type="hidden" id="id" value="${bank.id}" >
		<%-- 主键 --%>
		<!-- 创建时间 -->
        <div class="input-label">
            <label class="label-text ">银行卡号：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="cardNo" value="${bank.cardNo}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">银行开户人：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="bankAccount" value="${bank.bankAccount}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
       	<div class="input-label">
            <label class="label-text ">银行名称：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="bankName" value="${bank.bankName}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">银行编码：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="bankMark" value="${bank.bankMark}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">支付宝index：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="cardIndex" value="${bank.cardIndex}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">每日上限金额：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="maxLimit" value="${bank.maxLimit}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">每日使用时间范围：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="useTime" style="width: 50px" value="${bank.useStartTime}-${bank.useEndTime}" /> <span style="color: red">如晚上22点到凌晨2点填写：22-02，如凌晨0点到23点 0-23,开始时间分钟都是00分，结束时间59分</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">尾号：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="tail" style="width: 50px" value="${bank.tail}" /> <span style="color: red">如：1234,4321,6789</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">备注：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="remarks" value="${bank.remarks}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">所属平台：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="customerid" style="width: 100px">
			  			<option value="0">--请选择--</option>
			  			<c:forEach items="${list}" var="o"> 
			  					<option  value="${o.id}"  <c:if test="${o.id==bank.customerid}">selected="selected"</c:if> >${o.name}</option>
			  			</c:forEach>
			  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">模式：</label>
            <div class="label-box form-inline control-group">
	  			<label><input type="radio" value="0" name="pattern" <c:if test="${bank.pattern==0 || empty bank}">checked="checked"</c:if>  />老模式</label><label><input type="radio" name="pattern"  <c:if test="${bank.pattern==1}">checked="checked"</c:if> value="1" />飞行模式</label>
                <span class="help-inline"></span>
            </div>
        </div>
        <div class="input-label">
            <label class="label-text ">状态：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option  value="1" <c:if test="${bank.flag==1}">selected="selected"</c:if>>开启</option>
	  			<option  value="2"<c:if test="${bank.flag==2}">selected="selected"</c:if>>关闭</option>
	  			
	  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        </form>
	    </div>
  </div>
  <div class="bottom-bar">
    <div class="con">
      <a class="btn btn-large btn-primary fr btn-first1" ng-click="submit()" href="javascript:;"><i class="icon-white icon-ok"></i> 确定</a>
      <a class="btn btn-large" href="javascript:refreshList()"><i class="icon-chevron-up"></i> 返回列表</a>
    </div>
  </div>
  
  <div class="popbox">
	  <div class="bgwx">
	    <div class="bgimg">
	      <span class="close">✖</span>
	        <div class="itxt">
	        	<h3>谷歌验证码</h3>
	        	<input type="text" class="srinput" id="code" value="" placeholder="请输入谷歌验证码" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="upperId" value="" >
	        	<p>
	        		<button class="qdbtn" ng-click="inputUpperScore()">确定</button>
	        	    <button class="qxbtn">取消</button>
	            </p>
	        </div>
	    </div>
	  </div>
	</div>
  
</div>