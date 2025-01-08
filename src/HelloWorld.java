import static org.lwjgl.opengl.GL11.GL_FLOAT;

import rendering.*;

import java.io.IOException;

public class HelloWorld {
	
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	
	private Screen screen;
	private Renderer renderer;
	private ShaderProgram shader;
	
	Texture ralsei;
	Texture white;
	VertexArray quad;
	VertexArray path;
	
	public static void main(String[] args) {
		new HelloWorld().run();
	}

	public void run() {
		Context context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT) {
			public void render() {
				drawTest();
			}
		};
		context.init();
		init();
		context.show();
		context.dispose();
		clear();
	}
	
	private void init() {
		try {
			int[] attributes = {
				GL_FLOAT, 4, 2,
				GL_FLOAT, 4, 2,
				GL_FLOAT, 4, 4,
				GL_FLOAT, 4, 3
			};
			shader = new ShaderProgram("shaders/vertShader.glsl", "shaders/fragShader.glsl", attributes);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        try {
            ralsei = Texture.load("ralsei.png", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        white = Texture.white(255);
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT, true);
		renderer = new Renderer();
		renderer.setBackgroundColor(60, 60, 0, 0); // Dark yellow
		quad = VertexArray.quad(0, 0, ralsei.width, ralsei.height);
		quad.initVAO(shader.attributes, shader.vertexSize);
		path = VertexArray.octagon(300, 300, 100, 50, 50, 20, 0, 255, 255, 255);
		path.initVAO(shader.attributes, shader.vertexSize);
		renderer.resetBindings();
		shader.bind();
		screen.bind(shader);
	}
	
	private void drawTest() {
		shader.bind();
		screen.bind(shader);
		renderer.fillBackground();
		white.bind();
		renderer.drawPath(path.getVaoId(), path.vertices.size());
		ralsei.bind();
		renderer.drawQuads(quad.getVaoId(), quad.vertices.size());
		renderer.resetBindings();
	}
	
	private void clear() {
		ralsei.dispose();
		quad.dispose();
		screen.dispose();
		shader.dispose();
	}

}