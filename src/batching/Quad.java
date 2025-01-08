package batching;

public class Quad implements Cloneable {
	
	// Texture
	public String path = "";
	
	// Quad
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	
	public Quad() {}
	
	public Quad(String path, int x, int y, int width, int height) {
		this.path = path;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Quad clone() {
		try {
			Quad quad = (Quad) super.clone();
			quad.x = x;
			quad.y = y;
			quad.path = path;
			quad.width = width;
			quad.height = height;
			return quad;
		} catch (CloneNotSupportedException e) {
			return new Quad(path, x, y, width, height);
		}
	}
	
}
