<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/businessPayOrderList-script.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%@ page language="java" import="java.text.*"%>
<%
	Date date = new Date();
	Calendar star = Calendar.getInstance();
	star.setTime(date);
	star.set(Calendar.HOUR_OF_DAY, 0);
	star.set(Calendar.MINUTE, 0);
	star.set(Calendar.SECOND, 0);
	star.set(Calendar.MILLISECOND, 0);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String st = sdf.format(star.getTime());
	star.set(Calendar.HOUR_OF_DAY, 23);
	star.set(Calendar.MINUTE, 59);
	star.set(Calendar.SECOND, 59);
	star.set(Calendar.MILLISECOND, 999);
	String en = sdf.format(star.getTime());
%>
<div ng-controller='CampusController'>
	<div class="well well-small">
      <div class="pull-left">
        <font class="titleText">订单列表</font>
      </div>
	  <div class="pull-right">
	  		游戏订单号:<input type="text" id="gameOrderNumber" style="width: 100px"/>
	  		订单人：<input type="text" id="userName" style="width: 100px" />
	  		支付类型：<select id="orderType" style="width: 100px">
	  			<option value="">--请选择--</option>
	  			<option ng-repeat='b in patTran' value="{{b}}">{{b}}</option>
	  		</select>
	  		支付状态：<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option value="2">已支付</option>
	  			<option value="1">未支付</option>
	  		</select>
	  		开始时间：<input type="text" id="start" style="width: 130px" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,maxDate:'#F{$dp.$D(\'end\')}'})" value="<%=st%>" />
	  		结束时间：<input type="text" id="end" style="width: 130px"  onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,minDate:'#F{$dp.$D(\'start\')}'})" value="<%=en%>" />
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>订单号</th>
	      <th>订单人</th>
	      <th>支付类型</th>
	      <th>支付金额</th>
	      <th>实际金额</th>
	      <th>支付状态</th>
	      <th>订单创建时间</th>
	      <th>回调状态</th>
	      <th>回调成功时间</th>
	      <th>操作</th>
	    </tr>
	   
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat='user in users'>
	      <td>{{user.gameOrderNumber}}</td>
	      <td>{{user.userName}}</td>
	      <td>{{user.payName}}</td>
	      <td>{{user.price}}</td>
	      <td>{{user.realprice}}</td>
	      <td>
	      	<span ng-if="user.flag==1" style="background-color: red;color: #fff;padding: 5px 5px" >
	      		未支付
	        </span>
	        <span ng-if="user.flag==2" style="background-color:  #0ac50a ;color: #fff;padding: 5px 5px" >
	      		已支付
	        </span>
	        <span ng-if="user.flag==3"  >
	      		未知
	        </span>
	      </td>
	      <td>{{user.addTime}}</td>
	      <td>
	      	<span ng-if="user.callGameFlag==2 && user.callGameNumber>=11">
	      		回调失败
	      	</span>
	      	<span ng-if="user.callGameNumber<11">
	      		{{user.callGameFlag==1?'创建订单':user.callGameFlag==2?'回调中':user.callGameFlag==3?'回调成功':'未知'}}
	      	</span>
	      </td>
	      <td>{{user.callGameSUCTime}}</td>
	      <td>
	      	<span ng-if="user.callGameFlag==2 && user.callGameNumber>=11">
	      		<button type="button" class="btn btn-info" data="{{user.orderNumber}}"  ng-click="huidiao($event.target)"><i class="icon-white icon-edit" data="{{user.orderNumber}}"></i>回调游戏</button>
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
