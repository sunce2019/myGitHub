<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/userDetail-script.jsp"%>
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
		<input type="hidden" id="id" name="id" data-valid="true"  data-input="true"  value="${users.id}"/>
		<!-- 创建时间 -->
        <div class="input-label">
            <label class="label-text ">账号：</label>
            <div class="label-box form-inline control-group">
            	<c:if test="${empty users}">
            		<input type="text" id="loginname"   />		账号密码默认：123456
           		</c:if>
           		<c:if test="${not empty users}">
           			${users.loginname}
           		</c:if>
                <span class="help-inline"></span>
            </div>
        </div>
        <c:if test="${not empty users}">
	        <div class="input-label">
	            <label class="label-text ">修改密码：</label>
	            <div class="label-box form-inline control-group">
	            	<input type="text" id="password"   />
	                <span class="help-inline"></span>
	            </div>
	        </div>
        </c:if>
        <div class="input-label">
            <label class="label-text ">小名：</label>
            <div class="label-box form-inline control-group">
           		<input type="text" id="name" value="${users.name}"  />
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">角色：</label>
            <div class="label-box form-inline control-group">
           	    <select id="groups" multiple="multiple" style="width: 200px;height: 200px"  ng-model="groups"  >
		  			<option ng-repeat='b in groupsList' ng-selected="{{b.state}}" value="{{b.id}}">{{b.name}}</option>
		  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">状态：</label>
            <div class="label-box form-inline control-group">
           	    		<select id="flag" style="width: 100px">
			  			<option value="0">--请选择--</option>
			  			<option  value="1" <c:if test="${users.flag==1}">selected="selected"</c:if>>正常</option>
			  			<option  value="2"<c:if test="${users.flag==2}">selected="selected"</c:if>>禁用</option>
			  			
			  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <c:if test="${not empty users && users.type!=3}">
	        <div class="input-label">
	            <label class="label-text ">谷歌key：</label>
	            <div class="label-box form-inline control-group">
	            	UYSHQELNXLO4WAZL
	            	</br>
	            	<img alt="" src="../img/chart.jpg" />
	                <span class="help-inline"></span>
	            </div>
	        </div>
        </c:if>
        <c:if test="${not empty users && users.type==3}">
	        <div class="input-label">
	            <label class="label-text ">谷歌二维码：</label>
	            <div class="label-box form-inline control-group">
	            	${users.key}
	            	</br>
	            	<input type="text" value="${users.url}" />
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