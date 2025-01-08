import static org.lwjgl.opengl.GL11.GL_FLOAT;

import integration.FrameBufferRenderer;
import integration.SceneRenderer;
import rendering.*;

public class IntegrationTest {

	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	private final int BUFFER_WIDTH = 400;
	private final int BUFFER_HEIGHT = 300;

	private Screen screen;
	private ShaderProgram shader;
	private FrameBufferRenderer fbRenderer;
	private SceneRenderer sceneRenderer;

    public static void main(String[] args) {
		new IntegrationTest().run();
	}

	public void run() {
		Context context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT, "Test Integration") {
			public void render() {
				fbRenderer.renderToScreen();
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
		int[] attributes = new int[] {
			GL_FLOAT, 4, 2,
			GL_FLOAT, 4, 2,
			GL_FLOAT, 4, 4,
			GL_FLOAT, 4, 3
		};
        Renderer renderer = new Renderer();
		shader = new ShaderProgram("shaders/vertShader.glsl", "shaders/fragShader.glsl", attributes);
		sceneRenderer = new SceneRenderer(WINDOW_WIDTH, WINDOW_HEIGHT, shader, renderer);
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT, true);
		shader.bind();
		fbRenderer = new FrameBufferRenderer(BUFFER_WIDTH, BUFFER_HEIGHT, screen, shader) {
			@Override
			public void drawContent() {
				sceneRenderer.draw();
			}
		};
	}
	
	private void clear() {
		sceneRenderer.dispose();
		fbRenderer.dispose();
		screen.dispose();
		shader.dispose();
	}

}
