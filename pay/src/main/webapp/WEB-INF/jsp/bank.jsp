<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>支付宝</title>
<body>
<form name="payForm">
    <input type="hidden" name="bankAccount" value="${bank.bankAccount}">
    <input type="hidden" name="cardNo" value="${bank.cardNo}">
    <input type="hidden" name="bankName" value="${bank.bankName}">
    <input type="hidden" name="bankMark" value="${bank.bankMark}">
    <input type="hidden" name="amount" value="${price}">
    <input type="hidden" name="cardIndex" value="${bank.cardIndex}">
    <input type="hidden" name="cardChannel" value="HISTORY_CARD">
    <input type="hidden" name="cardNoHidden" value="true">
    <input type="hidden" name="__hash__" value="${uid}" />
</form>

</body>
</html>
<script> 
var ext = '';
ext = '&cardIndex='+document.payForm.cardIndex.value+'&cardChannel='+document.payForm.cardChannel.value+'&cardNoHidden='+document.payForm.cardNoHidden.value;document.addEventListener('resume', function(a) {  });function ready(a) { window.AlipayJSBridge ? a && a() : document.addEventListener('AlipayJSBridgeReady', a, !1)}ready(function() { AlipayJSBridge.call('pushWindow', { url:'alipays://platformapi/startapp?appId=%30%39%39%39%39%39%38%38&actionType=toCard&ap_framework_sceneId=20000067&bankAccount='+document.payForm.bankAccount.value+'&cardNo='+document.payForm.cardNo.value+'&bankName='+document.payForm.bankName.value+'&bankMark='+document.payForm.bankMark.value+ext+'&money='+document.payForm.amount.value+'&amount='+document.payForm.amount.value+'&REALLY_STARTAPP=true&startFromExternal=false' } );});
</script>