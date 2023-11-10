package batching;

public class Obj {
	
	public final Quad quad;
	public final Transform transform;
	public final float x, y;
	public final float width, height;
	
	public Obj(Quad quad, Transform transform, float x, float y, float width, float height) {
		this.quad = quad;
		this.transform = transform;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public float[] getTransformedVertices() {
		float[] p = new float[] {
				0, height, 			quad.x, quad.y + quad.height, 				// bottom left
				width, height, 		quad.x + quad.width, quad.y + quad.height, 	// bottom right
				width, 0, 	quad.x + quad.width, quad.y, 				// top right
				0, 0, 		quad.x, quad.y 							// top left
		};
		for (int i = 0; i < p.length; i += 4) {
			// Apply offset
			p[i] -= transform.offsetX;
			p[i + 1] -= transform.offsetY;
			// Apply scale
			p[i] *= transform.scaleX / 100f;
			p[i + 1] *= transform.scaleY / 100f;
			// Apply rotation
			double c = Math.cos(transform.rotation);
			double s = Math.sin(transform.rotation);
			double x = c * p[i] - s * p[i + 1];
			double y = s * p[i] + c * p[i + 1];
			p[i] = (float) x;
			p[i + 1] = (float) y;
		}
		return p;
	}
	
	public float[] getQuadVertices(float texWidth, float texHeight, int nFloats) {
		float[] v = getTransformedVertices();
		float[] quadVertices = new float[4 * nFloats];
		int q_offset = 0;
		for (int v_offset = 0; v_offset < v.length; v_offset += 4) {
			// Position
			quadVertices[q_offset] = v[v_offset] + x;
			quadVertices[q_offset + 1] = v[v_offset + 1] + y;
			try {
				// Texture
				quadVertices[q_offset + 2] = v[v_offset + 2] / texWidth;
				quadVertices[q_offset + 3] = v[v_offset + 3] / texHeight;
				// Color
				quadVertices[q_offset + 4] = transform.red / 255f;
				quadVertices[q_offset + 5] = transform.green / 255f;
				quadVertices[q_offset + 6] = transform.blue / 255f;
				quadVertices[q_offset + 7] = transform.alpha / 255f;
				// HSV
				quadVertices[q_offset + 8] = transform.hue;
				quadVertices[q_offset + 9] = transform.saturation / 100f;
				quadVertices[q_offset + 10] = transform.brightness / 100f;			
			} catch (IndexOutOfBoundsException e) {}
			q_offset += nFloats;
		}
		return quadVertices;
	}
	
	public float[] getTriangleVertices(int texWidth, int texHeight, int nFloats) {
		float[] quadVertices = getQuadVertices(texWidth, texHeight, nFloats);
		float[] triangleVertices = new float[6 * nFloats];
		
		int[] indexes = new int[] {0, 1, 2, 0, 2, 3};
		int tv_offset = 0; // offset to triangle vertex
		for (int q_offset = 0; q_offset < quadVertices.length; q_offset += 4 * nFloats) { // for each quad starting index q (4 vertices, 8 properties/vertex)
			for (int i = 0; i < 6; i++) { // for each vertex starting index
				int v_offset = indexes[i] * nFloats;
				// Copy each property from quads[q + v] to triangles[t]
				for (int p = 0; p < nFloats; p++)
					triangleVertices[tv_offset + p] = quadVertices[q_offset + v_offset + p];
				tv_offset += nFloats;
			}
		}
		
		return triangleVertices;
	}
	
	public float[] getVerticesX() {
		return new float[] { 0, width, width, 0 };
	}
	
	public float[] getVerticesY() {
		return new float[] { 0,	0, height, height };
	}
	
	public float[] getTextureX() {
		return new float[] { quad.x, quad.x + quad.width, quad.x + quad.width, quad.x };
	}
	
	public float[] getTextureY() {
		return new float[] { quad.y + quad.height, quad.y + quad.height, quad.y, quad.y };
	}
	
}
