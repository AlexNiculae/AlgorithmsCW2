import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageController {
    private final static double coefRed = 0.2989;
    private final static double coefGreen = 0.5870;
    private final static double coefBlue = 0.1140;

    private BufferedImage img;
    private File file;

    public void ImageController() {
        img = null;
        file = null;
    }

    public void readImage(String fileName) throws IOException {
        file = new File(fileName);
        img = ImageIO.read(file);
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    public int getPixel(int yCoordinate, int xCoordinate) {
        int rgb = img.getRGB(xCoordinate, yCoordinate);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        return (int)(coefRed * red + coefGreen * green + coefBlue * blue);
    }

    private int getRed(int rgb) {
        return (rgb >> 16) & 0xff;
    }

    private int getGreen(int rgb) {
        return (rgb >> 8) & 0xff;
    }

    private int getBlue(int rgb) {
        return rgb & 0xff;
    }

    public void setImageDimensions(int height, int width) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void setPixel(int yCoordinate, int xCoordinate, int pixelValue) {
        int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
        img.setRGB(xCoordinate, yCoordinate, rgb);
    }

    public void writeImage(String fileName) throws IOException {
        file = new File(fileName);
        ImageIO.write(img, "png", file);
    }
}
