package com.pay.bank;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Repository;

import com.pay.util.StringUtils;
import com.pay.vo.BankVo;

/**
*@author star
*@version 创建时间：2019年5月4日下午5:00:24
*/
@Repository
public class CCB implements BankBase {
	private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("busi1");
	@Override
	public BankVo analysis(String time, String content, String machine_num) throws Exception {
		try {
			if(StringUtils.isBlank(time)||StringUtils.isBlank(content)||StringUtils.isBlank(machine_num)){logger.info("数据错误："+content);return null;}
			logger.info("time="+time+",content="+content+",machine_num="+machine_num);
			String cardNo =  subString(content, "贵公司尾号", "的账户");
			if(StringUtils.isBlank(cardNo))
				cardNo =  subString(content, "您尾号", "的储蓄卡账户");
			
			String transferPerson =  subString(content, "分", "支付宝转账收入");
			
			String price = subString(content, "支付宝转账收入人民币", "元，余额");
			if(StringUtils.isBlank(price))
				price =  subString(content, "支付机构提现收入人民币", "元,活期");
			if(StringUtils.isBlank(price))
				price =  subString(content, "币", "元");
			price = price.replace(",","");
			if(cardNo==null || price==null) {logger.info("数据不完整："+content);return null;}
			return new BankVo(cardNo, StringUtils.SDF.parse(time), transferPerson, machine_num, Double.parseDouble(price));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("银行卡消息解析失败："+"time="+time+",content="+content+",machine_num="+machine_num); 
		}
		
	}
	
	public static void main(String[] args) {
		String content = "您尾号6096的储蓄卡账户5月8日18时43分银联入账收入人民币30.01元,活期余额196.29元。[建设银行]";
		try {
			BankVo BankVo = new CCB().analysis("2019-05-04 16:11:31", content, "1");
			System.out.println(BankVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String subString(String str, String strStart, String strEnd)throws Exception {
		 
        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);
 
        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return null;
        }
        if (strEndIndex < 0) {
            return null;
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }
	
}
