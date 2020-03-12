package src.engine.core;

import javax.swing.*;

import src.engine.helper.Assets;
import src.engine.helper.MarioActions;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;


public class MarioRender extends JComponent implements FocusListener {
    private static final long serialVersionUID = 790878775993203817L;
    public static final int TICKS_PER_SECOND = 24;

    private float scale;
    private GraphicsConfiguration graphicsConfiguration;

    int frame;
    Thread animator;
    boolean focused;

    public MarioRender(float scale) {
        this.setFocusable(true);
        this.setEnabled(true);
        this.scale = scale;

        Dimension size = new Dimension((int)(256 * scale), (int)(240 * scale));

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setFocusable(true);
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
        Assets.init(graphicsConfiguration);
    }

    public void renderWorld(MarioWorld world, Image image, Graphics g, Graphics og) {
        og.fillRect(0, 0, 256, 240);
        world.render(og);
        drawStringDropShadow(og, "Lives: " + world.lives, 0, 0, 7);
        drawStringDropShadow(og, "Coins: " + world.coins, 11, 0, 7);
        drawStringDropShadow(og, "Time: " + (world.currentTimer==-1?"Inf":(int)Math.ceil(world.currentTimer/1000f)), 22, 0, 7);
        if(MarioGame.verbose) {
            String pressedButtons = "";
            for (int i = 0; i < world.mario.actions.length; i++) {
                if (world.mario.actions[i]) {
                    pressedButtons += MarioActions.getAction(i).getString() + " ";
                }
            }
            drawStringDropShadow(og, "Buttons: " + pressedButtons, 0, 2, 1);
        }
        if (scale > 1) {
            g.drawImage(image, 0, 0, (int) (256 * scale), (int) (240 * scale), null);
        } else {
            g.drawImage(image, 0, 0, null);
        }

        BufferedImage bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_BYTE_GRAY);

        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[][] matrix = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                //System.out.println(bufferedImage.getRGB(j, i));
                matrix[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        //System.out.println(Arrays.toString(matrix));





        int res = 1;

        // Pick all elements one by one
        for (int i = 1; i < pixels.length; i++)
        {
            int j = 0;
            for (j = 0; j < i; j++)
                if (pixels[i] == pixels[j])
                    break;

            // If not printed earlier,
            // then print it
            if (i == j)
                res++;
        }

        /*
        System.out.printf("%d, %d", pixels.length, res);
        File outputfile = new File("C:\\Users\\Jonas\\Desktop\\Uni\\Year 4\\image.jpg");
        try {
            ImageIO.write(bufferedImage, "jpg", outputfile);
            System.out.println("Image written");
        }
        catch (IOException e) {
            e.printStackTrace();
        } */

    }

    public void drawStringDropShadow(Graphics g, String text, int x, int y, int c)
    {
        drawString(g, text, x*8+5, y*8+5, 0);
        drawString(g, text, x*8+4, y*8+4, c);
    }
    
    private void drawString(Graphics g, String text, int x, int y, int c) {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Assets.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }
    
    public void focusGained(FocusEvent arg0) {
        focused = true;
    }

    public void focusLost(FocusEvent arg0) {
        focused = false;
    }
}