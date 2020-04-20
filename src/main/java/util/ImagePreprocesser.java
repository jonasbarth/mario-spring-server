package util;


import engine.core.MarioWorld;
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
import java.io.File;
import java.io.IOException;

public class ImagePreprocesser {

    private VolatileImage image;
    private int scaledWidth;
    private int scaledHeight;
    private MarioWorld marioWorld;
    private final int egoOffset = 50;

    public ImagePreprocesser(VolatileImage image, int scaledWidth, int scaledHeight, MarioWorld marioWorld) {
        this.image = image;
        this.scaledHeight = scaledHeight;
        this.scaledWidth = scaledWidth;
        this.marioWorld = marioWorld;
    }

    public int[][][] getRGBMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();

        this.saveImage("original.png", bufferedImage);

        BufferedImage cropped = cropImage(bufferedImage, 0, 20, 256, 220);
        this.saveImage("cropped.png", cropped);
        BufferedImage finalImage = bilinear(cropped, this.scaledWidth, this.scaledHeight);

        this.saveImage("resized.png", finalImage);

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
        int[][][] matrix = new int[1][finalImage.getHeight()][finalImage.getWidth()];

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
                /*matrix[0][i][j] = r;
                #matrix[1][i][j] = g;
                #matrix[2][i][j] = b; */

                matrix[0][i][j] = gray;


            }
        }
        return matrix;
    }

    public int[][][] getEgoGrayscaleMatrix() {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        //BufferedImage cropped = cropImage(bufferedImage, 0, 20, 256, 220);
        //BufferedImage finalImage = bilinear(cropped, this.scaledWidth, this.scaledHeight);
        BufferedImage finalImage = getEgocentric(bufferedImage, this.marioWorld.mario.x, this.marioWorld.mario.y);
        //byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][][] matrix = new int[1][finalImage.getHeight()][finalImage.getWidth()];

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
                /*matrix[0][i][j] = r;
                #matrix[1][i][j] = g;
                #matrix[2][i][j] = b; */

                matrix[0][i][j] = gray;


            }
        }
        return matrix;
    }

    public int[][][] getEgoRGBMatrix(float marioX, float marioY) {
        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(this.image, 0, 0, null);
        bGr.dispose();
        //BufferedImage cropped = cropImage(bufferedImage, 0, 20, 256, 220);
        //BufferedImage finalImage = bilinear(cropped, this.scaledWidth, this.scaledHeight);
        BufferedImage egoImage = getEgocentric(bufferedImage, marioX, marioY);

        BufferedImage finalImage = bilinear(egoImage, this.scaledWidth, this.scaledHeight);
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

    public BufferedImage getEgocentric(BufferedImage image, float marioX, float marioY) {
        float offset = 50.0f;
        float x1 = marioX - offset - this.marioWorld.cameraX;
        float x2 = offset * 2;
        float y1 = marioY - offset - this.marioWorld.cameraY;
        float y2 = offset * 2;


        /*Mario is too far left to the screen so we need to have more pixels on the right*/
        if (marioX - offset < 0.0f) {
            //System.out.println("Mario is too far left on the screen");
            x1 = 0.0f;
            x2 = marioX + offset + Math.abs(marioX - offset);
        }
        /*Mario is too far right on the screen so we need more pixels on the left*/
        else if (marioX + offset - this.marioWorld.cameraX > image.getWidth()) {
            //System.out.println("Mario is too far right on the screen");
            x1 = marioX - this.marioWorld.cameraX - offset - (offset - (image.getWidth() - marioX));
            x2 = image.getWidth() - x1;
        }
        /*Mario is too far up on the screen so we need more pixels below*/
        if (marioY - offset < 0.0f) {
            //System.out.println("Mario is too far up on the screen");
            y1 = 0.0f;
            y2 = marioY + offset + Math.abs(marioY - offset);
        }
        /*Mario is too far down on the screen so need more pixels above*/
        else if (marioY + offset - this.marioWorld.cameraY > image.getHeight()) {
            //System.out.println("Mario is too far down on the screen.");
            y1 = marioY - this.marioWorld.cameraY - offset - (offset - (image.getHeight() - marioY));
            y2 = image.getHeight() - y1;
        }


        BufferedImage cropped = cropImage(image, (int) x1, (int) y1, (int) x2, (int) y2);
        //System.out.printf("Mario is at %f, %f\n", marioX, marioY);
        //System.out.printf("x1 = %f, x2 = %f, y1 = %f, y2 = %f\tDims = %d x % d\tcameraX = %f, cameraY = %f\txa = %f, ya = %f\n", x1, x2, y1, y2, cropped.getWidth(), cropped.getHeight(), this.marioWorld.cameraX, this.marioWorld.cameraY, this.marioWorld.mario.xa, this.marioWorld.mario.ya);
        /*
        File outputfile = new File("cropped" + x1 + ".jpg");
        try {
            ImageIO.write(cropped, "jpg", outputfile);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        } */


        return cropped;

    }

    private int getEgocentricScaledWidth() {
        float scaling = 0.5f;
        return (int) (scaling * (this.egoOffset * 2));
    }

    private int getEgocentricScaledHeight() {
        float scaling = 0.5f;
        return (int) (scaling * (this.egoOffset * 2));
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

    public void saveImage(String name, BufferedImage image) {
        File outputfile = new File(name);
        try {
            ImageIO.write(image, "jpg", outputfile);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
