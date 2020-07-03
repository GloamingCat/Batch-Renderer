package rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class FrameBuffer {
	
	public int width, height;
	public int id, renderBuffer;
	public Texture texture;
	
	public FrameBuffer(int width, int height) {
		this.id = 0;
		this.renderBuffer = 0;
		this.width = width;
		this.height = height;
		this.texture = null;
	}
	
	public void init() {
		// Create frame buffer.
		id = glGenFramebuffersEXT();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
		// Create render buffer.
		renderBuffer = glGenRenderbuffersEXT();
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, renderBuffer);
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, renderBuffer);
		// Create texture.
		texture = Texture.createTexture(width, height, null);
		texture.bind();
		texture.bindToFrameBuffer();
		if (glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT) == GL_FRAMEBUFFER_COMPLETE_EXT)
			System.out.println("Frame buffer created sucessfully.");
		else
			System.out.println("An error occured creating the frame buffer.");
	}
	
	public void bind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
	}
	
	public void unbind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
    public BufferedImage toImage() {
        glReadBuffer(GL_FRONT);
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
         
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;
                image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return image;
    }
    
    public void saveImage(String file) {
    	try {
    		BufferedImage image = toImage();
    		File outputfile = new File(file);
    		ImageIO.write(image, "png", outputfile);
    	} catch(IOException e) {
    		System.out.print("ue");
    	}
    }
    
    public void clear() {
    	if (texture != null)
    		texture.dispose();
    	if (id != 0)
    		glDeleteFramebuffersEXT(id);
    	if (renderBuffer != 0)
    		glDeleteRenderbuffersEXT(renderBuffer);
    }
    
}
