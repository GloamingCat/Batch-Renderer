package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

	private float bgR, bgG, bgB, bgA;
	private float pR, pG, pB, pA;

	public Renderer() {	}

	public void setBackgroundColor(float r, float g, float b, float a) {
		bgR = r; bgG = g; bgB = b; bgA = a;
	}

	public void setPencilColor(float r, float g, float b, float a) {
		pR = r; pG = g; pB = b; pA = a;
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
	}
	
	public void drawVertices(float[] data, int nAtt, int mode) {
		glBegin(mode);
		for (int i = 0; i < data.length; i += nAtt) {
			glVertex2f(data[i], data[i+1]);
			glTexCoord2f(data[i+2], data[i+3]);
		}
		glEnd();
	}
	
	public void drawVertices(int vaoId, int n, int mode) {
		glBindVertexArray(vaoId);
		glDrawArrays(mode, 0, n);
	}

	public void drawQuads(float[] data, int nAtt) {
		drawVertices(data, nAtt, GL_QUADS);
	}
	
	public void drawQuads(int vaoId, int n) {
		drawVertices(vaoId, n, GL_QUADS);
	}

	public void drawRectangle(float x, float y, int w, int h) {
		float[] data = new float[] {
			x, y, 0.0f, 0.0f,
			x+w, y, 1.0f, 0.0f,
			x+w, y+h, 1.0f, 1.0f,
			x, y+h, 0.0f, 1.0f
		};
		drawQuads(data, 2);
	}

	public void drawTriangles(float[] data, int vSize) {
		drawVertices(data, vSize, GL_TRIANGLES);
	}
	
	public void drawTriangles(int vaoId, int n) {
		drawVertices(vaoId, n, GL_TRIANGLES);
	}

	public void drawOutline(float[] vertices) {
		glColor4f(pR / 255f, pG / 255f, pB / 255f, pA / 255f);
		glBegin(GL_LINE_LOOP);
		for (int i = 0; i < vertices.length; i += 2) {
			glVertex2f(vertices[i], vertices[i+1]);
		}
		glEnd();
	}
	
	public void drawOctagon(int width, int height, float base, float side) {
		float w = width - 1;
		float h = height - 1;
		float t1 = (width - base) / 2;
		float t2 = w - t1;
		float l1 = (height - side) / 2;
		float l2 = h - l1;
		// Draw
		float[] points = new float[] {
			t1, 0,
			t2, 0,
			w, l1,
			w, l2,
			t2, h,
			t1, h,
			0, l2,
			0, l1
		};
		drawOutline(points);
	}

}
