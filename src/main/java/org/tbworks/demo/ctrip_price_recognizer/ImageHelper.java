package org.tbworks.demo.ctrip_price_recognizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Bo tang
 *
 */
public class ImageHelper {
	    /**
	     * 读入图片文件
	     * @param fnm
	     * @return BufferedImage
	     */
	    public BufferedImage getImg(String fnm) {
	        BufferedImage bi = null;
	        try {
	            bi = ImageIO.read(new File(fnm));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return bi;
	    }
	    /**
	     * 图片存盘
	     * @param fnm
	     * @param img
	     */
	    public void mkImgFile(String fnm, BufferedImage img) {
	        try {
	            ImageIO.write(img, "jpg", new File(fnm));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * 返回象元的RGB数组
	     * @param image
	     * @param x
	     * @param y
	     * @return int[3] RGB数组
	     */
	    public static int[] getSplitRGB(BufferedImage image, int x, int y) {
	        int[] rgb = null;
	        if (image != null && x < image.getWidth() && y < image.getHeight()) {
	            rgb = new int[3];
	            int pixel = image.getRGB(x, y);
	            rgb = getSplitRGB(pixel);
	        }
	        return rgb;
	    }
	    /**
	     * 返回象元的RGB数组
	     * @param pixel
	     * @return
	     */
	    public static int[] getSplitRGB(int pixel) {
	        int[] rgbs = new int[3];
	        rgbs[ 0] = (pixel & 0xff0000) >> 16;
	        rgbs[ 1] = (pixel & 0xff00) >> 8;
	        rgbs[ 2] = (pixel & 0xff);
	        return rgbs;
	    }
	    /**
	     * 
	     * @param bimg
	     * @return
	     */
	    public static int[] getPixes(BufferedImage bimg) {
	        int w = bimg.getWidth();
	        int h = bimg.getHeight();
	        int[] rgbs = new int[h * w];
	        bimg.getRGB(0, 0, w, h, rgbs, 0, w);
	        return rgbs;
	    }

	    /**
	     * 获取RGB矩阵
	     * @param image
	     * @return int[3][y]x[] RGB矩阵
	     */
	    public int[][][] getRGBMat(BufferedImage bimg) {
	        int w = bimg.getWidth();
	        int h = bimg.getHeight();
	        int[][][] rgbmat = new int[3][h][w];
	        int[] pixes = getPixes(bimg);
	        int k = 0;
	        for (int y = 0; y < h; y++) {
	            for (int x = 0; x < w; x++) {
	                int[] rgb = getSplitRGB(pixes[k++]);
	                rgbmat[0][y][x] = rgb[0];
	                rgbmat[1][y][x] = rgb[1];
	                rgbmat[2][y][x] = rgb[2];
	            }
	        }
	        return rgbmat;
	    }
	    /**
	     * 根据象元返回图片
	     * @param width
	     * @param height
	     * @param pixels
	     * @return
	     */
	    public Image getImg(int width, int height, int[] pixels) {
	        MemoryImageSource source;
	        Image image = null;
	        source = new MemoryImageSource(width, height, pixels, 0, width);
	        image = Toolkit.getDefaultToolkit().createImage(source);
	        return image;
	    }
	    /**
	     * 根据rg阵返回图片
	     * @param rgbmat
	     * @return
	     */
	    public BufferedImage getImg(int[][][] rgbmat) {
	        int w = rgbmat[0][0].length, h = rgbmat[0].length;
	        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	        for (int y = 0; y < image.getHeight(); y++) {
	            for (int x = 0; x < image.getWidth(); x++) {
	                int r = rgbmat[0][y][x]<<16, g = rgbmat[1][y][x]<<8, b = rgbmat[2][y][x];
	                int pixel = 0xff000000 | r | g | b;
	                //int pixel = r << 16 | g << 8 | b;
	                image.setRGB(x, y, pixel);
	            }
	        }
	        return image;
	    }
	    /**
	     * 产生随机黑白图片
	     * @return
	     */
	    public Image getImg() {
	        int width = 512, height = 512;
	        int[] pixels = new int[width * height];
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                boolean rand = Math.random() > 0.5;
	                pixels[y * width + x] =
	                        rand ? Color.black.getRGB() : Color.white.getRGB();
	            }
	        }
	        return getImg(width, height, pixels);
	    }
	    /**
	     * This method returns true if the specified image has transparent pixels
	     * @param image
	     * @return
	     */
	    public static boolean hasAlpha(Image image) {
	        // If buffered image, the color model is readily available
	        if (image instanceof BufferedImage) {
	            BufferedImage bimage = (BufferedImage) image;
	            return bimage.getColorModel().hasAlpha();
	        }

	        // Use a pixel grabber to retrieve the image's color model;
	        // grabbing a single pixel is usually sufficient
	        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	        try {
	            pg.grabPixels();
	        } catch (InterruptedException e) {
	        }

	        // Get the image's color model
	        ColorModel cm = pg.getColorModel();
	        return cm.hasAlpha();
	    }

	    /**
	     * This method returns a buffered image with the contents of an image
	     * @param image
	     * @return
	     */
	    public static BufferedImage toBufferedImage(Image image) {
	        if (image instanceof BufferedImage) {
	            return (BufferedImage) image;
	        }

	        // This code ensures that all the pixels in the image are loaded
	        image = new ImageIcon(image).getImage();

	        // Determine if the image has transparent pixels; for this method's
	        // implementation, see e661 Determining If an Image Has Transparent Pixels
	        boolean hasAlpha = hasAlpha(image);

	        // Create a buffered image with a format that's compatible with the screen
	        BufferedImage bimage = null;
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        try {
	            // Determine the type of transparency of the new buffered image
	            int transparency = Transparency.OPAQUE;
	            if (hasAlpha) {
	                transparency = Transparency.BITMASK;
	            }

	            // Create the buffered image
	            GraphicsDevice gs = ge.getDefaultScreenDevice();
	            GraphicsConfiguration gc = gs.getDefaultConfiguration();
	            bimage = gc.createCompatibleImage(
	                    image.getWidth(null), image.getHeight(null), transparency);
	        } catch (HeadlessException e) {
	            // The system does not have a screen
	        }

	        if (bimage == null) {
	            // Create a buffered image using the default color model
	            int type = BufferedImage.TYPE_INT_RGB;
	            if (hasAlpha) {
	                type = BufferedImage.TYPE_INT_ARGB;
	            }
	            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	        }

	        // Copy image to buffered image
	        Graphics g = bimage.createGraphics();

	        // Paint the image onto the buffered image
	        g.drawImage(image, 0, 0, null);
	        g.dispose();

	        return bimage;
	    }

	    /**
	     * 显示BufferedImage
	     * @param bimg
	     * @return JPanel图片
	     */
	    public JPanel showImg(final BufferedImage bimg) {
	        class ImgPanel extends JPanel {

	            @Override
	            public void paint(Graphics g) {
	                Graphics2D g2 = (Graphics2D) g;
	                try {
	                    g2.drawImage(bimg, 0, 0, null);
	                } finally {
	                    g2.dispose();
	                }
	            }
	        }
	        ImgPanel ip = new ImgPanel();
	        return ip;
	    }

	    public JPanel showImg(final Image img) {
	        class ImgPanel extends JPanel {

	            @Override
	            public void paint(Graphics g) {
	                Graphics2D g2 = (Graphics2D) g;
	                try {
	                    g2.drawImage(img, 0, 0, null);
	                } finally {
	                    g2.dispose();
	                }
	            }
	        }
	        ImgPanel ip = new ImgPanel();
	        return ip;
	    }

	    public void imgFrame(BufferedImage bimg, Color color) {
	        class ColorMaskFilter extends RGBImageFilter {

	            Color color;

	            ColorMaskFilter(Color mask) {
	                color = mask;
	                canFilterIndexColorModel = true;
	            }

	            public int filterRGB(int x, int y, int pixel) {
	                return 255 << 24 |
	                        (((pixel & 0xff0000) >> 16) * color.getRed() / 255) << 16 |
	                        (((pixel & 0xff00) >> 8) * color.getGreen() / 255) << 8 |
	                        (pixel & 0xff) * color.getBlue() / 255;
	            }
	        }
	        FilteredImageSource ii = new FilteredImageSource(bimg.getSource(), new ColorMaskFilter(color));
	        Image img = Toolkit.getDefaultToolkit().createImage(ii);
	        JPanel p = showImg(img);
	        JFrame frame = new JFrame("Image JFrame");
	        frame.add(p);
	        frame.setSize(600, 600);
	        //frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
	        frame.setVisible(true);
	    }

	    /**
	     * 图片显示窗口
	     * @return 窗口
	     */
	    private JFrame imgFrame(String fnm) {
	        BufferedImage img = getImg(fnm);
	        JPanel p = showImg(img);
	        JFrame frame = new JFrame("Image JFrame");
	        frame.add(p);
	        frame.setSize(img.getWidth(), img.getHeight());
	        //frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
	        frame.setVisible(true);
	        return frame;
	    }

	    private JFrame imgFrame0(String fnm) {
	        //读取图片
	        BufferedImage img = getImg(fnm);
	        
	        //转换为rgb阵
	        int[][][] rgbMat = getRGBMat(img);
	        //再转换为图片
	        img = getImg(rgbMat);
	        JPanel p = showImg(img);
	        JFrame frame = new JFrame("Image JFrame");
	        frame.add(p);
	        frame.setSize(img.getWidth(), img.getHeight());
	        //frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
	        frame.setVisible(true);
	        return frame;
	    }

	    private JFrame imgFrame(Image img) {
	        JPanel p = showImg(img);
	        JFrame frame = new JFrame("Image JFrame");
	        frame.add(p);
	        frame.setSize(600, 600);
	        //frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
	        frame.setVisible(true);
	        return frame;
	    }
 
	
}
