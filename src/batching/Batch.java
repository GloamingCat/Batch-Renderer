package batching;

public class Batch {
	
	public Obj[] objects;
	public String texturePath;
	
	public Batch(int n, String path) {
		objects = new Obj[n];
		texturePath = path;
	}
	
	public float[] getVertices(int width, int height) {
		float[] vertices = new float[objects.length * 6 * 8];
		for (int i = 0; i < objects.length; i++) {
			float[] objVertices = objects[i].getTriangleVertices(width, height);
			for (int k = 0; k < objVertices.length; k++)
				vertices[i * objVertices.length + k] = objVertices[k];
		}
		return vertices;
	}
	
}