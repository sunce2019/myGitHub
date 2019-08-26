<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/updateDetail-script.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div ng-controller='CampusController'>
  <div class="creatbox">
    <div class="middle">
        <p class="detile-title">
            <strong>编辑用户</strong>
        </p>
        <form id="infro" name="infro" action="threeFile.do"  
        method="post" enctype="multipart/form-data"> 
        
		<%-- 主键 --%>
		<!-- 创建时间 -->
        <div class="input-label">
            <label class="label-text ">账号：</label>
            <div class="label-box form-inline control-group">
           		{{users.loginname}}
                <span class="help-inline"></span>
            </div>
        </div>
        <div class="input-label">
            <label class="label-text ">小名：</label>
            <div class="label-box form-inline control-group">
           		{{users.name}}
                <span class="help-inline"></span>
            </div>
        </div>
        <div class="input-label">
            <label class="label-text ">原密码：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="password1"  value=""  />
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">新密码：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="password2"  value=""  />
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">确认密码：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" id="password3"  value=""  />
                <span class="help-inline"></span>
            </div>
        </div>
        
        
        </form>
	    </div>
  </div>
  <div class="bottom-bar">
    <div class="con">
      <a class="btn btn-large btn-primary fr" ng-click="submit()" href="javascript:;"><i class="icon-white icon-ok"></i> 确定</a>
    </div>
  </div>
</div>