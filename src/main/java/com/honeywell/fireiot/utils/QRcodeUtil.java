package com.honeywell.fireiot.utils;


import com.swetake.util.Qrcode;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRcodeUtil {

    /**
     * 绘制二维码image
     * @param version
     * @param data
     * @return
     * @throws IOException
     */
    public static BufferedImage getQRCode(Integer version, String data) throws IOException {
        Qrcode qrcode = new Qrcode();
        //纠错等级（分为L、M、H三个等级）L(7%)、M(15%)、Q(25%)、H(30%)
        qrcode.setQrcodeErrorCorrect('L');
        //N代表数字，A代表a-Z，B代表其它字符
        qrcode.setQrcodeEncodeMode('B');
        //版本 大小
        qrcode.setQrcodeVersion(version);
        //设置一下二维码的像素
        int width = 67 + 12 * (version - 1);
        int height = 67 + 12 * (version - 1);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //绘图
        Graphics2D gs = image.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.setColor(Color.BLACK);

        //清除下画板内容
        gs.clearRect(0, 0, width, height);
        //设置下偏移量,如果不加偏移量，有时会导致出错。
        int pixoff = 2;
        //生成二维码中要存储的信息
        byte[] d = data.getBytes("gb2312");
        if (d.length > 0 && d.length < 120) {
            boolean[][] s = qrcode.calQrcode(d);
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s.length; j++) {
                    if (s[j][i]) {
                        gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                    }
                }
            }
        }
        gs.dispose();
        image.flush();
        return image;
    }

    /**
     * 二维码下新增文字
     * @param data
     * @param note
     * @throws IOException
     */
    public static BufferedImage getQRcodeWithNote(String data, String note) throws IOException {
        BufferedImage image = getQRCode(2, data);
        Image src = image.getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        BufferedImage tag;
        tag = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        //设置低栏白边
        Graphics g1 = tag.getGraphics();
        //设置文字
        Graphics2D g2 = tag.createGraphics();
        g2.setColor(Color.BLACK);
        g2.setBackground(Color.WHITE);
        //必须清除，不然背景为黑色
        g2.clearRect(0, 0, 400, 400);
        Font font = new Font("微软雅黑", Font.BOLD, 20);
        g2.setFont(font);
        g1.fillRect(50, 300, 300, 50);
        g1.setColor(Color.WHITE);
        String[] split = note.split(":");
        for (int i = 0; i < split.length; i++) {
            g2.drawString(split[i], 55, 350 + i * 20);
        }
        g1.drawImage(src, 50, 25, null);
        g1.dispose();
        g2.dispose();
        tag.flush();
        return tag;
        // 写入数据库
//        ImageIO.write(tag, "png", new File("C:/Users/H300238/qrcode.png"));

    }

    /**
     * 生成二维码转为BASE64
     * @param image
     * @return
     * @throws IOException
     */
    public static String toBase64String(BufferedImage image) throws IOException {
        String format = "png";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image,format,os);
        byte b[] = os.toByteArray();
        String str = new BASE64Encoder().encode(b);
        return str;
    }
}