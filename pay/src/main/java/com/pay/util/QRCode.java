package com.pay.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import Decoder.BASE64Encoder;

/**
 * @author star
 * @version 创建时间：2019年4月3日下午2:26:10
 */
public class QRCode {
	private static final String CHARSET = "utf-8";

	/**
	 * 二维码生成器
	 * 
	 * @param text 二维码内容
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	public static String generalQRCode(String url) {
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1); // 先设置margin为1
		String binary = null;

		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedImage image = toBufferedImage(bitMatrix);
			////
			ImageIO.write(image, "png", out);
			byte[] bytes = out.toByteArray();

			// 2、将字节数组转为二进制
			BASE64Encoder encoder = new BASE64Encoder();
			binary = encoder.encodeBuffer(bytes).trim();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return binary;
	}

	public static String decode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	/**
	 * 二维码生成器
	 * 
	 * @param text 二维码内容
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	public static BufferedImage generalQRCode(String url, int x, int y) {
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1); // 先设置margin为1
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, x, y, hints);
			BufferedImage image = toBufferedImage(bitMatrix);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		return image;
	}

	public static void main(String[] args) {
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		String binary = null;

		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode("aaaa", BarcodeFormat.QR_CODE, 200, 200, hints);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedImage image = toBufferedImage(bitMatrix);

			////
			ImageIO.write(image, "png", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
