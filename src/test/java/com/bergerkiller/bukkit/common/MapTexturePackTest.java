package com.bergerkiller.bukkit.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Vector2f;

public class MapTexturePackTest
{

    //@Test
    public void test3DRender() {
        
        // Load the source texture
        MapResourcePack texturePack = new MapResourcePack("C:\\Users\\QT\\Desktop\\TexturePack\\1.12.1.jar");

        
        createAndShowGUI(texturePack);
    
        while (true) {
            AsyncTask.sleep(5000);
        }
    }

    public static void createAndShowGUI(MapResourcePack texture)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("C:\\Users\\QT\\Desktop\\lena512color.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        f.getContentPane().setLayout(new GridLayout(1,2));
        f.getContentPane().add(new JLabel(new ImageIcon(image)));
        f.getContentPane().add(new Pseudo3DImagePanel(image, texture));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

class Pseudo3DImagePanel extends JPanel
    implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 1L;
    private final BufferedImage inputBufferedImage;
    private final MapTexture background;
    private final MapResourcePack textures;
    private final Vector2f p0;
    private final Vector2f p1;
    private final Vector2f p2;
    private final Vector2f p3;
    private Vector2f draggedPoint;

    Pseudo3DImagePanel(BufferedImage inputImage, MapResourcePack textures)
    {
        this.inputBufferedImage = inputImage;
        this.textures = textures;
        this.background = MapTexture.fromImage(inputImage);
        this.background.setBlendMode(MapBlendMode.MULTIPLY);
        this.background.fill(MapColorPalette.COLOR_BLUE);
        this.p0 = new Vector2f(256,256);
        this.p1 = new Vector2f(128,128);
        this.p2 = new Vector2f(0,0);
        this.p3 = new Vector2f(0,0);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        MapTexture image = MapTexture.createEmpty(512, 512);
        image.setBlendMode(MapBlendMode.OVERLAY);
        //image.draw(background, 0, 0);

        // Draws a 3D quad
        float scale = 8.0f;
        float yaw = p1.x - 128;
        float pitch = p1.y - 128;

        //System.out.println("Yaw=" + yaw + " Pitch=" + pitch);

        Model model = textures.getModel("block/cactus");
        image.drawModel(model, scale, (int) p0.x, (int) p0.y, yaw, pitch);

        g.drawImage(image.toJavaImage(), 0, 0, null);

        int r = 8;
        g.setColor(Color.BLUE);
        g.fillOval((int)p0.x-r, (int)p0.y-r, r+r, r+r);
        g.fillOval((int)p1.x-r, (int)p1.y-r, r+r, r+r);
        g.fillOval((int)p2.x-r, (int)p2.y-r, r+r, r+r);
        g.fillOval((int)p3.x-r, (int)p3.y-r, r+r, r+r);
    }



    @Override
    public void mousePressed(MouseEvent e)
    {
        Vector2f p = new Vector2f(e.getX(), e.getY());
        int r = 8;
        if (p.distance(p0) < r) draggedPoint = p0;
        if (p.distance(p1) < r) draggedPoint = p1;
        if (p.distance(p2) < r) draggedPoint = p2;
        if (p.distance(p3) < r) draggedPoint = p3;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (draggedPoint != null)
        {
            draggedPoint.x = e.getX();
            draggedPoint.y = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        draggedPoint = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}

