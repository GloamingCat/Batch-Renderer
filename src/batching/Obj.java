package batching;

class Obj {
	Quad quad;
	Transform transform;
	float x, y;
	float width, height;
	
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
				0, 0, 			quad.x, quad.y + quad.height, 				// bottom left
				width, 0, 		quad.x + quad.width, quad.y + quad.height, 	// bottom right
				width, height, 	quad.x + quad.width, quad.y, 				// top right
				0, height, 		quad.x, quad.y 							// top left
		};
		for (int i = 0; i < p.length; i += 4) {
			// Apply offset
			p[i] -= transform.offsetX * 1.0f / quad.width;
			p[i + 1] -= transform.offsetY * 1.0f / quad.height;
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
	
	public float[] getQuadVertices(float texWidth, float texHeight) {
		float[] p = getTransformedVertices();
		float[] quadVertices = new float[4 * 8];
		int q_offset = 0;
		for (int v_offset = 0; v_offset < p.length; v_offset += 4) {
			// Position
			quadVertices[q_offset] = p[v_offset] + x;
			quadVertices[q_offset + 1] = p[v_offset + 1] + y;
			// Color
			// TODO: HSV
			quadVertices[q_offset + 2] = transform.red / 255f;
			quadVertices[q_offset + 3] = transform.green / 255f;
			quadVertices[q_offset + 4] = transform.blue / 255;
			quadVertices[q_offset + 5] = transform.alpha / 255f;
			// Texture
			quadVertices[q_offset + 6] = p[v_offset + 2] / texWidth;
			quadVertices[q_offset + 7] = 1 - p[v_offset + 3] / texHeight;
			q_offset += 8;
		}
		return quadVertices;
	}
	
	public float[] getTriangleVertices(int texWidth, int texHeight) {
		float[] quadVertices = getQuadVertices(texWidth, texHeight);
		float[] triangleVertices = new float[6 * 8];
		
		int[] indexes = new int[] {0, 1, 2, 0, 2, 3};
		int tv_offset = 0; // offset to triangle vertex
		for (int q_offset = 0; q_offset < quadVertices.length; q_offset += 4 * 8) { // for each quad starting index q (4 vertices, 8 properties/vertex)
			for (int i = 0; i < 6; i++) { // for each vertex starting index
				int v_offset = indexes[i] * 8;
				// Copy each property from quads[q + v] to triangles[t]
				for (int p = 0; p < 8; p++)
					triangleVertices[tv_offset + p] = quadVertices[q_offset + v_offset + p];
				tv_offset += 8;
			}
		}
		
		return triangleVertices;
	}
	
}
