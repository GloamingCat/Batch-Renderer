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
	private FrameBufferRenderer fbRenderer;
	
	Texture ralsei;
	VertexArray quad;
	
	public static void main(String[] args) {
		new IntegrationTestFB().run();
	}

	public void run() {
		Context context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT, "Test FB Integration") {
			public void render() {
				fbRenderer.renderToScreen();
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
			shader = new ShaderProgram();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		shader.bind();
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT, true);
		ralsei = Texture.load("ralsei.png", 4);
		renderer = new Renderer();
		renderer.setBackgroundColor(43, 43, 0, 0); // Dark yellow
		quad = VertexArray.quad(0, 0, ralsei.width, ralsei.height);
		quad.initVAO(shader.attributes, shader.vertexSize);
		renderer.resetBindings();
		fbRenderer = new FrameBufferRenderer(BUFFER_WIDTH, BUFFER_HEIGHT, screen, shader) {
			@Override
			public void drawContent() {
				drawTest();
			}
		};
	}
	
	private void drawTest() {
		ralsei.bind();
		renderer.fillBackground();
		renderer.drawQuads(quad.getVaoId(), quad.vertices.size());
	}
	
	private void clear() {
		fbRenderer.dispose();
		screen.dispose();
		shader.dispose();
	}

}