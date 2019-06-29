package com.cootf.wechat.util;

import com.cootf.wechat.support.BufferedImageLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 二维码工具类
 * @author : mengsj
 */
public class QRCodeUtil {

  private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

  private static final String CHARSET = "utf-8";
  private static final String FORMAT_NAME = "png";
  // 二维码尺寸
  private static final int QRCODE_SIZE = 300;
  // LOGO宽度
  private static final int WIDTH = 60;
  // LOGO高度
  private static final int HEIGHT = 60;

  private static final int WHITE = 0xFFFFFFFF;
  private static final int BLACK = 0xFF000000;


  /**
   * 生成二维码
   *
   * @param content 源内容
   * @param imgPath 生成二维码保存的路径
   * @param needCompress 是否要压缩
   * @return 返回二维码图片
   */
  private static BufferedImage createImage(String content, String imgPath, boolean needCompress)
      throws Exception {
    Hashtable hints = new Hashtable();
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
    hints.put(EncodeHintType.MARGIN, 1);
    BitMatrix bitMatrix = new MultiFormatWriter()
        .encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
            hints);
    BufferedImage image = toBufferedImage(bitMatrix);
    if (imgPath == null || "".equals(imgPath)) {
      return image;
    }
    // 插入图片
    QRCodeUtil.insertImage(image, imgPath, needCompress);
    return image;
  }

  /**
   * 生成二维码图片 不存储 直接以流的形式输出到页面
   * @param content 内容
   * @param response 响应对象
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void encodeQrcode(String content, HttpServletResponse response) {
    if (content == null || "".equals(content)) {
      return;
    }
    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
    Map hints = new HashMap();
    hints.put(EncodeHintType.CHARACTER_SET, CHARSET); //设置字符集编码类型
    BitMatrix bitMatrix = null;
    try {
      bitMatrix = multiFormatWriter
          .encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
      BufferedImage image = toBufferedImage(bitMatrix);
      //输出二维码图片流
      ImageIO.write(image, FORMAT_NAME, response.getOutputStream());
    } catch (Exception e1) {
      logger.error("二维码生成失败:{}", e1);
      e1.printStackTrace();
    }
  }

  private static BufferedImage toBufferedImage(BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
      }
    }
    return image;
  }

  /**
   * 在生成的二维码中插入图片
   */
  private static void insertImage(BufferedImage source, String imgPath, boolean needCompress)
      throws Exception {
    File file = new File(imgPath);
    if (!file.exists()) {
      System.err.println("" + imgPath + "   该文件不存在！");
      return;
    }
    Image src = ImageIO.read(new File(imgPath));
    int width = src.getWidth(null);
    int height = src.getHeight(null);
    if (needCompress) { // 压缩LOGO
      if (width > WIDTH) {
        width = WIDTH;
      }
      if (height > HEIGHT) {
        height = HEIGHT;
      }
      Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics g = tag.getGraphics();
      g.drawImage(image, 0, 0, null); // 绘制缩小后的图
      g.dispose();
      src = image;
    }
    // 插入LOGO
    Graphics2D graph = source.createGraphics();
    int x = (QRCODE_SIZE - width) / 2;
    int y = (QRCODE_SIZE - height) / 2;
    graph.drawImage(src, x, y, width, height, null);
    Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
    graph.setStroke(new BasicStroke(3f));
    graph.draw(shape);
    graph.dispose();
  }

  /**
   * 生成带logo二维码，并保存到磁盘
   * @param content 内容
   * @param imgPath 图片相对路径
   * @param destPath 目标路径
   * @param needCompress 是否压缩
   * @return 文件
   * @throws Exception 异常
   */
  public static File encode(String content, String imgPath, String destPath, boolean needCompress)
      throws Exception {
    BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
    mkdirs(destPath);
    String fileName = new Random().nextInt(99999999) + "." + FORMAT_NAME;//生成随机文件名
    File imgFile = new File(destPath + "/" + fileName);
    ImageIO.write(image, FORMAT_NAME, imgFile);
    return imgFile;
  }

  public static void mkdirs(String destPath) {
    File file = new File(destPath);
    // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir。(mkdir如果父目录不存在则会抛出异常)
    if (!file.exists() && !file.isDirectory()) {
      file.mkdirs();
    }
  }

  /**
   * 生成二维码并保存到磁盘目录
   * @param content 内容
   * @param imgPath 图片相对路径
   * @param destPath 目标路径
   * @return 文件
   * @throws Exception 异常
   */
  public static File encode(String content, String imgPath, String destPath) throws Exception {
    return QRCodeUtil.encode(content, imgPath, destPath, false);
  }

  /**
   * 生成二维码并保存到磁盘目录
   * @param content 内容
   * @param destPath 目标路径
   * @param needCompress 是否压缩
   * @return 文件
   * @throws Exception 异常
   */
  public static File encode(String content, String destPath, boolean needCompress)
      throws Exception {
    return QRCodeUtil.encode(content, null, destPath, needCompress);
  }

  /**
   * 生成二维码并保存到磁盘目录
   * @param content 内容
   * @param destPath 目标路径
   * @return 文件
   * @throws Exception 异常
   */
  public static File encode(String content, String destPath) throws Exception {
    return QRCodeUtil.encode(content, null, destPath, false);
  }

  public static void encode(String content, String imgPath, OutputStream output,
      boolean needCompress)
      throws Exception {
    BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
    ImageIO.write(image, FORMAT_NAME, output);
  }

  public static void encode(String content, OutputStream output) throws Exception {
    QRCodeUtil.encode(content, null, output, false);
  }


  /**
   * 从二维码中，解析数据
   *
   * @param file 二维码图片文件
   * @return 返回从二维码中解析到的数据值
   * @throws Exception 异常
   */
  public static String decode(File file) throws Exception {
    BufferedImage image;
    image = ImageIO.read(file);
    if (image == null) {
      return null;
    }
    BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    Result result;
    Hashtable hints = new Hashtable();
    hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
    result = new MultiFormatReader().decode(bitmap, hints);
    String resultStr = result.getText();
    return resultStr;
  }

  public static String decode(String path) throws Exception {
    return QRCodeUtil.decode(new File(path));
  }
}
