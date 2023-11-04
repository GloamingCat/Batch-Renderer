package rendering;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

public class VertexArray {

	public final ArrayList<Vertex> vertices;
	
	private int vaoId, vboId;
	
	public VertexArray() {
		vertices = new ArrayList<Vertex>();
	}
	
	public VertexArray(int n) {
		vertices = new ArrayList<Vertex>(n);
		for (int i = 0; i < n; i++) {
			vertices.add(new Vertex());
		}
	}
	
	public VertexArray(float[] data, int n) {
		vertices = new ArrayList<Vertex>(n);
		int nAtt = data.length / n;
		for (int i = 0; i < n; i++) {
			Vertex vertex = new Vertex();
			vertex.set(data, nAtt * i);
			vertices.add(vertex);
		}
	}
	
	public void setPositions(float[] x, float[] y) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.setPosition(x[i], y[i]);
		}
	}	
	
	public void setUVs(float[] fs, float[] v) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex p = vertices.get(i);
			p.setUV(fs[i], v[i]);
		}
	}
	
	public void setRGBAs(float[] r, float[] g, float[] b, float[] a) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.setRGBA(r[i], g[i], b[i], a[i]);
		}
	}
	
	public void setHSVs(float[] h, float[] s, float[] v) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex p = vertices.get(i);
			p.setHSV(h[i], s[i], v[i]);
		}
	}
	
	public float[] toArray() {
		return toArray(Vertex.totalSize);
	}
	
	public float[] toArray(int nNumbers) {
		float[] buffer = new float[nNumbers * vertices.size()];
		for (int i = 0; i < vertices.size(); i ++) {
			vertices.get(i).put(buffer, nNumbers*i);
		}
		return buffer;
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public void initVAO(int[] attributes) {
		int vertexSize = 0;
		int nAtt = attributes.length / 3;
		for (int i = 0; i < nAtt; i++) {
			int typeSize = attributes[3*i+1];
			int n = attributes[3*i+2];
			vertexSize += typeSize * n;
		}
		int nFloats = vertexSize / 4;
		float[] buffer = toArray(nFloats); // 
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		int pos = 0;
		for (int i = 0; i < nAtt; i++) {
			int type = attributes[3*i];
			int typeSize = attributes[3*i+1];
			int n = attributes[3*i+2];
			glEnableVertexAttribArray(i);
			glVertexAttribPointer(i, n, type, false, vertexSize, pos);
			pos += typeSize * n;
		}
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void dispose() {
		glDeleteBuffers(vboId);
		glDeleteVertexArrays(vaoId);
	}
	
	public static VertexArray quad(float x, float y, float w, float h) {
		VertexArray array = new VertexArray(4);
		array.setPositions(new float[] { x, x+w, x+w, x }, new float[] { y, y, y+h, y+h});
		array.setUVs(new float[] { 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.0f, 1.0f });
		return array;
	}

}
