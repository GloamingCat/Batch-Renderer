package rendering;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;

public class VertexArray {

	public final ArrayList<Vertex> vertices;
	
	private int vaoId, vboId;
	private int nFloats = Vertex.totalSize;
	
	//////////////////////////////////////////////////
	// {{ Constructors
	
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
		nFloats = data.length / n;
		for (int i = 0; i < n; i++) {
			Vertex vertex = new Vertex();
			vertex.set(data, nFloats * i);
			vertices.add(vertex);
		}
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Data update
	
	public void set(float[] data, int n) {
		nFloats = data.length / n;
		n = Math.min(n, vertices.size());
		for (int i = 0; i < n; i++) {
			vertices.get(i).set(data, nFloats * i);
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
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Buffer
	
	public float[] toArray() {
		return toArray(nFloats);
	}
	
	public float[] toArray(int nFloats) {
		return toBuffer(vertices.size(), nFloats);
	}
	
	public float[] toBuffer(int n) {
		return toBuffer(n, nFloats);
	}
	
	public float[] toBuffer(int n, int nFloats) {
		float[] buffer = new float[nFloats * n];
		for (int i = 0; i < n; i ++) {
			vertices.get(i).put(buffer, nFloats*i);
		}
		return buffer;
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ VAO
	
	public int getVaoId() {
		return vaoId;
	}
	
	public void bind() {
		glBindVertexArray(vaoId);
	}
	
	public void unbind() {
		glBindVertexArray(0);
	}
	
	public void initVAO(int[] attributes, int vertexSize) {
		int nAtt = attributes.length / 3;
		nFloats = vertexSize / 4;
		float[] buffer = toArray(nFloats);
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		if (buffer.length > 0)
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
	
	public void updateVAO(float[] buffer) {
		if (vaoId ==  NULL)
			throw new RuntimeException("VAO not initialized!");
		glBindVertexArray(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
	}
	
	public void updateVAO() {
		updateVAO(toArray(nFloats));
	}
	
	public void updateVAO(int n, int nFloats) {
		updateVAO(toBuffer(n, nFloats));
	}
	
	public void updateVAO(int n, int[] attributes) {
		int vertexSize = 0;
		int nAtt = attributes.length / 3;
		for (int i = 0; i < nAtt; i++) {
			int typeSize = attributes[3*i+1];
			int na = attributes[3*i+2];
			vertexSize += typeSize * na;
		}
		int nFloats = vertexSize / 4;
		updateVAO(n, nFloats);
	}
	
	public void dispose() {
		glDeleteBuffers(vboId);
		glDeleteVertexArrays(vaoId);
	}
	
	// }}

	//////////////////////////////////////////////////
	// {{ Static methods

	public static VertexArray quad(float x, float y, float w, float h) {
		VertexArray array = new VertexArray(4);
		array.setPositions(new float[] { x, x+w, x+w, x }, new float[] { y, y, y+h, y+h});
		array.setUVs(new float[] { 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.0f, 1.0f });
		return array;
	}
	
	// }}

}
