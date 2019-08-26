<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/businessDetail-script.jsp"%>
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
            <strong>编辑商户</strong>
        </p>
        <form id="infro" name="infro" action="threeFile.do"  
        method="post" enctype="multipart/form-data"> 
        
		<%-- 主键 --%>
		<input type="hidden" id="id" name="id" data-valid="true"   data-input="true"  value="${business.id}"/>
		
		<c:if test="${empty business}">
			<div class="input-label">
	            <label class="label-text ">是否自动对接：</label>
	            <div class="label-box form-inline control-group">
	            <label style="width: 50px">	<input type="radio" ng-model="auto" ng-checked="true" ng-change="autoChange()" name="auto" value="no" />否</label>
	            <label style="width: 50px">	<input type="radio" ng-model="auto" ng-change="autoChange()" name="auto" value="yes" />是</label>
	                <span class="help-inline"></span>
	            </div>
	        </div>
		</c:if>
		
		
		
		<!-- 创建时间 -->
        <div class="input-label no">
            <label class="label-text ">商务名称：</label>
            <div class="label-box form-inline control-group">
            	<c:if test="${empty business}">
            		<select id="uid" style="width: 100px" ng-change="uidJS();" ng-model="type" >
			  			<option value="0">--请选择--</option>
			  			<option ng-repeat='b in business' value="{{b.key}}">{{b.values}}</option>
			  		</select>
           		</c:if>
           		<c:if test="${not empty business}">
           			${business.name}
           			<input type="hidden" id="uid" value="${business.code}" />
           		</c:if>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label yes" style="display: none">
            <label class="label-text ">商务名称：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="name" />
                <span class="help-inline"></span>
            </div>
       	</div>
        
        <span <c:if test="${isOK=='2'}"> style="display: none"</c:if> >
        <div class="input-label">
            <label class="label-text ">支付类型：</label>
            <div class="label-box form-inline control-group">
            	<c:if test="${empty business}">
            		<select id="orderType" style="width: 100px" ng-change="changeorderType();" ng-model="orderType" >
			  			<option ng-selected="orderType=='0'" value="0">--请选择--</option>
			  			<option ng-repeat='b in patTran' value="{{b}}">{{b}}</option>
			  		</select>			  		
           		</c:if>
           		
           		<c:if test="${not empty business}">
           			${business.payType}
           			<input type="hidden" id="orderType" ng-model="orderType" value="${business.payType}"  />
           		</c:if>
           		&nbsp;&nbsp;&nbsp;&nbsp;<span >支付编码：</span><input type="text" id="payCode" value="${business.payCode}"/>
                <span class="help-inline"></span>
            </div>
        </div>
        
        
        
       	<div class="input-label">
            <label class="label-text ">所属用户：</label>
            <div class="label-box form-inline control-group">
            	<select id="customerid" ng-model="customerid" ng-change="customerchange()"  style="width: 100px" >
		  			<option value="0">--请选择--</option>
		  			<c:forEach items="${list}" var="o"> 
		  					<option  value="${o.id}"  <c:if test="${o.id==business.customer_id}">selected="selected"</c:if> >${o.name} </option>
		  			</c:forEach>
		  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label bank" <c:if test="${business.payType!='支付宝转银行' && business.payType!='微信转银行' && business.payType!='微信买单' && business.payType!='云闪付' }">style="display: none"</c:if> >
            <label class="label-text ">绑定银行卡：</label>
            <div class="label-box form-inline control-group">
           		<select multiple="multiple" id="bankId" style="width: 350px;height: 300px"  >
	                <option ng-repeat='b in bankList' value="{{b.cardNo}}" ng-selected="b.editClick" >{{b.bankAccount}}:{{b.tail}}-{{b.bankName}}-{{b.remarks}}</option>
		  		</select>
		  		ctrl + 鼠标左键多选
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label bankPattern" <c:if test="${business.payType!='云闪付' && business.payType!='微信买单' && business.payType!='支付宝转银行' && business.payType!='微信转银行'}">style="display: none"</c:if> >
            <label class="label-text ">银行卡切换模式：</label>
            <div class="label-box form-inline control-group">
           	    <select id="bankPattern" style="width: 100px">
		  			<option value="0">--请选择--</option>
		  			<option  value="1" <c:if test="${business.bankPattern==1}">selected="selected"</c:if>>成功笔数</option>
		  			<option  value="2"<c:if test="${business.bankPattern==2}">selected="selected"</c:if>>金额平摊</option>
		  			<option  value="3"<c:if test="${business.bankPattern==3}">selected="selected"</c:if>>订单轮询</option>
		  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">商户号：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="uids" value="${business.uid}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">商户秘钥：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="token" value="${business.token}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">商户公钥：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="public_key" value="${business.public_key}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">商户私钥：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="private_key" value="${business.private_key}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        <div class="input-label">
            <label class="label-text ">api地址：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="apiUrl" value="${business.apiUrl}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">白名单Ip：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="callbackip" value="${business.callbackip}" /> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">每日最大额度：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id="maxLimit" value="${business.maxLimit}" onchange="if(/\D/.test(this.value)){alert('只能输入整数');this.value='';}"/> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">固定金额：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id=fixed_price value="${business.fixed_price}"/>  <span style="color:#f00">注：固定金额如 10-50-100-500 -符号分割固定金额</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">范围最小金额：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id=range_min value="${business.range_min}"  onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))

