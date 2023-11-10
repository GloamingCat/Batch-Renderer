package rendering;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

	public static final int internalFormat = GL_RGBA;
	
	private final int id;
	public final int width;
	public final int height;
	public final int channels;

	public Texture(int width, int height, int channels, ByteBuffer data) {
		id = glGenTextures();
		this.width = width;
		this.height = height;
		this.channels = channels;
		bind();
		setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		setNearestFilter();
		//setLinearFilter();
		loadData(data);
	}
	
	public Texture(int width, int height, int channels) {
		this(width, height, channels, null);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void bind(int slot) {
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, id);
		glActiveTexture(GL_TEXTURE0);
	}

	public void bindToFrameBuffer(int bufferId) {
		glBindFramebuffer(GL_FRAMEBUFFER, bufferId);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, id, 0);
	}

	public void dispose() {
		glDeleteTextures(id);
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

	private void loadData(ByteBuffer buffer) {
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, internalFormat, GL_UNSIGNED_BYTE, buffer);
	}

	private void writeData(ByteBuffer buffer) {
		glGetTexImage(GL_TEXTURE_2D, 0, internalFormat, GL_UNSIGNED_BYTE, buffer);
	}

	public void write(String path, int channels) {
		bind();
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * channels);
		writeData(buffer);
		if (!STBImageWrite.stbi_write_png(path, width, height, channels, buffer, this.channels * width)) {
			throw new RuntimeException("Failed to write texture: " + STBImage.stbi_failure_reason());
		}
	}
	
	public void write(String path) {
		write(path, channels);
	}
	
	public ByteBuffer toBuffer(int channels) {
		bind();
		ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * channels);
		writeData(buffer);
		return buffer;
	}
	
	//////////////////////////////////////////////////
	// {{ Statis methods
	
	public static Texture load(String path, int channels) {
		ByteBuffer image;
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1);
			image = stbi_load(path, w, h, c, channels);
			if (image == null)
				throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
			width = w.get();
			height = h.get();
			if (channels == -1)
				channels = c.get();
		}
		return new Texture(width, height, channels, image);
	}
	
	public static Texture load(String path) {
		return load(path, -1);
	}
	
	public static Texture white(int alpha) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(4);
		buffer.put(0, (byte) 255);
		buffer.put(1, (byte) 255);
		buffer.put(2, (byte) 255);
		buffer.put(3, (byte) alpha);
		return new Texture(1, 1, 4, buffer);
	}
	
	// }}
	
}