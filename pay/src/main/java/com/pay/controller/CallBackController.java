package com.pay.controller;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.handleBusiness.Asan;
import com.pay.handleBusiness.AuToMD5;
import com.pay.handleBusiness.BaiJia;
import com.pay.handleBusiness.BaiWei;
import com.pay.handleBusiness.ChangYiFu;
import com.pay.handleBusiness.DingDing;
import com.pay.handleBusiness.GDD;
import com.pay.handleBusiness.GaoSheng;
import com.pay.handleBusiness.GeZi;
import com.pay.handleBusiness.Gt;
import com.pay.handleBusiness.HaiXing;
import com.pay.handleBusiness.HePing;
import com.pay.handleBusiness.HengTong;
import com.pay.handleBusiness.HiPay;
import com.pay.handleBusiness.JiTang;
import com.pay.handleBusiness.JiTang2;
import com.pay.handleBusiness.JuYou;
import com.pay.handleBusiness.KS;
import com.pay.handleBusiness.Ksk;
import com.pay.handleBusiness.LiRui;
import com.pay.handleBusiness.LongFa;
import com.pay.handleBusiness.NiuNiu;
import com.pay.handleBusiness.One4Bank;
import com.pay.handleBusiness.Pay5566;
import com.pay.handleBusiness.QiTongBao;
import com.pay.handleBusiness.QuanQiu;
import com.pay.handleBusiness.Redbull;
import com.pay.handleBusiness.SanJiu;
import com.pay.handleBusiness.SanLiuWu;
import com.pay.handleBusiness.SanQiErYi;
import com.pay.handleBusiness.Shanrubao;
import com.pay.handleBusiness.SiHai;
import com.pay.handleBusiness.TianQiang;
import com.pay.handleBusiness.WanDing;
import com.pay.handleBusiness.WeiHuBao;
import com.pay.handleBusiness.XiaoMei;
import com.pay.handleBusiness.XinChuangShi;
import com.pay.handleBusiness.XinChuangShi2;
import com.pay.handleBusiness.XingFa;
import com.pay.handleBusiness.XingRuBao;
import com.pay.handleBusiness.YiLian;
import com.pay.handleBusiness.YingFuBao;
import com.pay.handleBusiness.YongXiong;
import com.pay.handleBusiness.YuFu;
import com.pay.handlePay.GameApi;
import com.pay.payAssist.ToolKit;

import net.sf.json.JSONArray;

/**
 * 第三方支付成功 回调 Controller
 * 
 * @author star
 * @version 创建时间：2019年3月29日下午2:46:58
 */
@Controller
@RequestMapping("/callBack")
public class CallBackController {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private Shanrubao shanrubao;
	@Autowired
	private XingFa xingFa;
	@Autowired
	private Ksk ksk;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private HengTong hengtong;
	@Autowired
	private TianQiang tianQiang;
	@Autowired
	private Asan asan;
	@Autowired
	private XiaoMei xiaoMei;
	@Autowired
	private HaiXing haixing;
	@Autowired
	private GDD koudai;
	@Autowired
	private LiRui lirui;
	@Autowired
	private KS ks;
	@Autowired
	private BaiWei baiWei;
	@Autowired
	private Gt gt;
	@Autowired
	private JiTang jiTang;
	@Autowired
	private DingDing dingding;
	@Autowired
	private SanJiu sanjiu;
	@Autowired
	private com.pay.handleBusiness.BaBao baBao;
	@Autowired
	private SanLiuWu sanLiuWu;
	@Autowired
	private JiTang2 jiTang2;
	@Autowired
	private QuanQiu quanQiu;
	@Autowired
	private WanDing wanDing;
	@Autowired
	private One4Bank one4Bank;
	@Autowired
	private XinChuangShi xinChuangShi;
	@Autowired
	private AuToMD5 auToMD5;
	@Autowired
	private YiLian yiLian;
	@Autowired
	private LongFa longFa;
	@Autowired
	private SanQiErYi sanQiErYi;
	@Autowired
	private GaoSheng gaoSheng;
	@Autowired
	private XingRuBao xingRuBao;
	@Autowired
	private YongXiong yongXiong;
	@Autowired
	private XinChuangShi2 xinChuangShi2;
	@Autowired
	private HePing hePing;
	@Autowired
	private WeiHuBao weiHuBao;
	@Autowired
	private BaiJia baijia;
	@Autowired
	private YingFuBao yingFuBao;
	@Autowired
	private JuYou juyou;
	@Autowired
	private HiPay hiPay;
	@Autowired
	private NiuNiu niuniu;
	@Autowired
	private ChangYiFu changyifu;
	@Autowired
	private YuFu yufu;
	@Autowired
	private GeZi gezi;
	@Autowired
	private SiHai sihai;
	@Autowired
	private QiTongBao qiTongBao;
	@Autowired
	private Redbull redbull;
	@Autowired
	private Pay5566 pay5566;

