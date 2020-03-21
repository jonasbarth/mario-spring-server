package util;

import javafx.scene.transform.Scale;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.VolatileImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImagePreprocesser {

    private VolatileImage image;
    private int scaledWidth;
    private int scaledHeight;

    public ImagePreprocesser(VolatileImage image, int scaledWidth, int scaledHeight) {
        this.image = image;
        this.scaledHeight = scaledHeight;
        this.scaledWidth = scaledWidth;
    }

    public int[][][] getRGBMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        BufferedImage cropped = cropImage(bufferedImage, 0, 20, 256, 220);
        BufferedImage finalImage = bilinear(cropped, this.scaledWidth, this.scaledHeight);
        //byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][][] matrix = new int[3][finalImage.getHeight()][finalImage.getWidth()];

        for (int i = 0; i < finalImage.getHeight(); i++) {
            for (int j = 0; j < finalImage.getWidth(); j++) {
                //System.out.println(bufferedImage.getRGB(j, i));
                int p = finalImage.getRGB(j, i);
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

    public int[][][] getGrayscaleMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        BufferedImage cropped = cropImage(bufferedImage, 0, 20, 256, 220);
        BufferedImage finalImage = bilinear(cropped, this.scaledWidth, this.scaledHeight);
        //byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][][] matrix = new int[3][finalImage.getHeight()][finalImage.getWidth()];

        for (int i = 0; i < finalImage.getHeight(); i++) {
            for (int j = 0; j < finalImage.getWidth(); j++) {
                //System.out.println(bufferedImage.getRGB(j, i));
                int p = finalImage.getRGB(j, i);
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

    public byte[] getBytes() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();


        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            return bytes;
        }
        catch (IOException e) {
            return new byte[0];
        }

    }

    /**
     * Crops an image to the specified region
     * @param bufferedImage the image that will be crop
     * @param x the upper left x coordinate that this region will start
     * @param y the upper left y coordinate that this region will start
     * @param width the width of the region that will be crop
     * @param height the height of the region that will be crop
     * @return the image that was cropped.
     */
    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height){
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }

    public static BufferedImage bilinear(BufferedImage bufferedImage, int width, int height) {
        BufferedImage image = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, width, height);
        return image;
        /*
        BufferedImage scaledImage = new BufferedImage(width, height, bufferedImage.getType());
        final AffineTransform at = AffineTransform.getScaleInstance(bufferedImage.getWidth() / width, bufferedImage.getHeight() / height);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledImage = ato.filter(bufferedImage, scaledImage);
        return scaledImage;*/
    }

    public static BufferedImage bicubic(BufferedImage bufferedImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, bufferedImage.getType());
        final AffineTransform at = AffineTransform.getScaleInstance(bufferedImage.getWidth() / width, bufferedImage.getHeight() / height);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(bufferedImage, scaledImage);
        return scaledImage;
    }

    public static BufferedImage nearestNeighbour(BufferedImage bufferedImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, bufferedImage.getType());
        final AffineTransform at = AffineTransform.getScaleInstance(bufferedImage.getWidth() / width, bufferedImage.getHeight() / height);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        scaledImage = ato.filter(bufferedImage, scaledImage);
        return scaledImage;
    }

}
