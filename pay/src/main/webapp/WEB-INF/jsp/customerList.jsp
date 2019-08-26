<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/customerList-script.jsp"%>
<div ng-controller='CampusController' class="list_right">
	<div class="well well-small">
      <div class="pull-left">
        <font class="titleText">用户列表</font>
      </div>
	  <div class="pull-right">
	  		商户名:<input type="text" id="name" style="width: 100px"/>
	  		
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	  		<a class="btn btn-primary" id="ADDCUST" style="display: none" href="javascript:openDetailIframe('customerSavePage.do');"><i class="icon-white icon-plus"></i>添加用户</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>商户id</th>
	      <th>商户名</th>
	      <th>商户状态</th>
	      <th>商户ip</th>
	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat='user in users'>
	      <td>{{user.uid}}</td>
	      <td>{{user.name}}</td>
	      <td>{{user.flag==1?'正常':user.flag==2?'禁用':'未激活'}}</td>
	      <td>{{user.ip}}</td>
	      <td>
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
