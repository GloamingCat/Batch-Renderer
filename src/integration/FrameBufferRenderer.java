package integration;

import rendering.Renderer;
import rendering.Screen;
import rendering.ShaderProgram;
import rendering.VertexArray;

public abstract class FrameBufferRenderer {
	
	private Renderer renderer;
	private Screen main, fb;
	private ShaderProgram shader;
	private VertexArray fbQuad;

	public FrameBufferRenderer(int width, int height, Screen main, ShaderProgram shader) {
		this.shader = shader;
		this.main = main;
		renderer = new Renderer();
		renderer.setBackgroundColor(43, 0, 43, 0); // Dark purple
		fb = new Screen(main.width, main.height, width, height);
		fbQuad = VertexArray.quad(0, 0, main.width, main.height);
		fbQuad.initVAO(shader.attributes, shader.vertexSize);
		renderToBuffer();
	}
	
	private void renderToBuffer() {
		renderer.resetBindings();
		fb.bind(shader);
		drawContent();
		fb.texture.write("bla.png");
	}
	
	public void renderToScreen() {
		renderer.resetBindings();
		main.bind(shader);
		renderer.fillBackground();
		fb.texture.bind();
		renderer.drawQuads(fbQuad.getVaoId(), 4);
	}
	
	public abstract void drawContent();
	
	public void dispose() {
		fb.dispose();
		fbQuad.dispose();
	}
}
