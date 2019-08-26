package com.pay.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dao.BaseDao;
import com.pay.dao.RedisModel;
import com.pay.enums.PayTran;
import com.pay.enums.RedisKey;
import com.pay.model.Business;
import com.pay.model.Customer;
import com.pay.model.Menu;
import com.pay.model.PayOrder;
import com.pay.payAssist.ToolKit;
import com.pay.service.PayOrderService;
import com.pay.service.RedisLockService;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;

import Decoder.BASE64Encoder;
import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年4月1日下午4:04:27
 */
@Controller
@RequestMapping("/test")
@SuppressWarnings("all")
public class TestController extends BaseController {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<Menu> menuDao;
	@Autowired
	private RedisLockService redisLockService;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private RedisModel redisModel;

	public static void main(String[] args) throws Exception {
		// 模拟一个客户请求
		String key = "xhf0Bhv5smvv29fxvxpjV0Td4tQwwa0f";
		String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtsuuKaukWZGJ8qfxtcDfLXO+DIc/C8xfqkuUqkEaHMHfWAYf+xmnnIb+0v0j37jpL8zqLJRA+fWN57viTCv9voAgVPnLXjB9ceotI1azC7HfPxz6AJltzNc7leP4e/eFs4fBc1OUqhUZn4OXZXWQiLrjO1/ruZZ7f+ovy4Rx5qwIDAQAB";
		String uid = "194592037350313555"; // 商务号

		// String key = "46F4988FE56E44E88EAA062AAC108482";
		// String public_key =
		// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNyIw11G6PQ1iaqYdao0/MyfznJyFyTGg+e5xyLICIdxaJvsKjb2Qo8u4YvTUsv6YJJye68FAFbDoGXssDkVaGvAx7sPNROSD9gVvUU7dzMnzVqOA2pxxukI0bjNoGMYx3vvHLgLfyCJbc1QmH1P7lxYYaqtmFRAR5NMkwfOrDSQIDAQAB";
		// String uid = "0C933E6D465F49D98FE03EAA7C3ACA22"; // 商务号

		String price = "1";
		String istype = "YSF_BANK"; // 测试支付宝类型
		// String istype = "ZFB";
		String number = getOrderIdByUUId();
		new TestController().testXCS(key, public_key, uid, price, istype, 0, number);
		
		// zbpay();
		// aa();
		// 手动测试回调
		// {msg=, ovalue=100.00, orderid=SP20190822213001283825,
		// sysorderid=19082221300395244930, orderamt=100.00,
		// sign=40f41ba3dd7ef44247ebacefaedafbc3, attach=, opstate=0,
		// completiontime=2019/08/22 21:30:26}
		// ToolKit.request("http://localhost:8080/callBack/callBack.do",
		// "msg=&ovalue=100.00&orderid=SP20190822213001283825&sysorderid=19082221300395244930&orderamt=100.00&sign=40f41ba3dd7ef44247ebacefaedafbc3&attach=&opstate=0&completiontime=2019/08/22
		// 21:30:26");

	}

	private static void aa() {
		String backgroundPath = "D:\\2.jpg";
		String qrCodePath = "D:\\1.png";
		String outPutPath = "D:\\end.jpg";
		overlapImage(backgroundPath, qrCodePath, outPutPath);
	}

