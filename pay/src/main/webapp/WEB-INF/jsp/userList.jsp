<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/userList-script.jsp"%>
<div ng-controller='CampusController' class="list_right">
	<div class="well well-small">
      <div class="pull-left">
        <font class="titleText">用户列表</font>
      </div>
	  <div class="pull-right">
	  		账号:<input type="text" id="loginname" style="width: 100px"/>
	  		小名：<input type="text" id="name" style="width: 100px"/>
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	  		<a class="btn btn-primary" id="ADDUSER" style="display: none" href="javascript:openDetailIframe('../user/userAddPage.do');"><i class="icon-white icon-plus"></i>添加账号</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>账号</th>
	      <th>小名</th>
	      <th>角色</th>
	      <th>状态</th>
	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat='user in users'>
	      <td>{{user.loginname}}</td>
	      <td>{{user.name}}</td>
	      <td>
	      	<span ng-repeat='groups in user.groupsList'>
	      			{{groups.name}},
	      	</span>
	      </td>
	      <td>{{user.flag==1?'正常':user.type==2?'禁用':'未知'}}</td>
	      <td style="width: 150px">
	      		<span ng-if="user.editClick">
	      			<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="mining($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>编辑</button>
	     		</span>
	      </td>
	    </tr>
	  </tbody>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	    <tr class="toolbar">
	      <td colspan="100">
	        <b style="margin:0 10px">符合条件的记录</b>有<span class="list-count" id="itemContText">{{totalCount}}</span>条
	        <a id="listFirst" href="javascript:void(0)" ng-click="gotoFirst()">首页</a>&nbsp; <a id="listPrev" href="javascript:void(0)" ng-click="gotoPage(-1)">上一页</a>&nbsp; <a id="listNext" href="javascript:void(0)" ng-click="gotoPage(1)">下一页</a> &nbsp;<a id="listLast" ng-click="gotoLast()" href="javascript:void(0)">尾页</a>
	      </td>
	    </tr>
	  <tfoot>
	    
	  </tfoot>
	</table>
</div>
