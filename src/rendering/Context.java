package rendering;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.opengl.GL;

public class Context {
	
	private static boolean glfwInitialized = false;
	
	private long window;
	private int width;
	private int height;
	private String name;

	public Context(int width, int height, String name) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.window = NULL;
	}
	
	public Context(int width, int height) {
		this(width, height, "Hello World!");
	}

	public void init() {
		if (!glfwInitialized) {
			if ( !glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");
			glfwInitialized = true;
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		//glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		window = glfwCreateWindow(width, height, name, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		bind();
		GL.createCapabilities();
		
		//glEnable(GL_FRAMEBUFFER_SRGB);
		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LINE_STIPPLE);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(
			GL_SRC_ALPHA,
			GL_ONE_MINUS_SRC_ALPHA,
			GL_ONE,
			GL_ONE_MINUS_SRC_ALPHA
		);
		int error = glGetError();
		if (error != GL_NO_ERROR) {
			System.err.println("Context Error: " + error);
		}
	}
	
	public boolean isInitialized() {
		return window != NULL;
	}
	
	public void bind() {
		glfwMakeContextCurrent(window);
	}
	
	public void dispose() {
		if (!glfwInitialized)
			return;
		if (window != NULL) {
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		}
		glfwTerminate();
	}
	
	public void show() {
		if (window ==  NULL)
			throw new RuntimeException("Context not initialized!");
		glfwShowWindow(window);
		while ( !glfwWindowShouldClose(window) ) {
			render();
			int error = glGetError();
			if (error != GL_NO_ERROR)
				System.err.println("OpenGL Error: " + error);
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	public void render() {}
	
}
