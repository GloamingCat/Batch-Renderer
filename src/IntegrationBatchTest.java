import integration.SceneRenderer;
import rendering.*;

public class IntegrationBatchTest {
	
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	
	private Screen screen;
	private ShaderProgram shader;
	private SceneRenderer sceneRenderer;
	
	public static void main(String[] args) {
		new IntegrationBatchTest().run();
	}

	public void run() {
		Context context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT, "Test Batch Integration") {
			public void render() {
				sceneRenderer.draw();
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
		screen.bind(shader);
	}
	
	private void clear() {
		sceneRenderer.dispose();
		screen.dispose();
		shader.dispose();
	}

}