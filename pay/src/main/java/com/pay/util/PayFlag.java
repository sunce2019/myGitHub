package com.pay.util;

public class PayFlag {
	public static int INITS=0;
	public static int SUCCESS=1;
	public static int FAILURE=2;
	public static int RETREAT=3;
	public static int RETREATS=4;
	public static int RETREATFAILURE=5;
	public static int PROCESS=6;
	public static int EXPIRED=7;
	
	//0:未支付 1:成功2:失败3:已退款4:退款处理中5：退款失败6：支付处理中 7：订单过期
}
