package batching;

public class Batch {
	
	public Obj[] objects;
	public String texturePath;
	
	public Batch(int n, String path) {
		objects = new Obj[n];
		texturePath = path;
	}
	
	public int getSize() {
		return objects.length;
	}
	
	public float[] getQuadVertices(int width, int height) {
		return getQuadVertices(width, height, 8);
	}
	
	public float[] getQuadVertices(int width, int height, int nFloats) {
		float[] vertices = new float[objects.length * 4 * nFloats];
		for (int i = 0; i < objects.length; i++) {
			float[] objVertices = objects[i].getQuadVertices(width, height, nFloats);
			for (int k = 0; k < objVertices.length; k++)
				vertices[i * objVertices.length + k] = objVertices[k];
		}
		return vertices;
	}
	
	public float[] getTriangleVertices(int width, int height) {
		return getTriangleVertices(width, height, 8);
	}
	
	public float[] getTriangleVertices(int width, int height, int nFloats) {
		float[] vertices = new float[objects.length * 6 * nFloats];
		for (int i = 0; i < objects.length; i++) {
			float[] objVertices = objects[i].getTriangleVertices(width, height, nFloats);
			for (int k = 0; k < objVertices.length; k++)
				vertices[i * objVertices.length + k] = objVertices[k];
		}
		return vertices;
	}
	
}