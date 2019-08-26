<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>
<%@ include file="../../jsp.js/businessList-script.jsp"%>
<script src="../../js/clipboard.min.js"></script>
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
      <!-- <div class="pull-left">
        <font class="titleText">商户列表</font>
      </div> -->
	  <div class="pull-right">
	 		 用户名称：<select id="customer_id" style="width: 100px">
	  		<option value="0">--请选择--</option>
	  			<option ng-repeat='b in customer' value="{{b.key}}">{{b.values}}</option>
	  		</select>
	  		商户名称：<select id="uid" style="width: 100px">
	  			<option value="">--请选择--</option>
	  			<option ng-repeat='b in business' value="{{b.key}}">{{b.values}}</option>
	  		</select>
	  		<button type="button" class="btn btn-info" id="open" style="display: none"   ng-click="openClose($event.target,1)"><i class="icon-white icon-edit"  ></i>一键开启</button>
	  		<button type="button" class="btn btn-info"  id="close" style="display: none"   ng-click="openClose($event.target,2)"><i class="icon-white icon-edit"  ></i>一键关闭</button>
	  		商户状态：<select id="flag" style="width: 100px">
	  			<option value="0">--请选择--</option>
	  			<option  value="1" selected="selected">开启</option>
	  			<option  value="2">关闭</option>
	  		</select>
	  		商户查询：<input type="text" id="name" />
	  		<a class="btn btn-primary" href="" ng-click="query()"><i class="icon-white icon-search"></i>查询</a>
	  		<a class="btn btn-primary" id="edit" style="display: none" href="javascript:openDetailIframe('businessAddPage.do');"><i class="icon-white icon-plus"></i>添加类型</a>
	      <button id="btnRefresh" type="button" class="btn btn-info" ng-click="refreshCampus()"><i class="icon-white icon-refresh"></i>刷新</button>
	  </div>
	  <div class="clearfix"></div>
	</div>
	<table class="list-table">
	  <thead>
	    <tr class="col-name">
	      <th>所属用户</th>
	      <th>商户名称</th>
	      <th>支付类型</th>
	      <th>每日最大额度</th>
	      <th>今日已使用额度</th>
	      <th>固定金额</th>
	      <th>范围最小金额</th>
	      <th>范围最大金额</th>
	      <th>商户状态</th>
	      <th>操作</th>
	    </tr>
	    <tr class="sep-row"><td colspan="100"></td></tr>
	  </thead>
	  <tbody class="list-con">
	    <tr class="list-bd" ng-repeat="user in users | orderBy : ['customer_name', '-price']">
	      <td>{{user.customer_name}}</td>
	      <td>{{user.name}}</td>
	      <td>{{user.payType}}</td>
	      <td><span style="color: #f00; font-weight: bold;">￥{{user.maxLimit | number : 2}}</span></td>
	      <td>
			<span ng-if="(user.price)<(user.maxLimit*0.8)" style="color: #0ac50a; font-weight: bold;">￥{{user.price | number : 2}}</span>
			<span ng-if="(user.price)>=(user.maxLimit*0.8)" style="color: #f00; font-weight: bold;">￥{{user.price | number : 2}}</span>
		  </td>
	      <td>{{user.fixed_price}}</td>
	      <td>{{user.range_min}}</td>
	      <td>{{user.range_max}}</td>
	      <td>
	      <span ng-if="user.flag==1" style="background-color: #4d9c4d;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;" >
	      	开启
	      </span>
	      <span ng-if="user.flag==2" style="background-color: red;color: #fff;padding: 5px 10px;border-radius:2px; letter-spacing: 2px;">
	      	关闭
	      </span>
	      <td>
	      	<span ng-if="user.editClick">
	      		<button type="button" class="btn btn-info" data="{{user.id}}"  ng-click="editCampus($event.target)"><i class="icon-white icon-edit" data="{{user.id}}"></i>编辑</button>
	      	</span>
	      	
	      	<span ng-if="user.payType!='支付宝转银行' && user.payType!='微信转银行' && user.testClick">
	      		<button type="button" class="btn btn-info" payType="{{user.payType}}"  businessid="{{user.id}}" data="{{user.customer_id}}"  ng-click="upperScore($event.target)"><i class="icon-white icon-edit" payType="{{user.payType}}" data="{{user.customer_id}}" businessid="{{user.id}}"></i>测试通道</button>
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
	
	
	<div class="popbox urlshow">
	  <div class="bgwx">
	    <div class="bgimg">
	      <span class="close urlclose">✖</span>
	        <div class="itxt">
	        	<h3>跳转支付类型点击URL复制到手机浏览器访问</h3>
	        	<input type="text" class="srinput" id="money" value="" placeholder="请输入金额" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="customerid" value="" >
	        	<input type="hidden" id="businessid" value="" >
	        	<input type="hidden" id="payType" value="" >
	        	<input type="hidden" id="number" value="" >
	        	<span id="spanID"></span>
	        	<button class="qdbtn showParam" style="display: none" ng-click="showParam()">查看参数</button>
	        	<p>
	        		<button class="qdbtn" ng-click="inputUpperScore()">确定</button>
	        	    <button class="qxbtn urlqx">取消</button>
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
	        	<h3>谷歌验证码</h3>
	        	<input type="text" class="srinput" id="money" value="" placeholder="请输入验证码" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="uidTypes" value="" >
	        	<span id="spanID"></span>
	        	<p>
	        		<button class="qdbtn" ng-click="onekey()">确定</button>
	        	    <button class="qxbtn qxclose">取消</button>
	            </p>
	        </div>
	    </div>
	  </div>
	</div>
	
</div>

