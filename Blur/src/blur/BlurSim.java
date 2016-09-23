package blur;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class BlurSim 
{
    static class buffer
    {
        int pixels[];
        int width;
        int height;
    }
    
    static int getPixelSafe(int x, int y, buffer buf)
    {
        if (x < 0)
            x = buf.width+x;
        
        if (x >= buf.width)
        	x = x - buf.width;
        
        if (y < 0)
            y = buf.height+y;
        
        if (y >= buf.height)
        	y = y - buf.height;
        
        return buf.pixels[y * buf.width + x];
    }
    
    static buffer readImage(String path)
    {
    	buffer result = new buffer();
    	try
    	{
            BufferedImage imagem = ImageIO.read(new File(path));
            int width = imagem.getWidth();
            int height = imagem.getHeight();
            int pixels[] = new int[width * height];
            for (int y = 0; y < height; ++y)
                for (int x = 0; x < width; ++x)
                    pixels[y * width + x] = imagem.getRGB(x, y);

            result.width = width;
            result.height = height;
            result.pixels = pixels;
    	} 
    	catch (IOException ex) 
    	{
            ex.printStackTrace();
    	}
        
        return result;
    }
    
    static void saveImage(buffer buf, String path)
    {
    	try
    	{
            BufferedImage image = new BufferedImage(buf.width, buf.height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < buf.height; ++y)
                    for (int x = 0; x < buf.width; ++x)
                            image.setRGB(x, y, buf.pixels[y * buf.width + x]);

            ImageIO.write(image, "jpg", new File(path));
    	} 
    	catch (IOException ex) 
    	{
            ex.printStackTrace();
    	}
    }
    
    static void gaussianBlur(buffer buffer)
    {
        int offset[] = {-1, 0, 1};
        double kernel[] = 
        {
            1.0/16, 1.0/8, 1.0/16,
            1.0/8, 1.0/4, 1.0/8,
            1.0/16, 1.0/8, 1.0/16,
        };
        
        for (int pass = 0; pass < 1; ++pass) 
        {
	        for (int y = 0; y < buffer.height; ++y)
	        {
	            for (int x = 0; x < buffer.width; ++x)
	            {
	            	double finalRed = 0;
	            	double finalGreen = 0;
	            	double finalBlue = 0;
	            	for (int w = 0; w < 9; ++w)
	            	{
                        int pixel = getPixelSafe(x+offset[w % 3], y+offset[w / 3], buffer);
                        double k = kernel[w];

                        int red = (pixel) & 0xFF;
	                    int green = (pixel >> 8) & 0xFF;
	                    int blue = (pixel >> 16) & 0xFF;
	                    
	                    finalRed += (red * k);
	                    finalGreen += (green * k);
	                    finalBlue += (blue * k);
	            	}
	            	
	            	int finalPixel = ((int)finalRed << 0) | ((int)finalGreen << 8) | ((int)finalBlue << 16);
	            	buffer.pixels[y * buffer.width + x] = finalPixel;
	            }
	        }
        }
    }
    
    public static void main(String[] args) throws IOException 
    {
        Random rand = new Random();
        
        File[] fileList = new File("data").listFiles();
        buffer[] bufferList = new buffer[fileList.length];

        for (int i = 0; i < fileList.length; ++i)
            bufferList[i] = readImage(fileList[i].getName());

        // Gera 100 dados processando a imagem
        for (int i = 0; i < 100; ++i) 
        {
            int bufferIndex = rand.nextInt(bufferList.length);
            buffer buf = bufferList[bufferIndex];

            long t0 = System.currentTimeMillis();
            gaussianBlur(buf);
            long t1 = System.currentTimeMillis();

            long dt = (t1 - t0);
            System.out.println(dt);
        }
    }
}