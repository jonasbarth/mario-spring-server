package engine.helper;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.io.FileInputStream;


public class Assets
{
    public static Image[][] mario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    public static Image[][] map;
    static String curDir2 = System.getProperty("user.dir");
    static String curDir = curDir2.substring(0, curDir2.length());
    final static String img = curDir + "\\img\\";

    public static void init(GraphicsConfiguration gc)
    {
        try
        {
            mario = cutImage(gc, "mariosheet.png", 32, 32);
            smallMario = cutImage(gc, "smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, "firemariosheet.png", 32, 32);
            enemies = cutImage(gc, "enemysheet.png", 16, 32);
            items = cutImage(gc, "itemsheet.png", 16, 16);
            level = cutImage(gc, "mapsheet.png", 16, 16);
            particles = cutImage(gc, "particlesheet.png", 16, 16);
            font = cutImage(gc, "font.gif", 8, 8);
        }
        catch (Exception e)
        {   
            e.printStackTrace();
        }

    }

    private static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException
    {
        BufferedImage source = null;
        try {
            //source = ImageIO.read(Assets.class.getResourceAsStream(img + imageName));
            source = ImageIO.read(Assets.class.getResourceAsStream("/images/" + imageName));
            //source = ImageIO.read(new FileInputStream(new File(img + imageName)));

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (source == null) {
            imageName = img + imageName;
            //System.out.println(imageName);
            //System.out.println(new File(imageName).exists());
            File file = new File(imageName);
            source = ImageIO.read(file);
        }
        if (source == null) {
            File file = new File(imageName);
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            String suffix = imageName.substring(imageName.length() - 3, imageName.length());
            ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next ();
            reader.setInput(iis, true);
            source = reader.read (0);
        }
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException
    {
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++)
        {
            for (int y = 0; y < source.getHeight(null) / ySize; y++)
            {
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }

        return images;
    }

}