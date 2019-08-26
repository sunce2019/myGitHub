<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/discountsList-script.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.text.*"%>
<script src="../../js/clipboard.min.js"></script> 
<script type="text/javascript">

</script>
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
<div ng-controller='CampusController' class="list_right">
	<div class="well well-small">
	  <div class="pull-right" style="float: none;margin-left: 20px;">
	 		申请主题：<input type="text" id="theme" style="width: 150px;" />	
	 		会员账号：<input type="text" id="vipAccount" style="width: 150px;" />	 	 					  
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>	  		
	  		<button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>会员账号</th>
	      <th>主题</th>
	      <th>申请时间</th>
	      <th>审核状态</th>
	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat="user in users | orderBy : ['remarks', '-price']">
	      <td>{{user.vipAccount}}</td>
	      <td>{{user.theme}}</td>
	      <td>{{user.disTime}}</td>
	      <td ng-if="user.status==0">
		          <span  style="background-color: blue;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;" >
		      	     待审核
		         </span>
	      </td>	
	      <td ng-if="user.status==1">
		         <span  style="background-color: #4d9c4d;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;" >
		      	      已通过
		         </span>
	      </td>	
	      <td ng-if="user.status==2">
		         <span style="background-color: red;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;">
		      	     未通过
		        </span>                	
	      </td>	      
	      <td>
	      	<span ng-if="!user.editClick">
	      		<button ng-if="user.status==0" type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>	      		
	      		审核	      		
	      		</button>
	      		<button ng-if="user.status==1" type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>	      		
	      		查看	      		
	      		</button>
	      		<button ng-if="user.status==2" type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>	      		
	      		查看	      		
	      		</button>
	      	</span>&nbsp;&nbsp;
	      	<span ng-if="!user.delClick">
	      		<button type="button" class="btn btn-danger" data="{{user.id}}" ng-click="huidiao($event.target)"><i class="icon-white icon-remove" data="{{user.id}}" ></i>删除</button>
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
	</table>
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
