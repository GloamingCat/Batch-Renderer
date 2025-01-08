import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import rendering.Matrix4f;

import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import rendering.OpenGLError;
import rendering.OpenGLException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class OpenGL3Test {

	static final int WIDTH = 800;
	static final int HEIGHT = 600;

	public static void main(String[] args) throws IOException {

		//////////////////////////////////////////////////
		//region Initialization

		if (!GLFW.glfwInit()) {
			throw new OpenGLError("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		long window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Render PNG with LWJGL 3", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL)
			throw new OpenGLError("Failed to create the GLFW window");

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();

		//glEnable(GL_FRAMEBUFFER_SRGB);
		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//endregion

		//////////////////////////////////////////////////
		//region Create shader

		// Load shaders (vertex and fragment)
		String vertexShaderSource = """
                #version 330 core
                layout(location = 0) in vec2 position;
                layout(location = 1) in vec2 texCoords;
                out vec2 fragTexCoords;
                uniform mat4 projectionMatrix;
                void main() {
                    gl_Position = projectionMatrix * vec4(position, 0.0, 1.0);
                    fragTexCoords = texCoords;
                }""";
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);

		if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new OpenGLException("Vertex shader compilation failed: " + glGetShaderInfoLog(vertexShader));
		}

		String fragmentShaderSource = """
                #version 330 core
                in vec2 fragTexCoords;
                out vec4 color;
                uniform sampler2D textureSampler;
                void main() {
                    color = texture(textureSampler, fragTexCoords);
                }""";
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);

		if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new OpenGLException("Fragment shader compilation failed: " + glGetShaderInfoLog(fragmentShader));
		}

		int shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);

		if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
			throw new OpenGLError("Shader program linking failed: " + glGetProgramInfoLog(shaderProgram));
		}
		//endregion
		
		//////////////////////////////////////////////////
		//region Load Texture

		// Load the PNG image using STBImage
		ByteBuffer imageBuffer;
		int width, height;
		final int bpp = 4;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer w = stack.mallocInt(1);
			final IntBuffer h = stack.mallocInt(1);
			final IntBuffer c = stack.mallocInt(1);
			imageBuffer = STBImage.stbi_load("ralsei.png", w, h, c, bpp);
			if (imageBuffer == null) {
				throw new IOException("Failed to load the image: " + STBImage.stbi_failure_reason());
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

		// Texture object vertices
		float[] texVertices = {
				0.0f, 0.0f, 0.0f, 0.0f,
				width, 0.0f, 1.0f, 0.0f,
				width, height, 1.0f, 1.0f,
				0.0f, height, 0.0f, 1.0f
		};

		// Set up texture VAO
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8);
		glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//endregion
		
		//////////////////////////////////////////////////
		//region Create Frame Buffer

		// Bind buffer
		int fboId = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);

		// Create a texture to render to
		int fWidth = WIDTH / 2;
		int fHeight = HEIGHT / 2;
		int sWidth = WIDTH;
		int sHeight = HEIGHT;
		int bufferTextureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, bufferTextureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fWidth, fHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bufferTextureId, 0);

		// Un-bind buffer
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new OpenGLError("Framebuffer is not complete");
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		// Buffer object vertices
		float[] bufVertices = {
				0.0f, 0.0f, 0.0f, 0.0f,
				sWidth, 0.0f, 1.0f, 0.0f,
				sWidth, sHeight, 1.0f, 1.0f,
				0.0f, sHeight, 0.0f, 1.0f
		};

		// Set up buffer VAO and VBOs
		int vaoIdBuf = glGenVertexArrays();
		glBindVertexArray(vaoIdBuf);
		int vboIdBuf = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboIdBuf);
		glBufferData(GL_ARRAY_BUFFER, bufVertices, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8);
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//endregion

		//////////////////////////////////////////////////
		//region Shader bindings

		// Bind program
		glUseProgram(shaderProgram);
		
		// Bind the texture unit
		int textureLocation = glGetUniformLocation(shaderProgram, "textureSampler");
		glUniform1i(textureLocation, 0);

		// Screen projection
		Matrix4f screenView = Matrix4f.orthographic(0, WIDTH, HEIGHT, 0, 1, -1);
		//Matrix4f screenView = new Matrix4f();
		FloatBuffer screenViewBuffer = MemoryUtil.memAllocFloat(16);
		screenView.toBuffer(screenViewBuffer);

		// Buffer projection
		Matrix4f bufferView = Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, 1, -1);
		//Matrix4f bufferView = new Matrix4f();
		FloatBuffer bufferViewBuffer = MemoryUtil.memAllocFloat(16);
		bufferView.toBuffer(bufferViewBuffer);

		int projectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");
		
		//endregion

		//////////////////////////////////////////////////
		//region Draw texture on buffer
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);
		glUniformMatrix4fv(projectionMatrixLocation, false, bufferViewBuffer);

		glClearColor(0, 0, 0.4f, 1); // Dark blue
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Draw texture to buffer
		glBindTexture(GL_TEXTURE_2D, textureId);
		glBindVertexArray(vaoId);
		glDrawArrays(GL_QUADS, 0, 4);
		glBindVertexArray(0);
		//endregion
		
		//////////////////////////////////////////////////
		//region Draw buffer on screen
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUniformMatrix4fv(projectionMatrixLocation, false, screenViewBuffer);

		//////////////////////////////////////////////////
		//region Save buffer to file
		glBindTexture(GL_TEXTURE_2D, bufferTextureId);
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(fWidth * fHeight * bpp);
		glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		if (!STBImageWrite.stbi_write_png("bla.png", fWidth, fHeight, bpp, byteBuffer, bpp * fWidth)) {
			throw new OpenGLException("Failed to save the image: " + STBImage.stbi_failure_reason());
		}
		glBindTexture(GL_TEXTURE_2D, 0);
		//endregion
		
		// Main rendering loop
		GLFW.glfwShowWindow(window);

		while (!GLFW.glfwWindowShouldClose(window)) {
			glClearColor(0.4f, 0, 0, 1); // Dark red
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// Draw buffer to screen
			glBindTexture(GL_TEXTURE_2D, bufferTextureId);
			glBindVertexArray(vaoIdBuf);
			glDrawArrays(GL_QUADS, 0, 4);
			glBindVertexArray(0);

			// Check for OpenGL errors
			int error = glGetError();
			if (error != GL_NO_ERROR) {
				throw new OpenGLError(" " + error);
			}

			// Update the display
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
		}
		//endregion

		//////////////////////////////////////////////////
		//region Clean-up
		glDeleteTextures(textureId);
		glDeleteBuffers(vboId);
		glDeleteVertexArrays(vaoId);
		glDeleteTextures(bufferTextureId);
		glDeleteBuffers(vboIdBuf);
		glDeleteVertexArrays(vaoIdBuf);
		glDetachShader(shaderProgram, vertexShader);
		glDetachShader(shaderProgram, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		glDeleteProgram(shaderProgram);
		STBImage.stbi_image_free(imageBuffer);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		//endregion
	}




}