	/**
	 * 闪入包 回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/shangrubao")
	@ResponseBody
	public String shangrubaoCallback(Model model, HttpServletRequest request) {
		try {
			logger.info("闪入宝，开始回调");
			final Map<String, String> map = shanrubao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 红牛支付 回调函数
	 */
	@RequestMapping("/redbull")
	@ResponseBody
	public String redbull(Model model, HttpServletRequest request) {
		try {
			logger.info("红牛支付，开始回调");
			final Map<String, String> map = redbull.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 鑫发回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/xingfaCallback")
	@ResponseBody
	public String xingfaCallback(Model model, HttpServletRequest request) {
		try {
			logger.info("鑫发，开始回调");
			final Map<String, String> map = xingFa.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * KSK 回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/ksk")
	@ResponseBody
	public String ksk(Model model, HttpServletRequest request) {
		try {
			// gameApi.callBackGame(5);
			logger.info("KSK，开始回调");
			final Map<String, String> map = ksk.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * KSK 回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/hengtong")
	@ResponseBody
	public String hengtong(Model model, HttpServletRequest request) {
		try {
			logger.info("恒通，开始回调");
			final Map<String, String> map = hengtong.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 天强 回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/tiangqiang")
	@ResponseBody
	public String tiangqiang(Model model, HttpServletRequest request) {
		try {
			logger.info("天强，开始回调");
			final Map<String, String> map = tianQiang.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 阿三回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/asan")
	@ResponseBody
	public String asan(Model model, HttpServletRequest request) {
		try {
			logger.info("阿三，开始回调");
			final Map<String, String> map = asan.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 小美回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/xiaomei")
	@ResponseBody
	public String xiaomei(Model model, HttpServletRequest request) {
		try {
			logger.info("小美，开始回调");
			final Map<String, String> map = xiaoMei.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 海鑫回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/haixing")
	@ResponseBody
	public String haixing(Model model, HttpServletRequest request) {
		try {
			logger.info("海鑫，开始回调");
			final Map<String, String> map = haixing.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 黎睿支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/lirui")
	@ResponseBody
	public String lirui(Model model, HttpServletRequest request) {
		try {
			logger.info("黎睿支付，开始回调");
			final Map<String, String> map = lirui.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * KS支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/ks")
	@ResponseBody
	public String ks(Model model, HttpServletRequest request) {
		try {
			logger.info("KS支付，开始回调");
			final Map<String, String> map = ks.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 百威支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/baiwei")
	@ResponseBody
	public String baiwei(Model model, HttpServletRequest request) {
		try {
			logger.info("百威支付，开始回调");
			final Map<String, String> map = baiWei.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * GT支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/gt")
	@ResponseBody
	public String gt(Model model, HttpServletRequest request) {
		try {
			logger.info("GT支付，开始回调");
			final Map<String, String> map = gt.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 鸡汤支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/jitang")
	@ResponseBody
	public String jitang(Model model, HttpServletRequest request) {
		try {
			logger.info("鸡汤支付，开始回调");
			final Map<String, String> map = jiTang.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 钉钉支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/dingding")
	@ResponseBody
	public String dingding(Model model, HttpServletRequest request) {
		try {
			logger.info("钉钉支付，开始回调");
			final Map<String, String> map = dingding.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 三九支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/sanjiu")
	@ResponseBody
	public String sanjiu(Model model, HttpServletRequest request) {
		try {
			logger.info("三九支付，开始回调");
			final Map<String, String> map = sanjiu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 八宝支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/babao")
	@ResponseBody
	public String babao(Model model, HttpServletRequest request) {
		try {
			logger.info("八宝支付，开始回调");
			final Map<String, String> map = baBao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 三六五回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/sanliuwu")
	@ResponseBody
	public String sanliuwu(Model model, HttpServletRequest request) {
		try {
			logger.info("三六五支付，开始回调");
			final Map<String, String> map = sanLiuWu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 鸡汤2回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/jitang2")
	@ResponseBody
	public String jitang2(Model model, HttpServletRequest request) {
		try {
			logger.info("鸡汤2支付，开始回调");
			final Map<String, String> map = jiTang2.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 全球支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/quanqiu")
	@ResponseBody
	public String quanqiu(Model model, HttpServletRequest request) {
		try {
			logger.info("全球支付，开始回调");
			final Map<String, String> map = quanQiu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 万鼎支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/wanding")
	@ResponseBody
	public String wanding(Model model, HttpServletRequest request) {
		try {
			logger.info("万鼎支付，开始回调");
			final Map<String, String> map = wanDing.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * one4Bank支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/one4Bank")
	@ResponseBody
	public String one4Bank(Model model, HttpServletRequest request) {
		try {
			logger.info("one4Bank支付，开始回调");
			final Map<String, String> map = one4Bank.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 新創世支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/xinChuangShi")
	@ResponseBody
	public String xinChuangShi(Model model, HttpServletRequest request) {
		try {
			logger.info("新創世支付，开始回调");
			final Map<String, String> map = xinChuangShi.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 易联支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/yilian")
	@ResponseBody
	public String yilian(Model model, HttpServletRequest request) {
		try {
			logger.info("易联支付，开始回调");
			final Map<String, String> map = yiLian.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 和平支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/heping")
	@ResponseBody
	public String heping(Model model, HttpServletRequest request) {
		try {
			logger.info("和平支付，开始回调");
			final Map<String, String> map = hePing.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * auToMD5回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/callBack")
	@ResponseBody
	public String callBack(Model model, HttpServletRequest request) {
		try {
			logger.info("auToMD5 自动对接，开始回调");
			final Map<String, String> map = auToMD5.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * longFa回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/longFa")
	@ResponseBody
	public String longFa(Model model, HttpServletRequest request) {
		try {
			logger.info("longFa支付，开始回调");
			final Map<String, String> map = longFa.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * sanQiErYi回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/sanQiErYi")
	@ResponseBody
	public String sanQiErYi(Model model, HttpServletRequest request) {
		try {
			logger.info("longFa支付，开始回调");
			final Map<String, String> map = sanQiErYi.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 高盛回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/gaoSheng")
	@ResponseBody
	public String gaoSheng(Model model, HttpServletRequest request) {
		try {
			logger.info("高盛支付，开始回调");
			final Map<String, String> map = gaoSheng.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 星入宝回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/xingrubao")
	@ResponseBody
	public String xingrubao(Model model, HttpServletRequest request) {
		try {
			logger.info("星入宝，开始回调");
			final Map<String, String> map = xingRuBao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * hiPay回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/hiPay")
	@ResponseBody
	public String hiPay(Model model, HttpServletRequest request) {
		try {
			logger.info("hiPay，开始回调");
			final Map<String, String> map = hiPay.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * hiPay回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/sihaiPay")
	@ResponseBody
	public String sihaiPay(Model model, HttpServletRequest request) {
		try {
			logger.info("sihaiPay，开始回调");
			final Map<String, String> map = sihai.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 永兴回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/yongxing")
	@ResponseBody
	public String yongxing(Model model, HttpServletRequest request) {
		try {
			logger.info("永兴，开始回调");
			final Map<String, String> map = yongXiong.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 新创世2回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/xinchuangshi2")
	@ResponseBody
	public String xinchuangshi2(Model model, HttpServletRequest request) {
		try {
			logger.info("新创世2，开始回调");
			final Map<String, String> map = xinChuangShi2.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 维护宝回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/weihubao")
	@ResponseBody
	public String weihubao(Model model, HttpServletRequest request) {
		try {
			logger.info("维护宝，开始回调");
			final Map<String, String> map = weiHuBao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 百佳回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/baijia")
	@ResponseBody
	public String baijia(Model model, HttpServletRequest request) {
		try {
			logger.info("百佳，开始回调");
			final Map<String, String> map = baijia.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 盈付宝回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/yingFuBao")
	@ResponseBody
	public String yingFuBao(Model model, HttpServletRequest request) {
		try {
			logger.info("盈付宝，开始回调");
			final Map<String, String> map = yingFuBao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 口袋回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/koudai")
	@ResponseBody
	public String koudai(Model model, HttpServletRequest request) {
		try {
			logger.info("口袋，开始回调");
			final Map<String, String> map = koudai.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 聚友回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/juyou")
	@ResponseBody
	public String juyou(Model model, HttpServletRequest request) {
		try {
			logger.info("聚友，开始回调");
			final Map<String, String> map = juyou.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 牛牛回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/niuniu")
	@ResponseBody
	public String niuniu(Model model, HttpServletRequest request) {
		try {
			logger.info("牛牛，开始回调");
			final Map<String, String> map = niuniu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 畅壹付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/changyifu")
	@ResponseBody
	public String changyifu(Model model, HttpServletRequest request) {
		try {
			logger.info("畅壹，开始回调");
			final Map<String, String> map = changyifu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 御付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/yufu")
	@ResponseBody
	public String yufu(Model model, HttpServletRequest request) {
		try {
			logger.info("御付，开始回调");
			final Map<String, String> map = yufu.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 启通宝支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/qiTongBao")
	@ResponseBody
	public String qiTongBao(Model model, HttpServletRequest request) {
		try {
			logger.info("启通宝支付，开始回调:");
			final Map<String, String> map = qiTongBao.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 格子支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/gezi")
	@ResponseBody
	public String gezi(Model model, HttpServletRequest request) {
		try {
			logger.info("格子支付，开始回调:");
			final Map<String, String> map = gezi.payCallback(request);
			callBackGame(map);
			return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * 5566支付回调函数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/pay5566")
	@ResponseBody
	public void handleCallback(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("5566支付，开始回调！！！！");
			Map<String, String> map = pay5566.payCallback(request);
			callBackGame(map);
			if (!map.get("state").equals("SUCCESS")) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
			// return map.get("reBusiness");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}
		// return "";
	}

	/*
	 * @RequestMapping("/pay55661")
	 * 
	 * @ResponseBody public void handleCallback(@RequestBody CallbackVO callbackVO,
	 * HttpServletResponse response,HttpServletRequest request) { try {
	 * logger.info("5566支付，开始回调:"); Map<String,String> map =
	 * pay5566.payCallback(callbackVO,request); callBackGame(map);
	 * response.setStatus(HttpStatus.OK.value()); } catch (Exception e) {
	 * e.printStackTrace(); logger.info(e.getMessage());
	 * response.setStatus(HttpStatus.BAD_REQUEST.value()); }
	 * 
	 * }
	 */

	public static void main(String[] args) {
		SortedMap<String, Object> p = new TreeMap<>();
		p.put("accessToken", "a");
		SortedMap<String, Object> paramMap = new TreeMap<>();
		paramMap.put("outTradeNo", "a");
		paramMap.put("money", "a");
		paramMap.put("type", "a");
		paramMap.put("body", "a");
		paramMap.put("detail", "a");
		paramMap.put("notifyUrl", "a");
		paramMap.put("successUrl", "a");
		paramMap.put("callbackSuccessUrl", "http://szceft.natappfree.cc/gopay/callback");
		paramMap.put("productId", "a");
		p.put("param", paramMap);
		String a = JSONArray.fromObject(p).toString();
		try {
			String s = ToolKit.requestJson("http://localhost:8080/callBack/xiaomei.do", a.substring(1, a.length() - 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 开线程调游戏方api
	 * 
	 * @param map
	 */
	private void callBackGame(final Map<String, String> map) {
		if (map.get("state").equals("SUCCESS")) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						logger.info("第一次开始回调 游戏方：" + map.get("payorder_id"));
						gameApi.callBackGame(Integer.parseInt(map.get("payorder_id")), true, true);
						logger.info("结束第一次回调游戏方：" + map.get("payorder_id"));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

}
