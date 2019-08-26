package com.pay.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import com.pay.model.ResponseResult;

/**
 * 照片上传处理
 * 
 * @author acer
 */
@Controller
public class UploaderController extends BaseController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@ResponseBody
	@RequestMapping("/uploadImg")
	public void uploadPicture(@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		ResponseResult result = new ResponseResult();

		Map<String, Object> map = new HashMap<String, Object>();
		File targetFile = null;
		String url = "";// 返回存储路径
		int code = 1;
		System.out.println(file);
		String fileName = file.getOriginalFilename();// 获取文件名加后缀
		if (fileName != null && fileName != "") {
			//本地上传地址
			//String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					//+ request.getContextPath() + "/upload/imgs/";// 存储路径
			//服务器上传地址
			String returnUrl = "http://149.129.79.167:8070/upload/imgs/";
					
			String path = request.getSession().getServletContext().getRealPath("upload/imgs"); //文件存储位置
			String fileF = fileName.substring(fileName.lastIndexOf("."), fileName.length());// 文件后缀
			fileName = new Date().getTime() + "_" + new Random().nextInt(1000) + fileF;// 新的文件名

			// 先判断文件是否存在
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileAdd = sdf.format(new Date());
			// 获取文件夹路径
			File file1 = new File(path + "/" + fileAdd);
			// 如果文件夹不存在则创建
			if (!file1.exists() && !file1.isDirectory()) {
				file1.mkdir();
			}
			// 将图片存入文件夹
			targetFile = new File(file1, fileName);
			try {
				// 将上传的文件写到服务器上指定的文件。
				file.transferTo(targetFile);
				url = returnUrl + fileAdd + "/" + fileName;
				code = 0;
				result.setCode(code);
				result.setMessage("图片上传成功");
				map.put("url", url);
				result.setResult(map);
			} catch (Exception e) {
				e.printStackTrace();
				result.setMessage("系统异常，图片上传失败");
			}
		}

		writeJson(response, result);

	}

	/**
	 * 输出JSON数据
	 * 
	 * @param response
	 * @param jsonStr
	 */
	public void writeJson(HttpServletResponse response, String jsonStr) {
		response.setContentType("text/json;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(jsonStr);
			pw.flush();
		} catch (Exception e) {
			logger.info("输出JSON数据异常", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * 向页面响应json字符数组串流.
	 * 
	 * @param response
	 * @param jsonStr
	 * @throws IOException
	 * @return void
	 */
	public void writeJsonStr(HttpServletResponse response, String jsonStr) throws IOException {
		OutputStream outStream = null;
		try {
			response.reset();
			response.setCharacterEncoding("UTF-8");
			outStream = response.getOutputStream();
			outStream.write(jsonStr.getBytes("UTF-8"));
			outStream.flush();
		} catch (IOException e) {
			logger.info("输出JSON数据异常(writeJsonStr)", e);
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
	}

	public void writeJsonStr(HttpServletResponse response, InputStream in) throws IOException {
		if (null == in) {
			return;
		}
		OutputStream outStream = null;
		try {
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			outStream = response.getOutputStream();
			int len = 0;
			byte[] byt = new byte[1024];
			while ((len = in.read(byt)) != -1) {
				outStream.write(byt, 0, len);

			}
			outStream.flush();
		} catch (IOException e) {
			logger.info("输出JSON数据异常(writeJsonStr)", e);
		} finally {
			if (outStream != null) {
				outStream.close();
				in.close();
			}
		}
	}

	/**
	 * 输出JSON数据
	 * 
	 * @param response
	 * @param jsonStr
	 */
	public void writeJson(HttpServletResponse response, Object obj) {
		response.setContentType("text/json;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter pw = null;
		Gson gson = new Gson();
		try {
			pw = response.getWriter();
			pw.write(gson.toJson(obj));
			pw.flush();
		} catch (Exception e) {
			logger.info("输出JSON数据异常", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void writeHtml(HttpServletResponse response, String html) {
		response.setContentType("text/html;;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(html);
			pw.flush();
		} catch (Exception e) {
			logger.info("输出HTML数据异常", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

}