this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;

this.o_value=this.value}"/> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">范围最大金额：</label>
            <div class="label-box form-inline control-group">
           	    	<input type="text" id=range_max value="${business.range_max}" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))

this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;

this.o_value=this.value}"/> 
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">VIP：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="vip">
           	    		<option value="0" >--请选择--</option>
           	    		<option value="0" <c:if test="${business.vip==0}">selected="selected"</c:if>>0</option>
           	    		<option value="1" <c:if test="${business.vip==1}">selected="selected"</c:if>>1</option>
           	    		<option value="2" <c:if test="${business.vip==2}">selected="selected"</c:if>>2</option>
           	    		<option value="3" <c:if test="${business.vip==3}">selected="selected"</c:if>>3</option>
           	    		<option value="4" <c:if test="${business.vip==4}">selected="selected"</c:if>>4</option>
           	    		<option value="5" <c:if test="${business.vip==5}">selected="selected"</c:if>>5</option>
           	    		<option value="6" <c:if test="${business.vip==6}">selected="selected"</c:if>>6</option>
           	    		<option value="7" <c:if test="${business.vip==7}">selected="selected"</c:if>>7</option>
           	    		<option value="8" <c:if test="${business.vip==8}">selected="selected"</c:if>>8</option>
           	    		<option value="9" <c:if test="${business.vip==9}">selected="selected"</c:if>>9</option>
           	    		<option value="10" <c:if test="${business.vip==10}">selected="selected"</c:if>>10</option>
           	    	</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">跳转类型：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="jumpType">
           	    		<option value="1" >--请选择--</option>
           	    		<option value="1" <c:if test="${business.jumpType==1}">selected="selected"</c:if>>表单POST提交</option>
           	    		<option value="2" <c:if test="${business.jumpType==2}">selected="selected"</c:if>>window.location跳转</option>
           	    		<option value="3" <c:if test="${business.jumpType==3}">selected="selected"</c:if>>二维码</option>

           	    	</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        </span>
        <c:if test="${not empty business}">
   			<div class="input-label">
            <label class="label-text ">商务状态：</label>
            <div class="label-box form-inline control-group">
           	    	<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option  value="1" <c:if test="${business.flag==1}">selected="selected"</c:if>>开启</option>
	  			<option  value="2"<c:if test="${business.flag==2}">selected="selected"</c:if>>关闭</option>
	  			
	  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
   		</c:if>
   		
        <c:if test="${empty business}">
        	<input type="hidden" value="2" id="flag" />
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