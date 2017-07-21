/**
 * 
 */
package cn.himma.util.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.swetake.util.Qrcode;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年7月28日
 * @ClassName QRCodeUtil
 */
public class QRCodeUtil {

    /**
     * 生成二维码(QRCode)图片
     * 
     * @param content 二维码图片的内容
     * @param imgPath 生成二维码图片完整的路径
     * @param logoPath 二维码图片中间的logo路径
     */
    public static int createQRCode(String content, String imgPath, String logoPath, int width, int height, int logoWidth, int logoHeight) {
        try {
            Qrcode qrcodeHandler = new Qrcode();
            byte[] contentBytes = content.getBytes("UTF-8");
            int length = contentBytes.length;
            // L(7%)-154、M(15%)-122、Q(25%)-86、H(30%)-64
            if (length < 0 || length > 154) {
                System.err.println("QRCode content bytes length = " + length + ", not in [ 0,154 ]. ");
                return -1;
            }
            char ecc = calculateQrcodeErrorCorrect(length);
            qrcodeHandler.setQrcodeErrorCorrect(ecc);
            qrcodeHandler.setQrcodeEncodeMode('B');
            qrcodeHandler.setQrcodeVersion(7);
            // 构造一个BufferedImage对象 设置宽、高
            BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gs = bufImg.createGraphics();
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, width, height);
            // 设定图像颜色 > BLACK
            gs.setColor(Color.BLACK);
            // 设置偏移量 不设置可能导致解析出错
            // 输出内容 > 二维码
            boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
            // 计算单元格大小和边框空白大小
            int gridWidth = width / codeOut.length;
            int gridheight = height / codeOut[0].length;
            int widthOffset = width - gridWidth * codeOut.length;
            int heightOffset = height - gridheight * codeOut[0].length;

            for (int i = 0; i < codeOut.length; i++) {
                for (int j = 0; j < codeOut[i].length; j++) {
                    if (codeOut[i][j]) {
                        gs.fillRect(i * gridWidth + widthOffset / 2, j * gridheight + heightOffset / 2, gridWidth, gridheight);
                    }
                }
            }
            if (logoPath != null && logoPath.trim() != "") {
                Image img = ImageIO.read(new File(logoPath));// 实例化一个Image对象。
                gs.drawImage(img, (width - logoWidth) / 2, (height - logoHeight) / 2, logoWidth, logoHeight, null);
            }
            gs.dispose();
            bufImg.flush();
            // 生成二维码QRCode图片
            File imgFile = new File(imgPath);
            int index = imgPath.lastIndexOf(".");
            String formatName = imgPath.substring(index + 1);
            ImageIO.write(bufImg, formatName, imgFile);
        } catch (Exception e) {
            e.printStackTrace();
            return -100;
        }
        return 0;
    }

    /**
     * @param length
     * @return
     */
    private static char calculateQrcodeErrorCorrect(int length) {
        // L(7%)-154、M(15%)-122、Q(25%)-86、H(30%)-64
        char result = 'L';
        if (length <= 64) {
            result = 'H';
        } else if (length <= 86) {
            result = 'Q';
        } else if (length <= 122) {
            result = 'M';
        }
        System.out.println(length + "," + result);
        return result;
    }

    public static void main(String[] args) {
        String imgPath = "D:\\项目读写文件\\二维码.jpg";
        String logoPath = "D:\\项目读写文件\\头像.png";
        String content = "http://jingyan.baidu.com/article/86fae346c2d3173c49121acb.html";
        createQRCode(content, imgPath, logoPath, 200, 200, 50, 50);
    }
}
