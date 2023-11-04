import rendering.*;

public class HelloWorld {
	
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	
	private Screen screen;
	private Renderer renderer;
	private ShaderProgram shader;
	
	Texture ralsei;
	VertexArray quad;
	
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
			shader = new ShaderProgram();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		ralsei = Texture.load("ralsei.png", 4);
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT);
		renderer = new Renderer();
		renderer.setBackgroundColor(43, 43, 0, 0); // Dark yellow
		quad = VertexArray.quad(0, 0, ralsei.width, ralsei.height);
		quad.initVAO(shader.attributes, shader.vertexSize);
		renderer.resetBindings();
		shader.bind();
		screen.bind(shader);
	}
	
	private void drawTest() {
		ralsei.bind();
		renderer.fillBackground();
		renderer.drawQuads(quad.getVaoId(), 4);
		renderer.resetBindings();
	}
	
	private void clear() {
		ralsei.dispose();
		quad.dispose();
		screen.dispose();
		shader.dispose();
	}

}