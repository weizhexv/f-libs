package com.jkqj.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片工具类
 *
 * @author cb
 * @date 2022-09-27
 */
@Slf4j
public final class PictureUtils {

    // 识别颜色度数
    private static int COLOR_RANGE = 100;

    public static int[] cutoutCoordinate(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 最小
        int maxX = 0, maxY = 0;
        // 最大
        int minX = image.getWidth(), minY = image.getHeight();

        for (int y = image.getMinY(); y < image.getHeight(); y++) {
            // 内层遍历是X轴的像素
            for (int x = image.getMinX(); x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                // 对当前颜色判断是否在指定区间内
                if (!colorInRange(rgb)) {
                    minX = minX > x ? x : minX;
                    minY = minY > y ? y : minY;
                    maxX = maxX < x ? x : maxX;
                    maxY = maxY < y ? y : maxY;
                }
            }
        }

        return new int[]{minX, minY, maxX, maxY};
    }


    public static byte[] cutout(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            cutout(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

    private static void cutout(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);

        // 图片透明度
        int alpha;
        // 最小
        int maxX = 0, maxY = 0;
        // 最大
        int minX = image.getWidth(), minY = image.getHeight();

        for (int y = image.getMinY(); y < image.getHeight(); y++) {
            // 内层遍历是X轴的像素
            for (int x = image.getMinX(); x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                // 对当前颜色判断是否在指定区间内
                if (!colorInRange(rgb)) {
                    minX = minX > x ? x : minX;
                    minY = minY > y ? y : minY;
                    maxX = maxX < x ? x : maxX;
                    maxY = maxY < y ? y : maxY;
                }
            }
        }

        BufferedImage bufferedImage = new BufferedImage(maxX - minX, maxY - minY, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
            // 内层遍历是X轴的像素
            for (int y = bufferedImage.getMinX(); y < bufferedImage.getHeight(); y++) {
                int rgb = image.getRGB(x + minX, y + minY);

                if (!colorInRange(rgb)) {
                    // 设置为不透明
                    alpha = 255;
                    // #AARRGGBB 最前两位为透明度
                    rgb = (alpha << 24) | (0x000000);//黑色构图
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }

        // 生成图片为PNG
        ImageIO.write(bufferedImage, "png", outputStream);

        // 输出图片坐标
        log.info("minX: {}, minY: {}, maxX: {}, maxY: {}", minX, minY, maxX, maxY);
    }

    /**
     * 判断是背景还是内容
     *
     * @param color
     * @return
     */
    private static boolean colorInRange(int color) {
        // 获取color(RGB)中R位
        int red = (color & 0xff0000) >> 16;
        // 获取color(RGB)中G位
        int green = (color & 0x00ff00) >> 8;
        // 获取color(RGB)中B位
        int blue = (color & 0x0000ff);

        // 通过RGB三分量来判断当前颜色是否在指定的颜色区间内
        return red >= COLOR_RANGE && green >= COLOR_RANGE && blue >= COLOR_RANGE;
    }

}