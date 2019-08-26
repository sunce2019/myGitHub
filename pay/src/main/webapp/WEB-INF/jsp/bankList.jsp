<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/bankList-script.jsp"%>
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
<div ng-controller='CampusController' class="list_right">
	<div class="well well-small">
      <!-- <div class="pull-left"> -->
        <!-- <font class="titleText">银行卡列表</font> -->
      <!-- </div> -->
	  <div class="pull-right" style="float: none;margin-left: 20px;">
	 		银行卡号：<input type="text" id="cardNo" style="width: 150px;" />
	 		银行开户人：<input type="text" id="bankAccount" style="width: 150px;" />
	 		银行名称：<input type="text" id="bankName" style="width: 150px;" />
	 		所属平台：<select id="customer_id" onchange="orders()" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option ng-repeat='b in customer' value="{{b.key}}">{{b.values}}</option>
	  		</select>
	 		状态：<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option  value="1">开启</option>
	  			<option  value="2">禁用</option>
	  		</select>
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	  		<a class="btn btn-primary" id="ADDBANK" style="display: none" href="javascript:openDetailIframe('bankAddPage.do');"><i class="icon-white icon-plus"></i>添加银行卡</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>银行卡号</th>
	      <th>银行开户人</th>
	      <th>银行名称</th>
	      <th>银行编码</th>
	      <th>每日上限金额</th>
	      <th>今日存款金额</th>
	      <th>尾号</th>
	      <th>使用时间范围</th>
	      <th>备注</th>
	      <th>状态</th>
	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat="user in users | orderBy : ['remarks', '-price']">
	      <td>{{user.cardNo}}</td>
	      <td>{{user.bankAccount}}</td>
	      <td>{{user.bankName}}</td>
	      <td>{{user.bankMark}}</td>
	      <td>{{user.maxLimit}}</td>
	      <td><span style="color: #0ac50a; font-weight: bold;">￥{{user.price | number : 2}}</span></td>
	      <td>{{user.tail}}</td>
	      <td>{{user.useStartTime}}-{{user.useEndTime}}</td>
	      <td>{{user.remarks}}</td>
	      <td>
	      <span ng-if="user.flag==1" style="background-color: #4d9c4d;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;" >
	      	开启
	      </span>
	      <span ng-if="user.flag==2" style="background-color: red;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;">
	      	禁用
	      </span>
	      <td>
	      	<span ng-if="user.editClick">
	      		<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>编辑</button>
	      	</span>
	      	<span ng-if="user.delClick">
	      		<button type="button" class="btn btn-danger" data="{{user.id}}" ng-click="huidiao($event.target)"><i data="{{user.id}}" class="icon-white icon-remove"></i>删除</button>
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
