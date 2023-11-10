package rendering;

public class Vertex {

	public static final int totalSize = 11;
	public float x = 0, y = 0;
	public float u = 0, v = 0;
	public float r = 1, g = 1, b = 1, a = 1;
	public float h = 0, s = 1, l = 1;

	public void setPosition(float xx, float yy) {
		x = xx; y = yy;
	}
	
	public void setRGBA(float rr, float gg, float bb, float aa) {
		r = rr; g = gg; b = bb; a = aa;
	}
	
	public void setHSV(float hh, float ss, float vv) {
		h = hh; s = ss; l = vv;
	}
	
	public void setUV(float uu, float vv) {
		u = uu; v = vv;
	}
	
	public void set(float[] array, int pos, int nFloats) {
		if (nFloats >= 2) {
			x = array[pos];
			y = array[pos + 1];
		}
		if (nFloats >= 4) {
			u = array[pos + 2];
			v = array[pos + 3];
		}
		if (nFloats >= 8) {
			r = array[pos + 4];
			g = array[pos + 5];
			b = array[pos + 6];
			a = array[pos + 7];
		}
		if (nFloats >= 11) {
			h = array[pos + 8];
			s = array[pos + 9];
			l = array[pos + 10];
		}
	}
	
	public void put(float[] array, int pos, int nFloats) {
		if (nFloats >= 2) {
			array[pos] = x;
			array[pos + 1] = y;
		}
		if (nFloats >= 4) {
			array[pos + 2] = u;
			array[pos + 3] = v;
		}
		if (nFloats >= 8) {
			array[pos + 4] = r;
			array[pos + 5] = g;
			array[pos + 6] = b;
			array[pos + 7] = a;
		}
		if (nFloats >= 11) {
			array[pos + 8] = h;
			array[pos + 9] = s;
			array[pos + 10] = l;
		}
	}
	
	public float[] toArray() {
		return new float[] {
			x, y, u, v, r, g, b, a, h, s, l
		};
	}

}