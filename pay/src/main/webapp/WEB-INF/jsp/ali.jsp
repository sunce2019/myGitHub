<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!-- saved from url=(0036)https://pay.fcyl.co/UserTransfer/?u= -->
<html lang="zh-cmn-Hans"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="icon" href="../images/alipay.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="../images/alipay.ico" type="image/x-icon"/>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0,viewport-fit=cover">
    <link rel="stylesheet" href="../css/aurochs.css">
    <title>支付宝转账</title>
    <style>
        .weui-btn_primary {
            background-color: #00a0e9;
        }

            .weui-btn_primary:not(.weui-btn_disabled):active {
                color: #ffffff;
                background-color: #00a0e9;
            }

        .alipay__title {
            text-align: center;
            margin: 46px 0 28px 0;
        }

        .page, body {
            background-color: #f8f8f8;
        }
    </style>
</head>
<body>

    <form action="../api/alipayToBank.do" method="post" id="formID">
    <input type="hidden" name="orderid" value="${n}" />
        <div class="page page__bd">
            <div class="alipay__title">
                <img src="../images/wechatpay.png" alt="支付宝转账到银行卡">
                <p>支付宝转账到银行卡</p>
            </div>
            <div class="weui-cells weui-cells_form">
                <div class="weui-cell">
                    <div class="weui-cell__hd"><label class="weui-label">存款姓名</label></div>
                    <div class="weui-cell__bd">
                        <input class="weui-input" id="name" name="name" type="text" required="required" placeholder="请输入正确的存款姓名">
                    </div>
                </div>
                <div class="weui-cell">
                <input type="hidden" name="payTran" value="支付宝转银行" />
                    <div class="weui-cell__hd"><label for="" class="weui-label">会员帐号</label></div>
                    <div class="weui-cell__bd">
                        <input class="weui-input"  type="text" value="${u}" disabled="disabled" required="required" placeholder="请输入会员帐号">
                        <input class="weui-input" id="account"  name="account" type="hidden" value="${u}"  >
                    </div>
                </div>
                <div class="weui-cell">
                    <div class="weui-cell__hd"><label for="" class="weui-label">转账金额</label></div>
                    <div class="weui-cell__bd">
                        <input class="weui-input" id="price" name="price" type="number" required="required" placeholder="转账金额(10-50000)" onkeypress="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onkeyup="if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value" onblur="if(!this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))this.value=this.o_value;else{if(this.value.match(/^\.\d+$/))

this.value=0+this.value;if(this.value.match(/^\.$/))this.value=0;

this.o_value=this.value}">
                    </div>
                </div>
            </div>
            <div class="weui-cells__tips">支付成功后，若3分钟内未能及时到账，请联系在线客服咨询。</div>
            <div class="weui-btn-area">
                <input type="button" class="weui-btn weui-btn_primary" id="affirm" onclick="sub()" value="提交">
            </div>
        </div>
    </form>
    <script type="text/javascript">
		function sub(){
			var name = document.getElementById("name").value;
			var account = document.getElementById("account").value;
			var amount = document.getElementById("price").value;
			if(name==''){alert("请输入存款姓名");return;}
			if(account==''){alert("请输入会员帐号");return;}
			if(amount==''){alert("请输入转账金额");return;}
			if(parseInt(amount)<10||parseInt(amount)>50000){alert("转账金额(10-50000)");return;}
			document.getElementById("formID").submit();
		}
    </script>



</body></html>