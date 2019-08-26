package com.pay.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.pay.model.PayOrder;
import com.pay.vo.PayOrderCount;


/**
 * @author star
 * @version 创建时间：2019年4月13日下午1:11:54
 */
public class ExcleImpl {
	
	public static void export(List<PayOrder> list,PayOrderCount payOrderCount, ServletOutputStream out) throws Exception {
		try {
			// 第一步，创建一个workbook，对应一个Excel文件
			HSSFWorkbook workbook = new HSSFWorkbook();

			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet hssfSheet = workbook.createSheet("sheet1");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short

			HSSFRow row = hssfSheet.createRow(0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle hssfCellStyle = workbook.createCellStyle();

			// 居中样式
			hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			String[] titles = new String[] {"订单号","订单人","商户渠道","订单来源","支付类型","支付金额","实际金额","支付状态","订单创建时间","商户回调时间"};
			HSSFCell hssfCell = null;
			for (int i = 0; i < titles.length; i++) {
				hssfCell = row.createCell(i);// 列索引从0开始
				hssfCell.setCellValue(titles[i]);// 列名1
				hssfCell.setCellStyle(hssfCellStyle);// 列居中显示
			}

			for (int i = 0; i < list.size(); i++) {
				row = hssfSheet.createRow(i + 1);
				
				PayOrder clock = list.get(i);

				// 第六步，创建单元格，并设置值
				String name = null;
				if (clock.getGameOrderNumber() != null) {
					name = clock.getGameOrderNumber();
					hssfSheet.setColumnWidth(0, clock.getGameOrderNumber().getBytes().length*1*256);
				}
				row.createCell(0).setCellValue(name);
				
				String department = "";
				if (clock.getUserName() != null) {
					department = clock.getUserName();
				}
				row.createCell(1).setCellValue(department);
				
				String type = "";
				if (clock.getPayName() != null) {
					type = clock.getPayName();
				}
				row.createCell(2).setCellValue(type);
				
				String starts = null;
				if (clock.getCustomer_name() != null) {
					starts = clock.getCustomer_name();
					hssfSheet.setColumnWidth(3, clock.getCustomer_name().getBytes().length*1*256);
				}
				row.createCell(3).setCellValue(starts);
				
				String ends = null;
				if (clock.getOrderType() != null) {
					ends = clock.getOrderType();
					hssfSheet.setColumnWidth(4, ends.getBytes().length*1*256);
				}
				row.createCell(4).setCellValue(ends);
				
				String uses = null;
				if (clock.getPrice()!= null) {
					uses = Double.toString(clock.getPrice());
				}
				row.createCell(5).setCellValue(uses);
				
				String remarks = null;
				if (clock.getRealprice() != null) {
					remarks = Double.toString(clock.getRealprice());
				}
				row.createCell(6).setCellValue(remarks);
				
				String adminRemarks = null;
				if (clock.getFlag() != null) {
					adminRemarks = clock.getFlag()==1?"未支付":clock.getFlag()==2?"已支付":"未知";
				}
				row.createCell(7).setCellValue(adminRemarks);
				
				String addTime = null;
				if (clock.getAddTime() != null) {
					addTime = StringUtils.SDF.format(clock.getAddTime());
					hssfSheet.setColumnWidth(8, addTime.getBytes().length*1*256);
				}
				row.createCell(8).setCellValue(addTime);
				
				String callbackTime = null;
				if (clock.getCallbackTime() != null) {
					callbackTime = StringUtils.SDF.format(clock.getCallbackTime());
					hssfSheet.setColumnWidth(9, callbackTime.getBytes().length*1*256);
				}
				row.createCell(9).setCellValue(callbackTime);
			}
			
			row = hssfSheet.createRow(list.size()+1);
			
			row.createCell(5).setCellValue(payOrderCount.getPriceSum());
			row.createCell(6).setCellValue(payOrderCount.getRealpriceSum());
			// 第七步，将文件输出到客户端浏览器
			try {
				workbook.write(out);
				out.flush();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("导出信息失败！");

		}
	}
}
