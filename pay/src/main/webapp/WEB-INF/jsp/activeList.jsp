<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/activeList-script.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.text.*"%>
<script src="../../js/clipboard.min.js"></script> 

<script type="text/javascript">
	function changeCode(){	
	   document.getElementById("img").src="http://localhost:8080/getVerifyCode.do"+Math.random();	
	}
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
      <!-- <div class="pull-left">
        <font class="titleText">活动列表</font>
      </div> -->
	  <div class="pull-right" style="float: none;margin-left: 20px;">
	 		活动标题：<input type="text" id="title" style="width: 150px;" />
	 		开始时间：<input type="text" id="startTime" style="width: 150px" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,maxDate:'#F{$dp.$D(\'endTime\')}'})" value="" />
	  		结束时间：<input type="text" id="endTime" style="width: 150px"  onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,minDate:'#F{$dp.$D(\'startTime\')}'})" value="" />
	  		<!--  
	  		<img id="img"  onclick="changeCode();" style="-webkit-user-select: none;"  width="70px" height="30px" src="getVerifyCode.do">
	  		 -->
	  		 活动状态：<select id="flag" style="width: 100px">
	  			<option value="-1" selected="selected">--请选择--</option>
	  			<option  value="1" >开启</option>
	  			<option  value="0">关闭</option>
	  		</select>
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>	  		
	  		<a class="btn btn-primary" id="ADDactive" href="activeAddPage.do"><i class="icon-white icon-plus"></i>添加活动项</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>标题</th>
	      <!-- <th>详情</th> -->
	      <th style="overflow: hidden; white-space: nowrap;text-overflow: ellipsis;word-wrap: break-word;width:50">细则</th>
	      <th>开始时间</th>
	      <th>结束时间</th>
	      <th>状态</th>

	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat="user in users | orderBy : ['remarks', '-price']">
	      <td>{{user.title}}</td>
	      <!-- <td>{{user.content}}</td> -->
	      <td style="width: 500px;">
		      <span style="height:25px;width:450px;display:inline-block;line-height:25px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
		          {{user.details}}
		      </span>	          
	      </td>
	      <td>{{user.startTime}}</td>
	      <td>{{user.endTime}}</td>
	      <td>
	      <span ng-if="user.status" style="background-color: #4d9c4d;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;" >
	      	启用
	      </span>
	      <span ng-if="!user.status" style="background-color: red;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;">
	      	禁用
	      </span>
	      </td>  
	      <td>
	      	<span ng-if="user.editClick">
	      		<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>编辑</button>
	      	</span>&nbsp;&nbsp;
	      	<span ng-if="user.delClick">
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
	  <tfoot>
	  </tfoot>
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
