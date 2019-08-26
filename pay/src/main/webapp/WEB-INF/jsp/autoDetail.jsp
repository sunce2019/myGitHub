<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../../main.jsp" %>  
<%@ include file="../../jsp.js/autoDetail-script.jsp"%>
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
            <strong>编辑自动对接</strong>
        </p>
        <form id="infro" name="infro" action="threeFile.do"  
        method="post" enctype="multipart/form-data"> 
        <input type="hidden" id="id" value="${id}" >
		<%-- 主键 --%>
		<!-- 创建时间 -->
        <div class="input-label">
            <label class="label-text ">商户号：</label>
            <div class="label-box form-inline control-group">
            	<select id="uid" style="width: 100px" ng-change="uidChange()" ng-model="auto.businessid" >
		  			<option value="0">--请选择--</option>
		  			<option ng-repeat='b in auto.autoLoad.business' ng-selected="b.id==auto.businessid" value="{{b.id}}">{{b.name}}</option>
		  		</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">加密方式：</label>
            <div class="label-box form-inline control-group">
            	<select style="width: 100px" ng-model="auto.singType">
            		<option ng-repeat='b in auto.autoLoad.singType' ng-selected="b.code==auto.singType" value="{{b.code}}">{{b.text}}</option>
            	</select>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">加密编码：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" ng-model="auto.signCode"  style="width: 50px" />
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">参数列表：</label>
            <div class="label-box form-inline control-group">
            <a class="btn btn-primary" href="#" ng-click="addParam()" ><i class="icon-white icon-plus"></i>添加请求字段</a></br>
            	<span ng-repeat=" z in auto.paramList">
            	字段名：<input type="text" ng-model="z.name" style="width: 120px" /> 
            	字段值的类型：
            			<select ng-model="z.valType" style="width: 100px">
            				<option ng-repeat='b in auto.autoLoad.attribute' ng-selected="b.code==z.valType" value="{{b.code}}">{{b.text}}</option>
            			</select>
            	字段类型：
            		<select ng-model="z.type" ng-click="paramTypeChange(z,$event.target)" style="width: 100px">
            			<option ng-repeat='b in auto.autoLoad.parameType' ng-selected="b.code==z.type" value="{{b.code}}">{{b.text}}</option>
            		</select>
            		<span ng-show="z.type=='FIXED'">
            	字段值：<input type="text" ng-model="z.val"  style="width: 50px" /></span>
            	是否参与签名：
	            		<label style="width: 50px"><input type="radio" name="flag1{{$index}}" ng-model="z.flag" value="0" />yes  &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="z.flag" name="flag1{{$index}}" value="1" />no&nbsp;</label>
            		
            	是否urlencode：<label style="width: 50px"><input type="radio" name="urlFlag1{{$index}}" ng-model="z.urlFlag" value="0" />no  &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="z.urlFlag" name="urlFlag1{{$index}}" value="1" />yes&nbsp;</label>
            		<span ng-if="$index>0">
            			<button type="button" class="btn btn-danger" ng-click="delParam($index)"><i  class="icon-white icon-remove"></i>删除</button>
            		</span>
            		</br>
            	</span>	
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">金额类型：</label>
            <div class="label-box form-inline control-group">
            	<select ng-model="auto.amountType">
            		<option ng-repeat='b in auto.autoLoad.amountType' ng-selected="b.code==auto.amountType" value="{{b.code}}">{{b.text}}</option>
            	</select>
            	
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">支付类型列表：</label>
            <div class="label-box form-inline control-group">
            <a class="btn btn-primary" href="#" ng-click="addPayType()" ><i class="icon-white icon-plus"></i>添加支付类型</a></br>
            	<span ng-repeat=" z in auto.payTypeList">
            	第三方值：<input type="text" ng-model="z.values" style="width: 120px" /> 
            	类型：<select ng-model="z.typeCode" style="width: 120px">
           			<option ng-repeat='b in auto.autoLoad.payTran' ng-selected="b.code==z.typeCode"  value="{{b.code}}">{{b.text}}</option>
           		</select>
           		处理方式：<select ng-model="z.jumpType" style="width: 120px">
           			<option ng-repeat='b in auto.autoLoad.jumpType' ng-selected="b.code==z.jumpType"  value="{{b.code}}">{{b.text}}</option>
           		</select>
           		<span ng-if="$index>0">
            			<button type="button" class="btn btn-danger" ng-click="delPayType($index)"><i  class="icon-white icon-remove"></i>删除</button>
            		</span>
            		</br>
            	</span>	
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">key排序：</label>
            <div class="label-box form-inline control-group">
           	    <label><input type="radio" name="keySort" ng-model="auto.keySort" ng-checked="true" ng-change="keySortChange()" value="0"  />不参与排序 &nbsp;</label><label><input type="radio" ng-change="keySortChange()" ng-model="auto.keySort" name="keySort" value="1" />参与排序&nbsp;</label>
                <span class="help-inline"></span>
            </div>
        </div>
        <span class="keyStr" ng-show="auto.keySort==0">
	        <div class="input-label">
	            <label class="label-text ">key拼接字符1：</label>
	            <div class="label-box form-inline control-group">
	            	<input type="text" ng-model="auto.keyStr1"  style="width: 50px" /> <span>不填写，不进行字符串拼接</span>
	                <span class="help-inline"></span>
	            </div>
	        </div>
	        
	        <div class="input-label">
	            <label class="label-text ">key拼接字符2：</label>
	            <div class="label-box form-inline control-group">
	            	<input type="text" ng-model="auto.keyStr2"  style="width: 50px" /> <span>不填写，不进行字符串拼接</span>
	                <span class="help-inline"></span>
	            </div>
	        </div>
	        
	        <div class="input-label">
	            <label class="label-text ">key拼接字符3：</label>
	            <div class="label-box form-inline control-group">
	            	<input type="text" ng-model="auto.keyStr3"  style="width: 50px" /> <span>不填写，不进行字符串拼接</span>
	                <span class="help-inline"></span>
	            </div>
	        </div>
        </span>
        <div class="input-label">
            <label class="label-text ">字段加密排序：</label>
            <div class="label-box form-inline control-group">
            	<select ng-model="auto.sort" ng-click="sortChange(auto.sort,$event.target)">
            		<option ng-repeat='b in auto.autoLoad.sort' ng-selected="b.code==auto.sort" value="{{b.code}}">{{b.text}}</option>
            	</select>
            	
            	<span ng-show="auto.sort=='CUSTOM'"></br>排序顺序：<input type="text" ng-model="auto.customSort" /> <span style="color: red">自定义排序：将参数列表里字段名 使用,分隔如：a,b,c</span></span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">加密拼接字符串1：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" ng-model="auto.signStr1"  style="width: 50px" /> <span>不填写，不进行字符串拼接</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">加密拼接字符串2：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" ng-model="auto.signStr2"  style="width: 50px" /> <span>不填写，不进行字符串拼接</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">签名大小写：</label>
            <div class="label-box form-inline control-group">
           	    <label style="width: 50px"><input type="radio" name="sizeWrite" ng-model="auto.sizeWrite" value="0"  />大写 &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="auto.sizeWrite" name="sizeWrite" value="1"  />小写&nbsp;</label>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">请求方式：</label>
            <div class="label-box form-inline control-group">
            	<select ng-model="auto.requestMethod" ng-change="requestMethodChange()">
            		<option ng-repeat='b in auto.autoLoad.requestMethod' ng-selected="b.code==auto.requestMethod" value="{{b.code}}">{{b.text}}</option>
            	</select>
                <span class="help-inline"></span>
            </div>
        </div>
        <span ng-show="auto.requestMethod!='FRPOSTFROM'">
	        <div class="input-label">
	            <label class="label-text ">请求数据方式：</label>
	            <div class="label-box form-inline control-group">
	           	    	<label><input type="radio" name="requestData" ng-model="auto.requestData" value="0"  />传统拼接 &nbsp;</label><label><input type="radio" ng-model="auto.requestData" name="requestData" value="1" />JSON格式</label>
	                <span class="help-inline"></span>
	            </div>
	        </div>
	        
	        <div class="input-label">
	            <label class="label-text ">返回判断字段名：</label>
	            <div class="label-box form-inline control-group">
	           	    	<input type="text" ng-model="auto.flagField" style="width: 120px"  />  
	           	    	下单成功字符值： <input type="text" ng-model="auto.sucstr" style="width: 120px"  /> 
	           	    	</br></br>
	           	    	<a class="btn btn-primary" href="#" ng-click="addLayer()" ><i class="icon-white icon-plus"></i>添加字段</a>
	           	    	<span style="color: red">如多层级字段，添加多个{"code":"SUCCESS","model":{"url":"http://..."}} 添加 model,url字段，最后就是提交URL打开地址，或者生成二维码地址</span>
	           	    	</br>
	           	    	<span ng-repeat=" l in auto.layerList track by $index">
	           	    		{{$index+1}}：<input type="text" ng-model="l.val" style="width: 120px"  />
	           	    		<span ng-if="$index>0">
		            			<button type="button" class="btn btn-danger" ng-click="delLayer($index)"><i  class="icon-white icon-remove"></i>删除</button>
		            		</span>
	           	    		</br>
	           	    	</span>
	                <span class="help-inline"></span>
	            </div>
	        </div>
        </span>
        <div class="input-label">
            <label class="label-text ">回调参数列表：</label>
            <div class="label-box form-inline control-group">
           	    	<a class="btn btn-primary" href="#" ng-click="addbackParam()" ><i class="icon-white icon-plus"></i>添加参数</a>
           	    	</br>
           	    	<span ng-repeat=" l in auto.backParamList">
           	    		字段名：<input type="text" ng-model="l.name" style="width: 120px"  />
           	    		字段类型：
	            		<select ng-model="l.type" ng-click="changeBack(l,$event.target)" style="width: 100px">
	            			<option ng-repeat='b in auto.autoLoad.parameType' ng-selected="b.code==l.type" value="{{b.code}}">{{b.text}}</option>
	            		</select>
	            		<span ng-show="l.type=='BACKSUC'" >
	            		字段值：<input type="text" ng-model="l.val" style="width: 50px" /></span>
	            		是否参与签名：
	            		<label style="width: 50px"><input type="radio" name="flag{{$index}}" ng-model="l.flag" value="0"  />yes &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="l.flag" name="flag{{$index}}" value="1" />no&nbsp;</label>
           	    		
           	    		是否urldecode：<label style="width: 50px"><input type="radio" name="urlFlag3{{$index}}" ng-model="l.urlFlag" value="0" />no  &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="l.urlFlag" name="urlFlag4{{$index}}" value="1" />yes&nbsp;</label>
           	    		<span ng-if="$index>0">
	            			<button type="button" class="btn btn-danger" ng-click="delbackParam($index)"><i  class="icon-white icon-remove"></i>删除</button>
	            		</span>
           	    		</br>
           	    	</span>
                <span class="help-inline"></span>
            </div>
        </div>
 <!--        <div class="input-label">
            <label class="label-text ">验签大小写：</label>
            <div class="label-box form-inline control-group" >
           	    <label style="width: 50px">
           	    {{auto.yqSizeWrite}}
           	    <input type="radio" name="yqSizeWrite" ng-model="auto.yqSizeWrite" value="0"  />大写 &nbsp;</label><label style="width: 50px"><input type="radio" ng-model="auto.yqSizeWrite" name="yqSizeWrite" value="1"  />小写&nbsp;</label>
                <span class="help-inline"></span>
            </div>
        </div> -->
        <div class="input-label backsort" ng-show="auto.sort=='CUSTOM'">
            <label class="label-text ">回调自定义排序：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" ng-model="auto.backSort" /> <span style="color: red">自定义排序：将回調参数列表里字段名 使用,分隔如：a,b,c</span>
                <span class="help-inline"></span>
            </div>
        </div>
        
        <div class="input-label">
            <label class="label-text ">回调返回字符串：</label>
            <div class="label-box form-inline control-group">
            	<input type="text" ng-model="auto.banckSuccess" />
                <span class="help-inline"></span>
            </div>
        </div>
        
        </form>
	    </div>
  </div>
  <div class="bottom-bar">
    <div class="con">
      <a class="btn btn-large btn-primary fr btn-first1" ng-click="submit()" href="javascript:;"><i class="icon-white icon-ok"></i> 确定</a>
      <a class="btn btn-large" href="javascript:refreshList()"><i class="icon-chevron-up"></i> 返回列表</a>
    </div>
  </div>
  
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
	        	<span id="spanID"></span>
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
	        	<input type="text" class="srinput" id="code" value="" placeholder="请输入谷歌验证器" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))
this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;
this.o_value=this.value}">
	        	<input type="hidden" id="uidTypes" value="" >
	        	<span id="spanID"></span>
	        	<p>
	        		<button class="qdbtn" ng-click="inputUpperScore()">确定</button>
	        	    <button class="qxbtn qxclose">取消</button>
	            </p>
	        </div>
	    </div>
	  </div>
	</div>
  
</div>