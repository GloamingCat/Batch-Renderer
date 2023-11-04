import rendering.*;
import integration.*;

public class IntegrationTestFB {
	
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	private final int BUFFER_WIDTH = 400;
	private final int BUFFER_HEIGHT = 300;
	
	private Screen screen;
	private Renderer renderer;
	private ShaderProgram shader;
	private Context context;
	private FrameBufferRenderer fbRenderer;
	
	Texture ralsei;
	VertexArray quad;
	
	public static void main(String[] args) {
		new IntegrationTestFB().run();
	}

	public void run() {
		context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT, "Test Frame Buffer") {
			public void render() {
				fbRenderer.renderToScreen();
			}
		};
		context.init();
		init();
		context.show();
		context.dispose();
	}
	
	private void init() {
		try {
			shader = new ShaderProgram();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		shader.bind();
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT);
		ralsei = Texture.load("ralsei.png", 4);
		renderer = new Renderer();
		renderer.setBackgroundColor(43, 43, 0, 0); // Dark yellow
		quad = VertexArray.quad(0, 0, ralsei.width, ralsei.height);
		quad.initVAO(shader.attributes);
		renderer.resetBindings();
		fbRenderer = new FrameBufferRenderer(BUFFER_WIDTH, BUFFER_HEIGHT, screen, shader) {
			@Override
			public void drawContent() {
				drawTest();
			}
		};
		screen.bind(shader);
	}
	
	private void drawTest() {
		ralsei.bind();
		renderer.fillBackground();
		renderer.drawQuads(quad.getVaoId(), 4);
		renderer.resetBindings();
	}

}