	public static String overlapImage(String backgroundPath, String qrCodePath, String outPutPath) {
		try {
			// 设置图片大小
//            BufferedImage background = resizeImage(618,1000, ImageIO.read(new File("这里是背景图片的路径！")));
			BufferedImage background = resizeImage(1000, 618, ImageIO.read(new File(backgroundPath)));
//            BufferedImage qrCode = resizeImage(150,150,ImageIO.read(new File("这里是插入二维码图片的路径！")));
			BufferedImage qrCode = QRCode.generalQRCode("aaa", 200, 200);
			// 在背景图片中添加入需要写入的信息，例如：扫描下方二维码，欢迎大家添加我的淘宝返利机器人，居家必备，省钱购物专属小秘书！
			// String message = "扫描下方二维码，欢迎大家添加我的淘宝返利机器人，居家必备，省钱购物专属小秘书！";
			Graphics2D g = background.createGraphics();
			g.setColor(Color.white);
			g.setFont(new Font("微软雅黑", Font.BOLD, 20));
			// 在背景图片上添加二维码图片924 554 168
			g.drawImage(qrCode, 756, 400, 168, 154, null);
			g.dispose();
//            ImageIO.write(background, "jpg", new File("这里是一个输出图片的路径"));
			ImageIO.write(background, "jpg", new File(outPutPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage resizeImage(int x, int y, BufferedImage bfi) {
		BufferedImage bufferedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(bfi.getScaledInstance(x, y, Image.SCALE_SMOOTH), 0, 0, null);
		return bufferedImage;
	}

	private static void testAutoBack() {
		// source_order_id=1000000433349387, merchant_id=10800377, status=1}
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("merchantCode", "aaaa");
		vmap.put("amount", "500");
		vmap.put("orderNumber", "1000002002650963");
		vmap.put("payCode", "orderPaid");
		vmap.put("trxNo", "aaa");
		vmap.put("submitTime", "ddd");
		vmap.put("commodityName", "aa");
		vmap.put("payStatus", "orderPaid");
		vmap.put("payTime", "aaa");
		vmap.put("remark", "ddd");

		String sign = getMapParam(vmap) + "a37dffebd0084414a2adc348ec694531";
		vmap.put("sign", Encryption.md5_32(sign, "").toLowerCase());
		try {
			String ssss = ToolKit.requestJson("http://localhost:8080/callBack/callBack.do",
					JSONObject.fromObject(vmap).toString());
			System.out.println(ssss);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 递归迭代
	 * 
	 * @param parentList
	 * @param sublist
	 * @throws Exception
	 */
	private static List<Map<String, Object>> handleTee(List<Menu> list, String parentId) throws Exception {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < list.size() + 1; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Menu menu = list.get(i - 1);
			String thisId = parentId != "0" ? parentId + i : Integer.toString(menu.getId()); // 父级ID 1 11 12
			map.put("id", thisId);
			map.put("text", menu.getName());

			if (menu.getMenuList() != null && menu.getMenuList().size() > 0) {
				map.put("state", "closed");
				map.put("children", handleTee(menu.getMenuList(), thisId));
			}
			returnList.add(map);
		}
		return returnList;
	}

	/**
	 * 中博上分测试接口
	 */
	private static void zbpay() {
		String url = "http://enoab4pay.abjxnow.com/fourth_payment_platform/pay/addMoney";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("app_id", "1543495534070");
		map.put("account", "suibian08");
		map.put("money", "10");
		map.put("order_no", "ZFB" + getOrderIdByUUId());
		map.put("status", "0");
		map.put("type", "0");
		map.put("tradeType", "1");
		String p = getMapParam(map);
		p += "&sign=" + Encryption.sign(p + "&key=2609e6e3fa144cea9da2347375bfd952", "UTF-8").toLowerCase();
		p += "&remark=" + "测试";
		System.out.println(p);
		try {
			System.out.println(ToolKit.request(url, p));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 中博账号查询有效性。
	 */
	private static void zbquery() {
		String url = "http://enoab4pay.abjxnow.com/fourth_payment_platform/pay/checkAccount";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("app_id", "1543495534070");
		map.put("account", "suibian08");
		String p = getMapParam(map);
		p += "&sign=" + Encryption.sign(p + "&key=2609e6e3fa144cea9da2347375bfd952", "GB2312").toLowerCase();
		System.out.println(p);
		try {
			System.out.println(ToolKit.request(url, p));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getMapParam(TreeMap<String, String> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0, s.length() - 1);
	}

	public static String getMapParam(Map<String, String> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0, s.length() - 1);
	}

	/**
	 * 测试请求支付
	 */
	private static void testXINGFA(String key, String public_key) {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("price", "100");
		metaSignMap.put("istype", "ZFB");
		metaSignMap.put("userName", "mingz11");
		metaSignMap.put("orderid", getOrderIdByUUId());
		metaSignMap.put("uid", "194592037350313555");
		metaSignMap.put("callbackapiurl", "https://www.baidu.com/");
		metaSignMap.put("callbackviewurl", "https://www.baidu.com/");
		metaSignMap.put("time", Long.toString(new Date().getTime()));
		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		String sign = ToolKit.MD5(metaSignJsonStr + key, ToolKit.CHARSET);// 32位
		metaSignMap.put("sign", sign);

		try {
			byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(metaSignMap).getBytes(ToolKit.CHARSET),
					public_key);
			String param = new BASE64Encoder().encode(dataStr);
			String s = "data=" + URLEncoder.encode(param) + "&orderid=194592037350313555";
			System.out.println("http://localhost:8080/api/req.do?" + s);
			System.out.println("http://149.129.79.167:8080/api/req.do?" + s);
			System.out.println("http://149.129.94.177:8687/api/req.do?" + s);
			// ToolKit.request("http://localhost:8080/api/req.do", s);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getOrderIdByUUId() {
		int machineId = 1;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		return machineId + String.format("%015d", hashCodeV);
	}

	private int num = 100000;

	@RequestMapping(value = "/testApi")
	@ResponseBody
	public ReturnBan testApi(int customerid, int businessid, String price) {

		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);

		Calendar nowTime = Calendar.getInstance();

		nowTime.add(Calendar.MINUTE, -30);
		System.out.println(StringUtils.SDF.format(nowTime.getTime()));
		dc.add(Restrictions.ge("addTime", nowTime.getTime())); // 回调只能 回调半小时内的订单
		menuDao.findObj(dc);

		Customer c = customerDao.get(Customer.class, customerid);
		Business b = businessDao.get(Business.class, businessid);
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getServletPath();
		url = url.replace("/test/testApi.do", "");
		String number = getOrderIdByUUId();
		url = url + testXCS(c.getKey(), c.getPublic_key1(), c.getUid(), price, PayTran.getCode(b.getPayType()),
				b.getId(), number);
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("returnStr", redisModel.redisTemplate.opsForValue().get("returnStr"+number));
//		map.put("TESTparamStr", redisModel.redisTemplate.opsForValue().get("TESTparamStr"+number));
//		map.put("TESTsign", redisModel.redisTemplate.opsForValue().get("TESTsign"+number));
		return new ReturnBan("", true, url, number);
	}

	@RequestMapping(value = "/testysf")
	public String testysf(Model model, String orderNo) {
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);

		Calendar nowTime = Calendar.getInstance();

		nowTime.add(Calendar.MINUTE, -30);
		System.out.println(StringUtils.SDF.format(nowTime.getTime()));
		dc.add(Restrictions.ge("addTime", nowTime.getTime())); // 回调只能 回调半小时内的订单
		menuDao.findObj(dc);
		PayOrder p = new PayOrder();
		try {
			p.setCardNo("6212263602130808694");
			p.setOrderType("云闪付");
			payOrderService.BankCount(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("price", "10.00");
		model.addAttribute("orderNo", "0001211");
		model.addAttribute("png_base64", "https://hcpay.oss-cn-hongkong.aliyuncs.com/1559971737091.png");
		System.out.println(RedisKey.YSF_OSS.getCode() + orderNo);
		redisModel.redisTemplate.opsForValue().set(RedisKey.YSF_OSS.getCode() + orderNo,
				"https://hcpay.oss-cn-hongkong.aliyuncs.com/1559971737091.png", 1000 * 60 * 10, TimeUnit.MILLISECONDS);
		return "";
	}

	private String testXCS(String key, String public_key, String uid, String price, String istype, int id,
			String uuid) {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("price", price);
		metaSignMap.put("istype", istype);
		metaSignMap.put("userName", "test");
		metaSignMap.put("orderid", uuid);
		metaSignMap.put("uid", uid);
		metaSignMap.put("callbackapiurl", "https://www.baidu.com/");
		metaSignMap.put("callbackviewurl", "https://www.baidu.com/");
		metaSignMap.put("time", Long.toString(new Date().getTime()));
		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		String sign = ToolKit.MD5(metaSignJsonStr + key, ToolKit.CHARSET);// 32位
		metaSignMap.put("sign", sign);

		try {
			byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(metaSignMap).getBytes(ToolKit.CHARSET),
					public_key);
			String param = new BASE64Encoder().encode(dataStr);
			String s = "data=" + URLEncoder.encode(param) + "&orderid=" + uid + "&businessid=" + id + "&type=fc";

			System.out.println("http://localhost:8080/api/req.do?" + s);
			System.out.println("http://149.129.79.167:8080/api/req.do?" + s);
			System.out.println("https://47.56.47.216:8787/api/req.do?" + s);
			System.out.println("http://192.168.0.170:8080/api/req.do?" + s);
			return "/api/req.do?" + s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
