package batching;

public class Quad {
	
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
		return new Quad(path, x, y, width, height);
	}
	
}
