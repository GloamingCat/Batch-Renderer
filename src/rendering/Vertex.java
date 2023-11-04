package rendering;

public class Vertex {

	public static final int totalSize = 11;
	public float x, y;
	public float u, v;
	public float r, g, b, a;
	public float h, s, l;

	public void setPosition(float xx, float yy) {
		x = xx; y = yy;
	}
	
	public void setRGBA(float rr, float gg, float bb, float aa) {
		r =rr; g = gg; b = bb; a = aa;
	}
	
	public void setHSV(float hh, float ss, float vv) {
		h = hh; s = ss; l = vv;
	}
	
	public void setUV(float uu, float vv) {
		u = uu; v = vv;
	}
	
	public void set(float[] array, int pos) {
		try {
			x = array[pos];
			y = array[pos + 1];
			u = array[pos + 2];
			v = array[pos + 3];
			r = array[pos + 4];
			g = array[pos + 5];
			b = array[pos + 6];
			a = array[pos + 7];
			h = array[pos + 8];
			s = array[pos + 9];
			l = array[pos + 10];
		} catch (IndexOutOfBoundsException e) {}
	}
	
	public void put(float[] array, int pos) {
		try {
			array[pos] = x;
			array[pos + 1] = y;
			array[pos + 2] = u;
			array[pos + 3] = v;
			array[pos + 4] = r;
			array[pos + 5] = g;
			array[pos + 6] = b;
			array[pos + 7] = a;
			array[pos + 8] = h;
			array[pos + 9] = s;
			array[pos + 10] = l;
		} catch (IndexOutOfBoundsException e) {}
	}
	
	public float[] toArray() {
		return new float[] {
			x, y, u, v, r, g, b, a, h, s, l
		};
	}

}