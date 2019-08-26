<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/payorderList-script.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
<div ng-controller='CampusController' class="list_right">
	<div class="well well-small">
      <!-- <div class="pull-left"> -->
        <!-- <font class="titleText">订单列表</font> -->
      <!-- </div> -->
	  <div class="pull-right" style="float: none;margin-left: 20px; ">
	  		我方单号:<input type="text" id="orderNumber" style="width: 90px;"/>
	  		上游单号:<input type="text" id="gameOrderNumber" style="width: 90px;"/>
	  		用户查询：<input type="text" id="userName" style="width: 90px;" />
	  		备注搜索:<input type="text" id="remarks" style="width: 90px;"/>
			
	  		金额类型：
			<select id="priceType" style="width: 96px;">
	  			<option value="price">支付金额</option>
	  			<option value="realprice">实际金额</option>
	  		</select>
	  		金额范围：<input type="text" id="minPrice" style="width: 50px" />-<input type="text" id="maxPrice" style="width: 50px" />

			<br/>
			
	  		商户渠道：
			<select id="uid" style="width: 100px;">
	  			<option value="">--请选择--</option>
	  			<option ng-repeat='b in business' value="{{b.key}}">{{b.values}}</option>
	  		</select>
	  		订单来源：
			<select id="customer_id" onchange="orders()" style="width: 100px">
	  		<option value="0">--请选择--</option>
	  			<option ng-repeat='b in customer' value="{{b.id}}">{{b.name}}</option>
	  		</select>
	  		<c:if test="${user.type!=1}">
	  			<input type="hidden" id="customer_id" value="0">
	  		</c:if>
	  		支付类型：
			<select id="orderType" style="width: 100px">
	  			<option value="">--请选择--</option>
	  			<option ng-repeat='b in patTran' value="{{b}}">{{b}}</option>
	  		</select>
	  		支付状态：
			<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option value="2">已支付</option>
	  			<option value="1">未支付</option>
	  		</select>
	  		开始时间：<input type="text" id="start" style="width: 75px" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,maxDate:'#F{$dp.$D(\'end\')}'})" value="<%=st%>" />
	  		结束时间：<input type="text" id="end" style="width: 75px"  onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd HH:mm:ss', isShowToday: false, isShowClear: false,minDate:'#F{$dp.$D(\'start\')}'})" value="<%=en%>" />
	  		
			<br/>
			
			<div style="float:right">
			自动刷新：
	      	<select ng-init="autos=0;" id="auto" ng-model="autos" ng-change="auto()" style="width: 90px">
	  			<option value="0">--请选择--</option>
	  			<option value="3000">3秒</option>
	  			<option value="5000">5秒</option>
	  			<option value="10000">10秒</option>
	  		</select>
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	      	<a class="btn btn-primary" href="" id="export" ng-click="download()" style="display: none"><i class="icon-white icon-plus"></i>导出</a>
	      	</div>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	      
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>我方单号</th>
	      <th>上游单号</th>
	      <th>用户</th>
	      <th>商户渠道</th>
	      	<th>订单来源</th>
	      <th>支付类型</th>
	      <th>支付金额</th>
	      <th>实际金额</th>
	      <th>支付状态</th>
	      <th>订单创建时间</th>
	      <th>商户回调时间</th>
	      <th>回调状态</th>
	      <th>回调成功时间</th>
	      <th>备注</th>
	      <th>操作</th>
	    </tr>
	   
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat='user in users'>
	      <td>{{user.orderNumber}}</td>
	      <td>{{user.gameOrderNumber}}</td>
	      <td><a href="javascript:void(0);" data="{{user.useragent}}" ng-click="useragent($event.target)" style="color:#0000cc;text-decoration:underline" target="_blank"> {{user.userName}}</a> - [{{user.vip}}]</br><a href="https://ip.cn/{{user.ip}}" style="color:#0000cc;text-decoration:underline" target="_blank">{{user.ip}}</a></td>
	      <td>{{user.payName}}</td>
	      <td>{{user.customer_name}}</td>
	      <td>{{user.orderType}}
			<span ng-if="user.orderType=='支付宝转银行' || user.orderType=='微信转银行' || user.orderType=='云闪付' ">({{user.tail}})</span>
		  </td>
	      <td>{{user.price}}
	      	<span ng-if="user.payName=='速宝支付'">({{user.realprice}})</span>
	      </td>
	      <td>
	      	<span ng-if="user.flag==2">{{user.realprice}}</span>
	      	<span ng-if="user.flag==1">0</span>
	      </td>
	      <td width="46">
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
	      <td>{{user.callbackTime}}</td>
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
	      	<span ng-if="user.remarks=='金额相同;'" style="background-color: red;color: #fff;padding: 5px 5px" >{{user.remarks}}</span>
	      	<span ng-if="user.remarks!='金额相同;'" >{{user.remarks}}</span>
	      </td>
	      <td>
	      	<span ng-if="user.callGameFlag==2 && user.callGameNumber>=11 && user.backClick">
	      		<button type="button" class="btn btn-info" data="{{user.orderNumber}}"  ng-click="huidiao($event.target)"><i class="icon-white icon-edit" data="{{user.orderNumber}}"></i>回调游戏1</button>
	      	</span>
	      	<span ng-if="user.backClick2">
	      		<button type="button" class="btn btn-info"   ng-click="showHuidiao2(user.id)"><i class="icon-white icon-edit" ></i>回调游戏2</button>
	      	</span>
	      	<span ng-if="(user.orderType=='支付宝转银行' || user.orderType=='微信转银行' || user.orderType=='云闪付' || user.orderType=='微信买单')  && user.flag==1  && user.returnError=='' && user.upScoreClick ">
	      		<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="upperScore($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>上分</button>
	      	</span>
	      	<span ng-if=" user.returnError!='' && user.apiClick ">
	      		<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="showApiError($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>查看API</button>
	      	</span>
	      </td>
	    </tr>
		<tr class="total_num">
	      <td></td>
	      <td></td>
	      <td></td>
	      <td ><span class="itxtnum" id="tname"></span></td>
	      <td colspan="2"><span class="itxtnum">总支付金额:</span><br />
			<span style="color: #f00;">￥{{payOrderCount.obj.priceSum | number : 2}}</span><br />
			(<span style="color: #f00;font-size:10px;font-weight:bold">{{totalCount}}条</span>)
		  </td>
	      <td colspan="2"><span class="itxtnum">总实际金额:</span><br />
			<span style="color: #0ac50a;">￥{{payOrderCount.obj.realpriceSum | number : 2}}</span><br />
			(<span style="color: #0ac50a;font-size:10px;font-weight:bold">{{totalCount*payOrderCount.obj.successNumber/100 | number : 0}}条</span>)
		  </td>
	      <td colspan="2"><span class="itxtnum">金额成功率:</span><br />
	      	<span ng-if="payOrderCount.obj.successRate>=60" style="color: #0ac50a;" >{{payOrderCount.obj.successRate}}%</span>
	      	<span ng-if="payOrderCount.obj.successRate<60" style="color: #f00;" >{{payOrderCount.obj.successRate}}%</span><br />
			(<span style="font-size:10px;font-weight:bold">目标: 60%+</span>)
	      </td>
	      <td colspan="2"><span class="itxtnum">笔数成功率:</span><br />
	      	<span ng-if="payOrderCount.obj.successNumber>=60" style="color: #0ac50a;" >{{payOrderCount.obj.successNumber}}%</span>
	      	<span ng-if="payOrderCount.obj.successNumber<60" style="color: #f00;" >{{payOrderCount.obj.successNumber}}%</span><br />
			(<span style="color: #0ac50a;font-size:10px;font-weight:bold">{{totalCount*payOrderCount.obj.successNumber/100 | number : 0}}条</span>/<span style="color: #f00;font-size:10px;font-weight:bold">{{totalCount}}条</span>)
	      </td>
	      <td colspan="2"><span class="itxtnum">平均成功率:</span><br />
	      	<span ng-if="((payOrderCount.obj.successRate+payOrderCount.obj.successNumber)/2 | number : 2)>=60" style="color: #0ac50a;" >{{(payOrderCount.obj.successRate+payOrderCount.obj.successNumber)/2 | number : 2}}%</span>
	      	<span ng-if="((payOrderCount.obj.successRate+payOrderCount.obj.successNumber)/2 | number : 2)<60" style="color: #f00;" >{{(payOrderCount.obj.successRate+payOrderCount.obj.successNumber)/2 | number : 2}}%</span><br />
			<span style="font-size:10px;font-weight:bold">({{payOrderCount.obj.successRate  | number : 2}}%+{{payOrderCount.obj.successNumber  | number : 2}}%)/2</span>
	      </td>
	      <td></td>
	      <td></td>
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
	        	<h3>客服输入上分金额</h3>
	        	<input type="text" class="srinput" id="upperMoney" value="" placeholder="请输入金额" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="upperId" value="" >
	        	<p>
	        		<button class="qdbtn" ng-click="inputUpperScore()">确定</button>
	        	    <button class="qxbtn" onclick="qxbtnclose()" >取消</button>
	            </p>
	        </div>
	    </div>
	  </div>
	</div>
	
	
	<div class="popbox boxopen">
	  <div class="bgwx">
	    <div class="bgimg">
	      <span class="close opneclose">✖</span>
	        <div class="itxt">
	        	<h3>请输入回调金额</h3>
	        	<input type="text" class="srinput" id="backMoney" value="" placeholder="请输入金额" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="backId" value="" >
	        	<p>
	        		<button class="qdbtn" ng-click="huidiao2()">确定</button>
	        	    <button class="qxbtn qxclose" onclick="qxbtnclose()" >取消</button>
	            </p>
	        </div>
	    </div>
	  </div>
	</div>
	
	
</div>


