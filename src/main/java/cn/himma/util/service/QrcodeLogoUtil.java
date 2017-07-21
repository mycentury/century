package cn.himma.util.service;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import lombok.Data;

import org.springframework.util.StringUtils;

import cn.himma.util.file.FileNameUtil;

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

public class QrcodeLogoUtil {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String DEFAULT_QRCODE_FORMAT = "JPG";
    // 二维码宽度
    private static final int DEFAULT_QRCODE_WIDTH = 280;
    // 二维码高度
    private static final int DEFAULT_QRCODE_HEIGHT = 280;
    // LOGO宽度
    private static final int DEFAULT_LOGO_WIDTH = 60;
    // LOGO高度
    private static final int DEFAULT_LOGO_HEIGHT = 60;

    @Data
    public static class QrcodeInfo {
        private String qrcodeContent;
        private String qrcodePath;
        private boolean needCompress;
        private String qrcodeName;
        private String logoPath;
        private String qrcodeFormat;
        private int qrcodeWidth;
        private int qrcodeHeight;
        private int logoWidth;
        private int logoHeight;

        public QrcodeInfo(String qrcodeContent, String qrcodePath, boolean needCompress) {
            this.qrcodeContent = qrcodeContent;
            this.qrcodePath = qrcodePath;
            this.needCompress = needCompress;
            setUnnecessaryParams();
        }

        /**
         * @param qrcodeContent
         * @param qrcodePath
         * @param qrcodeName
         * @param needCompress
         * @param qrcodeFormat
         * @param qrcodeWidth
         * @param qrcodeHeight
         * @param logoWidth
         * @param logoHeight
         */
        private void setUnnecessaryParams() {
            this.qrcodeName = FileNameUtil.generateRandomFileName(10);
            this.qrcodeFormat = DEFAULT_QRCODE_FORMAT;
            this.qrcodeWidth = DEFAULT_QRCODE_WIDTH;
            this.qrcodeHeight = DEFAULT_QRCODE_HEIGHT;
            this.logoWidth = DEFAULT_LOGO_WIDTH;
            this.logoHeight = DEFAULT_LOGO_HEIGHT;
        }
    }

    private static BufferedImage createImage(QrcodeInfo qrcodeInfo) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(qrcodeInfo.qrcodeContent, BarcodeFormat.QR_CODE, qrcodeInfo.qrcodeWidth,
                qrcodeInfo.qrcodeHeight, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        // 插入图片
        if (StringUtils.hasText(qrcodeInfo.logoPath)) {
            insertLogoImage(image, qrcodeInfo);
        }
        return image;
    }

    /**
     * 插入LOGO
     * 
     * @param source 二维码图片
     * @param imgPath LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertLogoImage(BufferedImage source, QrcodeInfo qrcodeInfo) throws Exception {
        File file = new File(qrcodeInfo.logoPath);
        if (!file.exists()) {
            System.err.println("" + qrcodeInfo.logoPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(qrcodeInfo.logoPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (qrcodeInfo.needCompress) { // 压缩LOGO
            if (width > qrcodeInfo.logoWidth) {
                width = qrcodeInfo.logoWidth;
            }
            if (height > qrcodeInfo.logoHeight) {
                height = qrcodeInfo.logoHeight;
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
        int x = (qrcodeInfo.qrcodeWidth - width) / 2;
        int y = (qrcodeInfo.qrcodeHeight - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        int arcw = 6;// 原值为6
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, arcw, arcw);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成二维码(内嵌LOGO)
     * 
     * @param qrcodeInfo 内容
     * @return String 二维码全路径
     */
    public static String generateQrcode(QrcodeInfo qrcodeInfo) {
        try {
            String fullPath = qrcodeInfo.qrcodePath + File.separator + qrcodeInfo.qrcodeName + "." + qrcodeInfo.qrcodeFormat;
            mkdirs(fullPath);
            BufferedImage image = createImage(qrcodeInfo);
            ImageIO.write(image, qrcodeInfo.qrcodeFormat, new File(fullPath));
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     * 
     * @author weqinjia.liu Email: mmm333zzz520@163.com
     * @date 2013-12-11 上午10:16:36
     * @param destPath 存放目录
     */
    private static void mkdirs(String destPath) {
        File file = new File(destPath);
        // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码(内嵌LOGO)
     * 
     * @param content 内容
     * @param imgPath LOGO地址
     * @param output 输出流
     * @param needCompress 是否压缩LOGO
     */
    public static boolean generateQrcodeStream(QrcodeInfo qrcodeInfo, OutputStream output) {
        try {
            BufferedImage image = createImage(qrcodeInfo);
            ImageIO.write(image, qrcodeInfo.qrcodeFormat, output);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解析二维码
     * 
     * @param file 二维码图片
     * @return
     * @throws Exception
     */
    public static String parseQrcode(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                return null;
            }
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
            Result result = new MultiFormatReader().decode(bitmap, hints);
            String resultStr = result.getText();
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析二维码
     * 
     * @param path 二维码图片地址
     * @return
     * @throws Exception
     */
    public static String parseQrcode(String path) {
        return parseQrcode(new File(path));
    }

    private static ErrorCorrectionLevel calculateQrcodeErrorCorrect(int length) {
        // L(7%)-154、M(15%)-122、Q(25%)-86、H(30%)-64
        ErrorCorrectionLevel result = ErrorCorrectionLevel.L;
        if (length <= 58) {
            result = ErrorCorrectionLevel.H;
        } else if (length <= 80) {
            result = ErrorCorrectionLevel.Q;
        } else if (length <= 116) {
            result = ErrorCorrectionLevel.M;
        }
        System.out.println(length + "," + result);
        return result;
    }

    public static void main(String[] args) {
        String qrcodeContent = "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890" + "1234567890"
                + "1234567890";
        String qrcodePath = "D:\\项目读写文件\\Qrcode";
        String logoPath = "D:\\项目读写文件\\头像.png";
        QrcodeInfo qrcodeInfo = new QrcodeInfo(qrcodeContent, qrcodePath, true);
        qrcodeInfo.setLogoPath(logoPath);
        String generateQrcode = generateQrcode(qrcodeInfo);
        System.out.println(generateQrcode);
        File file = new File(generateQrcode);
        String parseQrcode = parseQrcode(file);
        System.out.println(parseQrcode);
    }
}
