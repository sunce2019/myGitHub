<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/customerDetail-script.jsp"%>
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
            <strong>编辑用户</strong>
        </p>
        <form id="infro" name="infro" action="threeFile.do"  
        method="post" enctype="multipart/form-data"> 
        
		<%-- 主键 --%>
		<input type="hidden" id="id" name="id" data-valid="true"   data-input="true"  value="${customer.id}"/>
		<!-- 创建时间 -->
        <div class="input-label">
            <label class="label-text ">商户名：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="name"  value="${customer.name}"/>
                <span class="help-inline"></span>
            </div>
        </div>
        <c:if test="${empty customer}">
        	<input type="hidden" id="flag" value="0" />
        </c:if>
        
        <div class="input-label">
            <label class="label-text ">商户ip：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="ip" value="${customer.ip}"/> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">商户回调方式：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="callbackType" style="width: 100px">
			  			<option  value="1" <c:if test="${customer.callbackType==1}">selected="selected"</c:if>>中博回调</option>
			  			<option  value="2" <c:if test="${customer.callbackType==2}">selected="selected"</c:if>>棋牌回调</option>
			  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <c:if test="${not empty customer}">
        	<div class="input-label">
            <label class="label-text ">商务状态：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="flag" style="width: 100px">
	  			<option  value="3" <c:if test="${customer.flag==3}">selected="selected"</c:if>>未激活</option>
	  			<option  value="1" <c:if test="${customer.flag==1}">selected="selected"</c:if>>正常</option>
	  			<option  value="2"<c:if test="${customer.flag==2}">selected="selected"</c:if>>禁用</option>
	  			
	  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        	
        	<div class="input-label">
	            <label class="label-text ">商户id：</label>
	            <div class="label-box form-inline control-group">
	           	    	${customer.uid}
	                <span class="help-inline"></span>
	            </div>
	        </div>	
	        
	        <div class="input-label">
	            <label class="label-text ">商户key token(MD5秘钥)：</label>
	            <div class="label-box form-inline control-group">
	           	    	${customer.key}
	                <span class="help-inline"></span>
	            </div>
	        </div>	
	        
	        <div class="input-label">
	            <label class="label-text ">商户 对称秘钥：</label>
	            <div class="label-box form-inline control-group">
	           	    	${customer.symmetric}
	                <span class="help-inline"></span>
	            </div>
	        </div>
	        
	        <div class="input-label">
	            <label class="label-text ">商户public_key：</label>
	            <div class="label-box form-inline control-group">
	           	    	<input type="text" value="${customer.public_key1}" />
	                <span class="help-inline"></span>
	            </div>
	        </div>
	        
	        <div class="input-label">
	            <label class="label-text ">商户private_key：</label>
	            <div class="label-box form-inline control-group">
	           	    	<input type="text" value="${customer.private_key_2}" />
	                <span class="help-inline"></span>
	            </div>
	        </div>
        </c:if>
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

