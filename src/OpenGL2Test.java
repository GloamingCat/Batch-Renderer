import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class OpenGL2Test {

	static final int WIDTH = 800;
	static final int HEIGHT = 600;	

	public static void main(String[] args) {

		// Set up GLFW
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		long window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Render PNG with LWJGL 3", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		// Load the PNG image using STBImage
		ByteBuffer imageBuffer;
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1);

			imageBuffer = STBImage.stbi_load("ralsei.png", w, h, c, 4);
			if (imageBuffer == null) {
				throw new RuntimeException("Failed to load the image: " + STBImage.stbi_failure_reason());
			}
			width = w.get(0);
			height = h.get(0);
		}

		// Create an OpenGL texture from the image data
		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);

		// Create a buffer id
		int fboId = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);

		// Create a texture to render to
		int bufferTextureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, bufferTextureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bufferTextureId, 0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer is not complete");
		}

		glEnable(GL_FRAMEBUFFER_SRGB);
		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glBindFramebuffer(GL_FRAMEBUFFER, fboId);

		glClearColor(0, 0, 0.5f, 1); // Dark blue
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Draw texture to buffer
		glBindTexture(GL_TEXTURE_2D, textureId);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(1, 0);
		glVertex2f(width, 0);
		glTexCoord2f(1, 1);
		glVertex2f(width, height);
		glTexCoord2f(0, 1);
		glVertex2f(0, height);
		glEnd();

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		// Main rendering loop
		GLFW.glfwShowWindow(window);

		while (!GLFW.glfwWindowShouldClose(window)) {
			glClearColor(0.5f, 0, 0, 1); // Dark red
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// Draw buffer to screen
			glBindTexture(GL_TEXTURE_2D, bufferTextureId);
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
			glTexCoord2f(1, 0);
			glVertex2f(WIDTH / 2, 0);
			glTexCoord2f(1, 1);
			glVertex2f(WIDTH / 2, HEIGHT);
			glTexCoord2f(0, 1);
			glVertex2f(0, HEIGHT);
			glEnd();

			// Update the display
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
		}

		// Clean up resources
		STBImage.stbi_image_free(imageBuffer);
		glDeleteTextures(textureId);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}

}
