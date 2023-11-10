package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

	private float bgR, bgG, bgB, bgA;
	private float pR, pG, pB, pA, pSize = 2;

	public Renderer() {	}

	//////////////////////////////////////////////////
	// {{ State
	
	public void setBackgroundColor(float r, float g, float b, float a) {
		bgR = r; bgG = g; bgB = b; bgA = a;
	}

	public void setPencilColor(float r, float g, float b, float a) {
		pR = r; pG = g; pB = b; pA = a;
	}
	
	public void setPencilSize(float size) {
		pSize = size;
	}

	public void fillBackground() {
		glClearColor(bgR / 255f, bgG / 255f, bgB / 255f, bgA / 255f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void resetBindings() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindVertexArray(0); 
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
		glUseProgram(0);
	}

	// }}
	
	//////////////////////////////////////////////////
	// {{ Mesh
	
	public void drawVertices(int vaoId, int n, int mode) {
		glBindVertexArray(vaoId);
		glDrawArrays(mode, 0, n);
	}

	public void drawQuads(int vaoId, int n) {
		drawVertices(vaoId, n, GL_QUADS);
	}
	
	public void drawTriangles(int vaoId, int n) {
		drawVertices(vaoId, n, GL_TRIANGLES);
	}
	
	public void drawPolygon(int vaoId, int n) {
		drawVertices(vaoId, n, GL_POLYGON);
	}
	
	public void drawPath(int vaoId, int n) {
		glLineWidth(pSize);
		glColor4f(pR / 255f, pG / 255f, pB / 255f, pA / 255f);
		drawVertices(vaoId, n, GL_LINE_LOOP);
	}

	// }}

	
}
