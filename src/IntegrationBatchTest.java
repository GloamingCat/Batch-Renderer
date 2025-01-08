import static org.lwjgl.opengl.GL11.GL_FLOAT;

import integration.SceneRenderer;
import rendering.*;

public class IntegrationBatchTest {
	
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	
	private Screen screen;
	private ShaderProgram shader;
	private SceneRenderer sceneRenderer;
	private Renderer renderer;
	
	public static void main(String[] args) {
		new IntegrationBatchTest().run();
	}

	public void run() {
		Context context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT, "Test Batch Integration") {
			public void render() {
				shader.bind();
				screen.bind(shader);
				sceneRenderer.draw();
				renderer.resetBindings();
			}
		};
		context.init();
		init();
		context.show();
		context.dispose();
		clear();
		sceneRenderer.dispose();
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
		renderer = new Renderer();
		sceneRenderer = new SceneRenderer(WINDOW_WIDTH, WINDOW_HEIGHT, shader, renderer);
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT, true);
		shader.bind();
		screen.bind(shader);
	}
	
	private void clear() {
		renderer.resetBindings();
		sceneRenderer.dispose();
		screen.dispose();
		shader.dispose();
	}

}