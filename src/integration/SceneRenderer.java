package integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import batching.*;
import rendering.Renderer;
import rendering.ShaderProgram;
import rendering.Texture;
import rendering.VertexArray;

public class SceneRenderer {

	private final Renderer renderer;
	private Scene scene;
	private final HashMap<String, Texture> loadedTextures;
	private final int width, height;
	private final VertexArray vertexArray;
	private final int nFloats;
	
	public SceneRenderer(int w, int h, ShaderProgram shader, Renderer renderer) {
		width = w;
		height = h;
		loadedTextures = new HashMap<>();
		this.renderer = renderer;
		init();
		vertexArray = new VertexArray(scene.allObjects().size() * 4);
		vertexArray.initVAO(shader.attributes, shader.vertexSize);
		nFloats = shader.vertexSize / 4;
	}
	
	protected void init() {
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
			float x = rand.nextFloat() * (width - ralsei.width);
			float y = rand.nextFloat() * (height - ralsei.height);
			scene.add(ralsei, transform, x + 29, y + 28, ralsei.width, ralsei.height, 0);
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
			float x2 = rand.nextFloat() * (width - t.width * 2);
			float y2 = rand.nextFloat() * (height - t.height * 2);
			scene.add(t, transform2, x2 + 29, y2 + 26, t.width * 2, t.height * 2, 0);
		}
		scene.shuffle();
	}
	
	public void draw() {
		final int channels = 4;
		renderer.setBackgroundColor(0, 43, 43, 0); // Dark green
		renderer.fillBackground();
		BatchIterator batches = scene.getBatchIterator();
		while(!batches.done()) {
			Batch batch = batches.next();
			Texture texture = loadedTextures.get(batch.texturePath);
			if (texture == null) {
                try {
                    texture = Texture.load(batch.texturePath, channels);
					loadedTextures.put(batch.texturePath, texture);
                } catch (IOException e) {
                    e.printStackTrace();
					continue;
                }
			}
			int texWidth = texture.width;
			int texHeight = texture.height;
			int n = batch.getSize() * 4;
			texture.bind();
			float[] data = batch.getQuadVertices(texWidth, texHeight, nFloats);
			vertexArray.set(data, n);
			vertexArray.updateVAO(data);
			vertexArray.bind();
			renderer.drawQuads(vertexArray.getVaoId(), n);
			vertexArray.unbind();
		}
		renderer.resetBindings();
	}
	
	public void dispose() {
		vertexArray.dispose();
		for (Texture texture : loadedTextures.values()) {
			texture.dispose();
		}
	}
	
}
