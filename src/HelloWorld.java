

import batching.*;
import rendering.*;

import java.util.Random;

public class HelloWorld {
	
	private final int WINDOW_WIDTH = 640;
	private final int WINDOW_HEIGHT = 480;
	private final int BUFFER_WIDTH = 640;
	private final int BUFFER_HEIGHT = 480;
	
	// The window handle
	private FrameBuffer fb;
	private Renderer renderer;
	private Context context;
	
	private Scene scene;
	
	Texture ralsei;

	public void run() {
		context = new Context(WINDOW_WIDTH, WINDOW_HEIGHT) {
			public void render() {
				renderToScreen();
			}
		};
		context.init();
		init();
		context.show();
		context.dispose();
	}
	
	private void init() {
		initScene();
		ralsei = Texture.loadTexture("ralsei.png");
		renderer = new Renderer();
		renderer.init();
		renderer.setBackgroundColor(43, 43, 43);
		fb = new FrameBuffer(BUFFER_WIDTH, BUFFER_HEIGHT);
		fb.init();
		renderToBuffer();
		renderer.resetBinding();
	}
	
	private void initScene() {
		Random rand = new Random(0);
		scene = new Scene(1);
		for (int i = 0; i < 10; i++) {
			// Random Ralsei
			Quad ralsei = new Quad();
			ralsei.path = "ralsei.png";
			ralsei.width = 57;
			ralsei.height = 56;
			ralsei.x = rand.nextInt(6) * 57;
			ralsei.y = rand.nextInt(2) * 56;
			Transform transform = new Transform();
			transform.offsetX = 29;
			transform.offsetY = 28;
			transform.rotation = rand.nextInt(360);
			float x = rand.nextFloat() * (BUFFER_WIDTH - 57);
			float y = rand.nextFloat() * (BUFFER_HEIGHT - 56);
			scene.add(ralsei, transform, x + 29, y + 28, 57, 56, 0);
			// Random avatar
			Quad t = new Quad();
			t.path = "tt.png";
			t.width = 29;
			t.height = 26;
			t.x = rand.nextInt(2) * 29;
			t.y = 0;
			Transform transform2 = new Transform();
			transform2.offsetX = 14;
			transform2.offsetY = 13;
			transform2.rotation = rand.nextInt(360);
			x = rand.nextFloat() * (BUFFER_WIDTH - 58);
			y = rand.nextFloat() * (BUFFER_HEIGHT - 52);
			scene.add(t, transform2, x + 29, y + 26, 58, 52, 0);
		}
	}
	
	private void renderToBuffer() {
		renderer.setCameraSize(BUFFER_WIDTH, BUFFER_HEIGHT);
		fb.bind();
		drawScene();
		fb.saveImage("bla.png");
		fb.unbind();
	}
	
	private void renderToScreen() {
		renderer.setCameraSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		fb.texture.bind();
		renderer.clear();
		renderer.drawRectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	private void drawScene() {
		renderer.clear();
		BatchIterator batches = scene.getBatchIterator();
		while(!batches.done()) {
			Batch batch = batches.next();
			Texture texture = Texture.loadTexture(batch.texturePath);
			int texWidth = texture.getWidth();
			int texHeight = texture.getHeight();
			texture.bind();
			float[] vert = batch.getVertices(texWidth, texHeight);
			renderer.drawMesh(vert);
			texture.dispose();
		}
	}
	
	public static void main(String[] args) {
		new HelloWorld().run();
	}

}