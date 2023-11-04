import integration.FrameBufferRenderer;
import integration.SceneRenderer;
import rendering.Context;
import rendering.Screen;
import rendering.ShaderProgram;

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
		try {
			shader = new ShaderProgram();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		sceneRenderer = new SceneRenderer(WINDOW_WIDTH, WINDOW_HEIGHT, shader);
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT);
		shader.bind();
		fbRenderer = new FrameBufferRenderer(BUFFER_WIDTH, BUFFER_HEIGHT, screen, shader) {
			@Override
			public void drawContent() {
				sceneRenderer.draw();
			}
		};
		screen.bind(shader);
	}
	
	private void clear() {
		sceneRenderer.dispose();
		fbRenderer.dispose();
		screen.dispose();
		shader.dispose();
	}

}
