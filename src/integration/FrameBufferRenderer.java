package integration;

import rendering.Renderer;
import rendering.Screen;
import rendering.ShaderProgram;
import rendering.VertexArray;

public abstract class FrameBufferRenderer {
	
	private final Renderer renderer;
	private final Screen main, fb;
	private final ShaderProgram shader;
	private final VertexArray fbQuad;

	public FrameBufferRenderer(int width, int height, Screen main, ShaderProgram shader) {
		this.shader = shader;
		this.main = main;
		renderer = new Renderer();
		renderer.setBackgroundColor(43, 0, 43, 0); // Dark purple
		fb = new Screen(width, height, false);
		fbQuad = VertexArray.quad(0, 0, main.width, main.height);
		fbQuad.initVAO(shader.attributes, shader.vertexSize);
		renderToBuffer();
	}
	
	private void renderToBuffer() {
		shader.bind();
		fb.bind(shader);
		drawContent();
		renderer.resetBindings();
	}
	
	public void renderToScreen() {
		shader.bind();
		main.bind(shader);
		renderer.fillBackground();
		fb.texture.bind();
		renderer.drawQuads(fbQuad.getVaoId(), fbQuad.vertices.size());
		renderer.resetBindings();
	}
	
	public abstract void drawContent();
	
	public void dispose() {
		fb.dispose();
		fbQuad.dispose();
	}
}
