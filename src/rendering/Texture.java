package rendering;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int id;
    private int width;
    private int height;

    public Texture() {
        id = glGenTextures();
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
    
    public void bindToFrameBuffer() {
    	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, id, 0);
    }

    public void dispose() {
        glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void setLinearFilter() {
		setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
    
    public void setNearestFilter() {
        setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }
    
    private void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    private void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }
    
    public static Texture createTexture(int width, int height, ByteBuffer data) {
        Texture texture = new Texture();
        texture.width = width;
        texture.height = height;
        texture.bind();
        texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setNearestFilter();
        texture.uploadData(GL_RGBA8, width, height, GL_RGBA, data);
        return texture;
    }

    public static Texture loadTexture(String path) {
        ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(path, w, h, comp, 4);
            if (image == null)
                throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator() + stbi_failure_reason());
            width = w.get();
            height = h.get();
        }
        return createTexture(width, height, image);
    }


}