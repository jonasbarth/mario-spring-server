package main.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.VolatileImage;

public class ImagePreprocesser {

    private VolatileImage image;

    public ImagePreprocesser(VolatileImage image) {
        this.image = image;
    }

    public int[][] getRGBMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][] matrix = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                //System.out.println(bufferedImage.getRGB(j, i));
                int p = bufferedImage.getRGB(j, i);
                //get alpha
                int a = (p>>24) & 0xff;
                //get red
                int r = (p>>16) & 0xff;
                //get green
                int g = (p>>8) & 0xff;
                //get blue
                int b = p & 0xff;
                matrix[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        return matrix;
    }

    public int[][][] getGrayscaleMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        //byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][][] matrix = new int[3][bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                //System.out.println(bufferedImage.getRGB(j, i));
                int p = bufferedImage.getRGB(j, i);
                //get alpha
                int a = (p>>24) & 0xff;
                //get red
                int r = (p>>16) & 0xff;
                //get green
                int g = (p>>8) & 0xff;
                //get blue
                int b = p & 0xff;

                int gray = (r + g + b) / 3;
                matrix[0][i][j] = r;
                matrix[1][i][j] = g;
                matrix[2][i][j] = b;

            }
        }
        return matrix;
    }

}
